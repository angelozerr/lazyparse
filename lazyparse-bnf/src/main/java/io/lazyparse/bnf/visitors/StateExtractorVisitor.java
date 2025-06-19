package io.lazyparse.bnf.visitors;

import java.util.HashSet;
import java.util.Set;

import io.lazyparse.bnf.BnfParser;

class StateExtractorVisitor implements BnfExpressionVisitor<Void> {
	private final Set<String> states = new HashSet<>();

	public Set<String> getStates() {
		return states;
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
		for (var e : sequence.elements)
			e.accept(this);
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
		if (annotation.name.equals("@pushState") || annotation.name.equals("@whenState")) {
			String state = annotation.getValue();
			if (state != null) {
				states.add(state);
			}
		} else if (annotation.expr != null) {
			annotation.expr.accept(this);
		}
		return null;
	}
}