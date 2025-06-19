package io.lazyparse.bnf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ExpandedPattern {

	private record Item(String pattern, String nextState, String tokenName, boolean recoverUntil) {

		@Override
		public int hashCode() {
			return Objects.hash(nextState, pattern, recoverUntil, tokenName);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Item other = (Item) obj;
			return Objects.equals(nextState, other.nextState) && Objects.equals(pattern, other.pattern)
					&& recoverUntil == other.recoverUntil && Objects.equals(tokenName, other.tokenName);
		}
	};

	private List<Item> items;

	private final String pattern;

	private final String nextState;

	private final String tokenName;

	private List<ExpandedPattern> children;

	private boolean recoverUntil;

	public ExpandedPattern(String pattern, String nextState, String tokenName, boolean recoverUntil) {
		this.pattern = pattern;
		this.nextState = nextState;
		this.tokenName = tokenName;
		this.recoverUntil = recoverUntil;
		this.items = new ArrayList<>();
	}

	public String getPattern() {
		return pattern;
	}

	public boolean isRecoverUntil() {
		return recoverUntil;
	}

	public char[] getPatternChars() {
		return pattern.toCharArray();
	}

	public String getNextState() {
		return nextState;
	}

	public String getTokenName() {
		return tokenName;
	}

	public List<ExpandedPattern> getChildren() {
		return children != null ? children : Collections.emptyList();
	}

	public List<ExpandedPattern> expand() {
		List<ExpandedPattern> expanded = new ArrayList<ExpandedPattern>();
		Collections.sort(items, (i1, i2) -> {
			if (i1.pattern == null || i2.pattern == null) {
				return 0;
			}
			return i1.pattern.length() - i2.pattern.length();
		});
		List<Item> done = new ArrayList<>();
		for (var item : items) {
			if (!done.contains(item)) {
				ExpandedPattern expandedPattern = null;
				String pattern = item.pattern();
				String match = pattern;
				if (pattern != null) {

					if (!item.recoverUntil) {
						for (int i = 0; i < pattern.length(); i++) {
							match = pattern.substring(0, pattern.length() - i);

							for (var child : items) {
								if (!child.equals(item) && !child.recoverUntil) {
									if (child.pattern != null && child.pattern.startsWith(match)) {
										if (expandedPattern == null || !expandedPattern.getPattern().equals(match)) {
											expandedPattern = new ExpandedPattern(match, null, null, false);
											expanded.add(expandedPattern);
										}
										expandedPattern.addPattern(
												child.pattern.substring(match.length(), child.pattern.length()),
												child.nextState, child.tokenName, child.recoverUntil);
										done.add(child);
									}
								}
							}

						}
					}
					
					if (expandedPattern == null) {
						expandedPattern = new ExpandedPattern(item.pattern, item.nextState, item.tokenName,
								item.recoverUntil);
						expanded.add(expandedPattern);
					} else {
						expandedPattern.addPattern(item.pattern.substring(match.length(), item.pattern.length()),
								item.nextState, item.tokenName, item.recoverUntil);
					}
				}
			}
		}
		Collections.sort(expanded, (i1, i2) -> {
			if (i1.recoverUntil) {
				return 1;
			}
			if (i2.recoverUntil) {
				return -1;
			}
			if (i1.pattern == null || i2.pattern == null) {
				return 0;
			}
			return i2.pattern.length() - i1.pattern.length();
		});
		return expanded;
	}

	public void addPattern(String pattern, String nextState, String tokenName, boolean recoverUntil) {
		var item = new Item(pattern, nextState, tokenName, recoverUntil);
		if (!items.contains(item)) {
			items.add(item);
		}
	}

}
