package io.lazyparse.generator.context;

public abstract class ContextEntry {
	
	public static enum ContextEntryKind {
		SIMPLE,
		FILE,
		DIRECTORY;
	}
	
	private final String name;
	private final Object value;
	private final ContextEntryKind kind;

	public ContextEntry(String name, Object value, ContextEntryKind kind) {
		this.name = name;
		this.value = value;
		this.kind = kind;
	}
	
	public String getName() {
		return name;
	}
	
	public Object getValue() {
		return value;
	}
	
	public ContextEntryKind getKind() {
		return kind;
	}
	

}
