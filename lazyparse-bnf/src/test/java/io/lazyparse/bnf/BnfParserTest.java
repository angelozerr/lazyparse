package io.lazyparse.bnf;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import io.lazyparse.bnf.BnfParser;
import io.lazyparse.bnf.BnfParser.Rule;

import java.util.List;

public class BnfParserTest {

    @Test
    public void testSimpleRepetitionStar() {
        String input = "rule ::= \"a\"*";
        BnfParser parser = new BnfParser(input);
        List<Rule> rules = parser.parse().getRules();

        assertEquals(1, rules.size());
        Rule rule = rules.get(0);
        assertEquals("rule", rule.name);

        String exprStr = rule.expression.toString();
        // On attend "a"* comme expression
        assertTrue(exprStr.contains("\"a\"*"), "Expression should contain \"a\"* but was: " + exprStr);
    }

    @Test
    public void testSequenceWithRepetition() {
        String input = "seq ::= \"a\" \"b\"* \"c\"";
        BnfParser parser = new BnfParser(input);
        List<Rule> rules = parser.parse().getRules();

        assertEquals(1, rules.size());
        Rule rule = rules.get(0);
        assertEquals("seq", rule.name);

        String exprStr = rule.expression.toString();
        // Exemple attendu : ("a" ("b")* "c")
        assertTrue(exprStr.contains("\"b\"*"), "Expression should contain \"b\"* but was: " + exprStr);
    }

    @Test
    public void testAlternativeWithRepetition() {
        String input = "alt ::= \"a\"* | \"b\"";
        BnfParser parser = new BnfParser(input);
        List<Rule> rules = parser.parse().getRules();

        assertEquals(1, rules.size());
        Rule rule = rules.get(0);
        assertEquals("alt", rule.name);

        String exprStr = rule.expression.toString();
        assertTrue(exprStr.contains("\"a\"*"), "Expression should contain \"a\"* but was: " + exprStr);
        assertTrue(exprStr.contains("|"), "Expression should contain alternative operator | but was: " + exprStr);
    }
    
    @Test
    public void testTwoRules() {
        String input =
                "rule1 ::= \"a\"*\n" +    // repetition *
                "rule2 ::= rule1 | \"b\""; // alternative |

        BnfParser parser = new BnfParser(input);
        List<Rule> rules = parser.parse().getRules();

        assertEquals(2, rules.size(), "Should parse two rules");

        Rule r1 = rules.get(0);
        Rule r2 = rules.get(1);

        assertEquals("rule1", r1.name);
        assertEquals("rule2", r2.name);

        System.out.println("Rule 1: " + r1);
        System.out.println("Rule 2: " + r2);

        // Optional: check that r1.expression.toString() contains the '*'
        assertTrue(r1.expression.toString().contains("*"), "rule1 should contain repetition *");

        // Optional: check that r2.expression.toString() contains the '|'
        assertTrue(r2.expression.toString().contains("|"), "rule2 should contain alternative |");
    }
}
