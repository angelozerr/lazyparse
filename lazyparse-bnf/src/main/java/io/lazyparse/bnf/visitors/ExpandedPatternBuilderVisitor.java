package io.lazyparse.bnf.visitors;

import java.util.Map;

import io.lazyparse.bnf.BnfParser;
import io.lazyparse.bnf.BnfParser.Annotation;
import io.lazyparse.bnf.BnfParser.Sequence;
import io.lazyparse.bnf.ExpandedPattern;

public class ExpandedPatternBuilderVisitor implements BnfExpressionVisitor<Void> {
	private final BnfGrammar grammar;
	private final Map<String, ExpandedPattern> patterns;
	private final ExpandedPatternContext context = new ExpandedPatternContext();

	public ExpandedPatternBuilderVisitor(BnfGrammar grammar, Map<String, ExpandedPattern> patterns) {
		this.grammar = grammar;
		this.patterns = patterns;
	}

	@Override
	public Void visitIdentifier(BnfParser.Identifier identifier) {
		BnfParser.Rule rule = grammar.getRule(identifier.name);
		if (rule != null) {
			rule.expression.accept(this);
		}
		return null;
	}

	@Override
	public Void visitLiteral(BnfParser.Literal literal) {
		context.pattern = literal.value;
		var parent = literal.getParent();
		if (parent instanceof Sequence seq) {
			int nextStateIndex = seq.elements.indexOf(literal) + 1;
			if (seq.elements.size() > nextStateIndex) {
				var expr = seq.elements.get(nextStateIndex);
				if (expr instanceof Annotation ann) {
					if ( ann.name.equals("@pushState")) {
						context.nextState = ann.getValue();
					} else if ( ann.name.equals("@popState")) {
						context.nextState = "WithinContent";
					}
				}
			}

		}
		addIfComplete();
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
		switch (annotation.name) {
		case "@token" -> {
			String value = annotation.getValue();
			if (value != null) {
				context.tokenName = value;
			}
		}
		case "@recoverUntil" -> {
			String value = annotation.getValue();
			if (value != null) {
				context.recoverUntil = true;
				context.pattern = value;
				addIfComplete();
			}
		}
		case "@whenState", "@pushState" -> {
			String state = annotation.getValue();
			if (state != null) {
				if (annotation.name.equals("@pushState")) {
					context.nextState = state;
				}
				context.expandedPattern = patterns.computeIfAbsent(state,
						k -> new ExpandedPattern(null, null, null, false));
			}
		}
		}
		// if (annotation.expr != null) annotation.expr.accept(this);
		return null;
	}

	private void addIfComplete() {
		if (context.expandedPattern != null && context.pattern != null) {
			context.expandedPattern.addPattern(context.pattern, context.nextState, context.tokenName,
					context.recoverUntil);
			context.reset();
		}
	}
}

class ExpandedPatternContext {
	public String pattern = null;
	public String tokenName = null;
	public boolean recoverUntil = false;
	public ExpandedPattern expandedPattern;
	public String nextState;

	public void reset() {
		pattern = null;
		tokenName = null;
		nextState = null;
		recoverUntil = false;
	}
}