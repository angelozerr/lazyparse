package io.lazyparse.bnf.visitors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.lazyparse.bnf.BnfParser;

public class RuleResolvingVisitor implements BnfExpressionVisitor<BnfParser.Expression> {

	private final Map<String, BnfParser.Rule> ruleMap;
	private final Set<String> resolving = new HashSet<>();

	public RuleResolvingVisitor(Map<String, BnfParser.Rule> ruleMap) {
		this.ruleMap = ruleMap;
	}

	public List<BnfParser.Rule> resolveAllRules() {
		List<BnfParser.Rule> resolved = new ArrayList<>();
		for (Map.Entry<String, BnfParser.Rule> entry : ruleMap.entrySet()) {
			String name = entry.getKey();
			BnfParser.Expression resolvedExpr = resolve(entry.getValue().expression);
			resolved.add(new BnfParser.Rule(name, resolvedExpr));
		}
		return resolved;
	}

	private BnfParser.Expression resolve(BnfParser.Expression expr) {
		return expr.accept(this);
	}

	@Override
	public BnfParser.Expression visitIdentifier(BnfParser.Identifier identifier) {
		String name = identifier.name;
		if (resolving.contains(name)) {
			// Prevent from cycle
			return identifier;
		}
		BnfParser.Rule rule = ruleMap.get(name);
		if (rule == null) {
			return identifier;
		}
		resolving.add(name);
		BnfParser.Expression resolved = rule.expression.accept(this);
		resolving.remove(name);
		return resolved;
	}

	@Override
	public BnfParser.Expression visitLiteral(BnfParser.Literal literal) {
		return literal;
	}

	@Override
	public BnfParser.Expression visitSequence(BnfParser.Sequence seq) {
	    List<BnfParser.Expression> flattened = new ArrayList<>();

	    for (BnfParser.Expression element : seq.elements) {
	        BnfParser.Expression resolved = element.accept(this);

	        if (resolved instanceof BnfParser.Sequence nestedSeq) {
	            flattened.addAll(nestedSeq.elements); // fusion
	        } else {
	            flattened.add(resolved);
	        }
	    }

	    return new BnfParser.Sequence(flattened);
	}

	@Override
	public BnfParser.Expression visitAlternative(BnfParser.Alternative alternative) {
		return new BnfParser.Alternative(alternative.left.accept(this), alternative.right.accept(this));
	}

	@Override
	public BnfParser.Expression visitRepetition(BnfParser.Repetition repetition) {
		return new BnfParser.Repetition(repetition.expr.accept(this), repetition.type);
	}

	@Override
	public BnfParser.Expression visitAnnotation(BnfParser.Annotation annotation) {
		//BnfParser.Expression resolvedExpr = annotation.expr != null ? annotation.expr.accept(this) : null;
		//return new BnfParser.Annotation(annotation.name, resolvedExpr);
		return annotation;
	}
}
