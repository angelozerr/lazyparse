package io.lazyparse.generator;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import io.quarkus.qute.TemplateLocator;

public class ProjectTemplateLocator implements TemplateLocator {

	private final Path base;

	public ProjectTemplateLocator(Path base) {
		this.base = base;
	}

	@Override
	public Optional<TemplateLocation> locate(String id) {
		Path templatePath = base.resolve(id);
		if (Files.exists(templatePath)) {
			return Optional.of(new ResourceTemplateLocation(templatePath));
		}
		return Optional.empty();
	}
}