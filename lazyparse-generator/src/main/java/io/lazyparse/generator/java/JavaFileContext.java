package io.lazyparse.generator.java;

import io.lazyparse.generator.context.Context;

public class JavaFileContext extends Context {

	private final String packageName;
	private final String className;

	public JavaFileContext(String packageName, String className) {		
		super(new JavaPackageContextEntry(packageName), new JavaFileContextEntry(className));
		this.packageName = packageName;
		this.className = className;
	}
	
	public String getPackageName() {
		return packageName;
	}
	
	public String getClassName() {
		return className;
	}

}
