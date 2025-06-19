package io.lazyparse.generator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import io.lazyparse.bnf.visitors.BnfGrammar;
import io.lazyparse.generator.context.Context;
import io.lazyparse.generator.context.ContextEntry;
import io.quarkus.qute.Engine;
import io.quarkus.qute.FragmentNamespaceResolver;
import io.quarkus.qute.NamedArgument;
import io.quarkus.qute.ReflectionValueResolver;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;

/**
 * 
 */
public class CodeGenerator {

	private final Engine engine;

	public CodeGenerator(Path templatesProjectPath) {
		this.engine = Engine.builder() //
				.addDefaults() //
				.addLocator(new ProjectTemplateLocator(templatesProjectPath))
				.addValueResolver(new ReflectionValueResolver())//
				// Named arguments for fragment namespace resolver
				.addNamespaceResolver(new NamedArgument.ParamNamespaceResolver())
				.addNamespaceResolver(new FragmentNamespaceResolver(FragmentNamespaceResolver.FRAGMENT))//
				.addNamespaceResolver(new FragmentNamespaceResolver(FragmentNamespaceResolver.CAPTURE))//
				.build();

	}

	public void generate(String templateName, Context context, Path outputDir) throws IOException {
		generate(null, templateName, context, outputDir);
	}

	public void generate(BnfGrammar grammar, String templateName, Context context, Path outputDir) throws IOException {

		Template template = engine.getTemplate(templateName);

		TemplateInstance templateInstance = template.instance();
		if (grammar != null) {
			templateInstance = templateInstance.data("grammar", grammar);
		}
		for (ContextEntry entry : context.getEntries()) {
			templateInstance = templateInstance.data(entry.getName(), entry.getValue());
		}
		String generatedCode = templateInstance.render();

		System.err.println(generatedCode);

		String dir = context.getDir();
		if (dir != null) {
			outputDir = outputDir.resolve(dir);
		}

		String fileName = context.getFileName();
		Path outFile = outputDir.resolve(fileName);

		Files.createDirectories(outputDir);
		Files.writeString(outFile, generatedCode);
	}
}
