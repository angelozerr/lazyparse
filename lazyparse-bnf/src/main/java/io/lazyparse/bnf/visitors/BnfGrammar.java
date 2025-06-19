package io.lazyparse.bnf.visitors;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.lazyparse.bnf.BnfParser;
import io.lazyparse.bnf.ExpandedPattern;
import io.lazyparse.bnf.BnfParser.Alternative;
import io.lazyparse.bnf.BnfParser.Annotation;
import io.lazyparse.bnf.BnfParser.Expression;
import io.lazyparse.bnf.BnfParser.Identifier;
import io.lazyparse.bnf.BnfParser.Literal;
import io.lazyparse.bnf.BnfParser.Repetition;
import io.lazyparse.bnf.BnfParser.Rule;
import io.lazyparse.bnf.BnfParser.Sequence;

public class BnfGrammar {

	private final List<BnfParser.Rule> rules;
	private final Map<String, BnfParser.Rule> ruleMap;
	private final Map<String, ExpandedPattern> expandedPatterns;

	public BnfGrammar(List<BnfParser.Rule> rules) {
		this.rules = rules;
		this.ruleMap = new LinkedHashMap<>();
		for (BnfParser.Rule r : rules) {
			ruleMap.put(r.name, r);
		}
		RuleResolvingVisitor resolver = new RuleResolvingVisitor(ruleMap);
		List<BnfParser.Rule> resolvedRules = resolver.resolveAllRules();
		this.expandedPatterns = createExpandedPatterns(rules);
	}

	public List<BnfParser.Rule> getRules() {
		return rules;
	}

	public BnfParser.Rule getRule(String name) {
		return ruleMap.get(name);
	}

	public String getInitialState() {
		return "TODO";
	}

	public Set<String> getStates() {
		StateExtractorVisitor visitor = new StateExtractorVisitor();
		for (BnfParser.Rule rule : rules) {
			rule.expression.accept(visitor);
		}
		return visitor.getStates();
	}

	public Set<String> getTokens() {
		TokenCollectorVisitor visitor = new TokenCollectorVisitor();
		for (BnfParser.Rule rule : rules) {
			rule.expression.accept(visitor);
		}
		return visitor.getTokens();
	}

	private Map<String, ExpandedPattern> createExpandedPatterns(List<BnfParser.Rule> rules) {
		Map<String, ExpandedPattern> resolved = new HashMap<>();
		ExpandedPatternBuilderVisitor visitor = new ExpandedPatternBuilderVisitor(this, resolved);
		for (BnfParser.Rule rule : rules) {
			rule.expression.accept(visitor);
		}
		return resolved;
	}

	public ExpandedPattern getRulesByWhithinState(String state) {
		return expandedPatterns.getOrDefault(state, new ExpandedPattern(null, null, null, false));
	}
}
