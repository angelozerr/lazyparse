package io.lazyparse.bnf;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BnfGrammar {

	private final List<BnfParser.Rule> rules;
	private final Map<String, BnfParser.Rule> ruleMap;

	public BnfGrammar(List<BnfParser.Rule> rules) {
		this.rules = rules;
		this.ruleMap = new LinkedHashMap<>();
		for (BnfParser.Rule r : rules) {
			ruleMap.put(r.name, r);
		}
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

	public Set<String> getTokens() {
		return Collections.emptySet();
	}

	/** Extrait tous les états @state(...) trouvés dans toutes les règles */
	public Set<String> getStates() {
		Set<String> states = new LinkedHashSet<>();
		for (BnfParser.Rule r : rules) {
			extractStatesFromExpr(r.expression, states);
		}
		return states;
	}

	private void extractStatesFromExpr(Object expr, Set<String> states) {
		if (expr instanceof BnfParser.Annotation ann) {
			if ("@whenState".equals(ann.name) || "@pushState".equals(ann.name)) {
				if (ann.expr instanceof BnfParser.Identifier id) {
					states.add(id.name);
				} else if (ann.expr instanceof BnfParser.Sequence seq) {
					for (Object e : seq.elements) {
						if (e instanceof BnfParser.Identifier id) {
							states.add(id.name);
						} else {
							extractStatesFromExpr(e, states);
						}
					}
				}
			} else if (ann.expr != null) {
				extractStatesFromExpr(ann.expr, states);
			}
		} else if (expr instanceof BnfParser.Sequence seq) {
			for (Object e : seq.elements) {
				extractStatesFromExpr(e, states);
			}
		} else if (expr instanceof BnfParser.Alternative alt) {
			extractStatesFromExpr(alt.left, states);
			extractStatesFromExpr(alt.right, states);
		} else if (expr instanceof BnfParser.Repetition rep) {
			extractStatesFromExpr(rep.expr, states);
		}
	}

	/** Extrait tous les noms de règles (tokens) */
	public Set<String> extractTokens() {
		Set<String> tokens = new LinkedHashSet<>();
		for (BnfParser.Rule r : rules) {
			tokens.add(r.name.toUpperCase());
		}
		return tokens;
	}
}
