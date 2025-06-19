package io.lazyparse.bnf;

import java.util.ArrayList;
import java.util.List;

import io.lazyparse.bnf.CharBnfLexer.Token;
import io.lazyparse.bnf.CharBnfLexer.TokenType;
import io.lazyparse.bnf.visitors.BnfExpressionVisitor;
import io.lazyparse.bnf.visitors.BnfGrammar;

public class BnfParser {

	private final List<CharBnfLexer.Token> tokens;
	private int pos = 0;

	public BnfParser(String input) {
		CharBnfLexer lexer = new CharBnfLexer();
		this.tokens = lexer.tokenize(input);
	}

	private CharBnfLexer.Token peek() {
		if (pos < tokens.size()) {
			return tokens.get(pos);
		}
		return new CharBnfLexer.Token(CharBnfLexer.TokenType.EOF, "", pos);
	}

	private Token peek(int offset) {
		int index = pos + offset;
		if (index >= tokens.size()) {
			return new Token(TokenType.EOF, "", -1); // or however you define EOF
		}
		return tokens.get(index);
	}

	private CharBnfLexer.Token consume() {
		CharBnfLexer.Token t = peek();
		pos++;
		return t;
	}

	private void expect(CharBnfLexer.TokenType expected) {
		CharBnfLexer.Token t = peek();
		if (t.type != expected) {
			throw new RuntimeException("Expected " + expected + " but found " + t.type + " at position " + t.position);
		}
		consume();
	}

	public BnfGrammar parse() {
		return new BnfGrammar(parseRules());
	}

	private List<Rule> parseRules() {
		List<Rule> rules = new ArrayList<>();
		while (peek().type != CharBnfLexer.TokenType.EOF) {
			rules.add(parseRule());
		}
		return rules;
	}

	private Rule parseRule() {
		System.out.println(
				"Parsing rule at token " + peek().type + "('" + peek().text + "') at position " + peek().position);

		CharBnfLexer.Token nameToken = consume();
		if (nameToken.type != CharBnfLexer.TokenType.IDENTIFIER) {
			throw new RuntimeException(
					"Expected IDENTIFIER but found " + nameToken.type + " at position " + nameToken.position);
		}
		String ruleName = nameToken.text;

		expect(CharBnfLexer.TokenType.COLON_COLON_EQUALS);

		Expression expr = parseExpression();

		return new Rule(ruleName, expr);
	}

	private Expression parseExpression() {
		Expression left = parseSequence();
		while (peek().type == CharBnfLexer.TokenType.PIPE) {
			consume();
			Expression right = parseSequence();
			left = new Alternative(left, right);
		}
		return left;
	}

	private Expression parseSequence() {
		List<Expression> elements = new ArrayList<>();
		elements.add(parsePostfix());

		while (true) {
			Token current = peek();

			// Stop if this token starts a new rule (e.g., IDENTIFIER followed by ::=)
			if (current.type == TokenType.IDENTIFIER) {
				Token next = peek(1);
				if (next.type == TokenType.COLON_COLON_EQUALS) {
					break;
				}
			}

			// Stop if it's an operator or end of rule
			if (current.type == TokenType.PIPE || current.type == TokenType.EOF
					|| current.type == TokenType.COLON_COLON_EQUALS || current.type == TokenType.RPAREN) {
				break;
			}

			elements.add(parsePostfix());
		}

		if (elements.size() == 1) {
			return elements.get(0);
		}
		return new Sequence(elements);
	}

	private Expression parsePostfix() {
		// Parse the primary expression (IDENTIFIER, literal, group, etc.)
		Expression primary = parsePrimary();

		// Check for postfix operators: '*', '+', '?'
		// These modify the cardinality of the preceding expression
		while (true) {
			Token t = peek();
			if (t.type == TokenType.STAR || t.type == TokenType.PLUS || t.type == TokenType.QUESTION) {
				// Consume the postfix token
				consume();
				// Wrap the primary expression with the appropriate repetition type
				switch (t.type) {
				case STAR:
					primary = new Repetition(primary, Repetition.Type.ZERO_OR_MORE);
					break;
				case PLUS:
					primary = new Repetition(primary, Repetition.Type.ONE_OR_MORE);
					break;
				case QUESTION:
					primary = new Repetition(primary, Repetition.Type.OPTIONAL);
					break;
				default:
					throw new RuntimeException("Unexpected postfix operator: " + t.type);
				}
			} else {
				// No more postfix operators, return the final expression
				break;
			}
		}

		return primary;
	}

	private Expression parsePrimary() {
		CharBnfLexer.Token t = peek();

		// Gestion des annotations @xxx
		if (t.type == CharBnfLexer.TokenType.ANNOTATION) {
			consume(); // consommer le '@'

			/*
			 * CharBnfLexer.Token annotationNameToken = consume(); if
			 * (annotationNameToken.type != CharBnfLexer.TokenType.IDENTIFIER) { throw new
			 * RuntimeException("Expected IDENTIFIER after '@' but found " +
			 * annotationNameToken.type); }
			 */

			String annotationName = t.text;

			// Vérifier s'il y a un argument entre parenthèses : e.g. @recoverUntil(...)
			Expression argument = null;
			if (peek().type == CharBnfLexer.TokenType.LPAREN) {
				consume(); // consommer '('
				argument = parseExpression(); // on parse une expression pour l'argument
				if (peek().type != CharBnfLexer.TokenType.RPAREN) {
					throw new RuntimeException("Expected ')' after annotation argument");
				}
				consume(); // consommer ')'
			}

			// Expression expr = parsePrimary();

			if (argument != null) {
				List<Expression> elems = new ArrayList<>();
				elems.add(argument);
				// elems.add(expr);
				return new Annotation(annotationName, argument);
			} else {
				return new Annotation(annotationName, null);
			}
		}

		// Sinon, traitement classique
		switch (t.type) {
		case IDENTIFIER:
			return new Identifier(consume().text);

		case STRING_LITERAL:
			return new Literal(consume().text);

		case LPAREN:
			consume();
			Expression inner = parseExpression();
			if (peek().type != CharBnfLexer.TokenType.RPAREN) {
				throw new RuntimeException("Expected ')' but found " + peek().text + " at position " + peek().position);
			}
			consume();
			return inner;

		default:
			throw new RuntimeException("Unexpected token " + t.type + " (" + t.text + ") at position " + t.position);
		}
	}

	// AST classes

	public static class Rule {
		public final String name;
		public final Expression expression;

		public Rule(String name, Expression expression) {
			this.name = name;
			this.expression = expression;
		}

		@Override
		public String toString() {
			return name + " ::= " + expression;
		}
	}

	public interface Expression {

		Expression getParent();

		<R> R accept(BnfExpressionVisitor<R> visitor);

	}

	static abstract class ExpressionBase implements Expression {

		private Expression parent;

		@Override
		public Expression getParent() {
			return parent;
		}

		public void setParent(Expression parent) {
			this.parent = parent;
		}
	}

	public static class Identifier extends ExpressionBase {
		public final String name;

		public Identifier(String name) {
			this.name = name;
		}

		@Override
		public <R> R accept(BnfExpressionVisitor<R> visitor) {
			return visitor.visitIdentifier(this);
		}

		public String toString() {
			return name;
		}
	}

	public static class Literal extends ExpressionBase {
		public final String value;

		public Literal(String value) {
			this.value = value;
		}

		public String toString() {
			return "\"" + value + "\"";
		}

		@Override
		public <R> R accept(BnfExpressionVisitor<R> visitor) {
			return visitor.visitLiteral(this);
		}
	}

	public static class Sequence extends ExpressionBase {
		public final List<Expression> elements;

		public Sequence(List<Expression> elements) {
			this.elements = elements;
			elements.forEach(elt -> ((ExpressionBase) elt).setParent(this));
		}

		public String toString() {
			StringBuilder sb = new StringBuilder("(");
			for (int i = 0; i < elements.size(); i++) {
				if (i > 0)
					sb.append(" ");
				sb.append(elements.get(i).toString());
			}
			sb.append(")");
			return sb.toString();
		}

		@Override
		public <R> R accept(BnfExpressionVisitor<R> visitor) {
			return visitor.visitSequence(this);
		}
	}

	public static class Alternative extends ExpressionBase {
		public final Expression left;
		public final Expression right;

		public Alternative(Expression left, Expression right) {
			this.left = left;
			this.right = right;
			if (left != null) {
				((ExpressionBase)left).setParent(this);
			}
			if (right != null) {
				((ExpressionBase)right).setParent(this);
			}
		}

		public String toString() {
			return "(" + left + " | " + right + ")";
		}

		@Override
		public <R> R accept(BnfExpressionVisitor<R> visitor) {
			return visitor.visitAlternative(this);
		}
	}

	public static class Repetition extends ExpressionBase {
		public enum Type {
			ZERO_OR_MORE, ONE_OR_MORE, OPTIONAL
		}

		public final Expression expr;
		public final Type type;

		public Repetition(Expression expr, Type type) {
			this.expr = expr;
			this.type = type;
			if (expr != null) {
				((ExpressionBase)expr).setParent(this);
			}
		}

		public String toString() {
			switch (type) {
			case ZERO_OR_MORE:
				return expr + "*";
			case ONE_OR_MORE:
				return expr + "+";
			case OPTIONAL:
				return expr + "?";
			default:
				return expr.toString();
			}
		}

		@Override
		public <R> R accept(BnfExpressionVisitor<R> visitor) {
			return visitor.visitRepetition(this);
		}
	}

	public static class Annotation extends ExpressionBase {
		public final String name;
		public final Expression expr;

		public Annotation(String name, Expression expr) {
			this.name = name;
			this.expr = expr;
			if (expr != null) {
				((ExpressionBase)expr).setParent(this);
			}
		}

		public String toString() {
			return name + " " + expr;
		}

		public String getValue() {
			if (expr instanceof Identifier id) {
				return id.name;
			}
			if (expr instanceof Literal literal) {
				return literal.value;
			}
			if (expr instanceof BnfParser.Sequence seq && seq.elements.get(0) instanceof BnfParser.Identifier id) {
				return id.name;
			}
			return null;
		}

		@Override
		public <R> R accept(BnfExpressionVisitor<R> visitor) {
			return visitor.visitAnnotation(this);
		}
	}

}
