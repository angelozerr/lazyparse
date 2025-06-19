package io.lazyparse.generator.context;

public abstract class FileContextEntry<T> extends ContextEntry {

	private final String fileName;

	public FileContextEntry(String name, T value) {
		super(name, value, ContextEntryKind.FILE);
		this.fileName = computeFileName(value);
	}


	public String getFileName() {
		return fileName;
	}

	protected abstract String computeFileName(T value);
}
