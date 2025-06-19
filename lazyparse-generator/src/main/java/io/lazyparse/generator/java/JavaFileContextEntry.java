package io.lazyparse.generator.java;

import io.lazyparse.generator.context.FileContextEntry;

public class JavaFileContextEntry extends FileContextEntry<String> {

	public JavaFileContextEntry(String className) {
		super("className", className);
	}

	@Override
	protected String computeFileName(String className) {
		return className + ".java";
	}

}
