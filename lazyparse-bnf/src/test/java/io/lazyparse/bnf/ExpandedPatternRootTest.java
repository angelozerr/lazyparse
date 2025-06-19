package io.lazyparse.bnf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class ExpandedPatternRootTest {

	@Test
	public void testQute() {
		ExpandedPattern pattern = new ExpandedPattern(null, null, null, false);
		pattern.addPattern("{!", "WithinComment", "StartComment", false);
		pattern.addPattern("{", "WithinExpression", "StartExpression", false);
		List<ExpandedPattern> expanded = pattern.expand();
		assertEquals(1, expanded.size());

		ExpandedPattern root = expanded.get(0);
		assertEquals("{", root.getPattern());

		List<ExpandedPattern> childExpanded = root.expand();
		assertEquals(2, childExpanded.size());

		ExpandedPattern child0 = childExpanded.get(0);
		assertEquals("!", child0.getPattern());
		assertEquals("WithinComment", child0.getNextState());
		assertEquals("StartComment", child0.getTokenName());

		ExpandedPattern child1 = childExpanded.get(1);
		assertEquals("", child1.getPattern());
		assertEquals("WithinExpression", child1.getNextState());
		assertEquals("StartExpression", child1.getTokenName());

		assertTrue(child1.expand().isEmpty());
	}
}
