package io.lazyparse.generator.context;

import java.util.ArrayList;
import java.util.List;

public class Context {

	private final List<ContextEntry> entries;
	
	private FileContextEntry<?> file;
	private DirectoryContextEntry<?> dir;

	public Context(DirectoryContextEntry<?> dir, FileContextEntry<?> file) {
		this.entries = new ArrayList<ContextEntry>();
		this.file = file;
		this.dir = dir;
		put(dir);
		put(file);
	}

	public void put(String name, Object value) {
		put(new SimpleContexEntry(name, value));
	}

	public void put(ContextEntry contextEntry) {
		entries.add(contextEntry);
	}
	
	public List<ContextEntry> getEntries() {
		return entries;
	}

	public String getDir() {
		return dir.getDir();
	}

	public String getFileName() {
		return file.getFileName();
	}
	
}
