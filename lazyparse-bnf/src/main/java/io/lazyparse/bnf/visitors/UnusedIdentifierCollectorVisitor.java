package io.lazyparse.bnf.visitors;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.lazyparse.bnf.BnfParser.Alternative;
import io.lazyparse.bnf.BnfParser.Annotation;
import io.lazyparse.bnf.BnfParser.Identifier;
import io.lazyparse.bnf.BnfParser.Literal;
import io.lazyparse.bnf.BnfParser.Repetition;
import io.lazyparse.bnf.BnfParser.Rule;
import io.lazyparse.bnf.BnfParser.Sequence;

public class UnusedIdentifierCollectorVisitor {

	public static Set<String> findUnusedIdentifiers(List<Rule> rules) {
		Set<String> defined = new HashSet<>();
		Set<String> referenced = new HashSet<>();
		IdentifierVisitor visitor = new IdentifierVisitor(referenced);

		for (Rule rule : rules) {
			defined.add(rule.name);
			rule.expression.accept(visitor); // ✅ appel de accept(visitor)
		}

		defined.removeAll(referenced);
		return defined;
	}

	private static class IdentifierVisitor implements BnfExpressionVisitor<Void> {

		private final Set<String> referenced;

		public IdentifierVisitor(Set<String> referenced) {
			this.referenced = referenced;
		}

		@Override
		public Void visitIdentifier(Identifier identifier) {
			referenced.add(identifier.name);
			return null;
		}

		@Override
		public Void visitLiteral(Literal literal) {
			return null;
		}

		@Override
		public Void visitSequence(Sequence sequence) {
			for (var e : sequence.elements) {
				e.accept(this);
			}
			return null;
		}

		@Override
		public Void visitAlternative(Alternative alternative) {
			alternative.left.accept(this);
			alternative.right.accept(this);
			return null;
		}

		@Override
		public Void visitRepetition(Repetition repetition) {
			repetition.expr.accept(this);
			return null;
		}

		@Override
		public Void visitAnnotation(Annotation annotation) {
			if (annotation.expr != null) {
				annotation.expr.accept(this);
			}
			return null;
		}
	}
}