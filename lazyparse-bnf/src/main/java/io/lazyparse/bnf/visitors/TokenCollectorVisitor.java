package io.lazyparse.bnf.visitors;

import java.util.HashSet;
import java.util.Set;

import io.lazyparse.bnf.BnfParser;

public class TokenCollectorVisitor implements BnfExpressionVisitor<Void> {
	private final Set<String> tokens = new HashSet<>();

	public Set<String> getTokens() {
		return tokens;
	}

	@Override
	public Void visitIdentifier(BnfParser.Identifier identifier) {
		return null;
	}

	@Override
	public Void visitLiteral(BnfParser.Literal literal) {
		return null;
	}

	@Override
	public Void visitSequence(BnfParser.Sequence sequence) {
		for (var e : sequence.elements) {
			e.accept(this);
		}
		return null;
	}

	@Override
	public Void visitAlternative(BnfParser.Alternative alt) {
		alt.left.accept(this);
		alt.right.accept(this);
		return null;
	}

	@Override
	public Void visitRepetition(BnfParser.Repetition repetition) {
		repetition.expr.accept(this);
		return null;
	}

	@Override
	public Void visitAnnotation(BnfParser.Annotation annotation) {
		if (annotation.name.equals("@token")) {
			String value = annotation.getValue();
			if (value != null) {
				tokens.add(value);
			}
		} else if (annotation.expr != null) {
			annotation.expr.accept(this);
		}
		return null;
	}
}