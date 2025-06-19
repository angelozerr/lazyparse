package io.lazyparse.generator.context;

public abstract class DirectoryContextEntry<T> extends ContextEntry {

	private final String dir;

	public DirectoryContextEntry(String name, T value) {
		super(name, value, ContextEntryKind.DIRECTORY);
		this.dir = computeDir(value);
	}

	public String getDir() {
		return dir;
	}

	protected abstract String computeDir(T value);
}
