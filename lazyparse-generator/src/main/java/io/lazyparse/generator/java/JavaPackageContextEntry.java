package io.lazyparse.generator.java;

import io.lazyparse.generator.context.DirectoryContextEntry;

public class JavaPackageContextEntry extends DirectoryContextEntry<String>{

	public JavaPackageContextEntry(String packageName) {
		super("packageName", packageName);
	}

	@Override
	protected String computeDir(String packageName) {
		return packageName.replace(".", "/");
	}

}
