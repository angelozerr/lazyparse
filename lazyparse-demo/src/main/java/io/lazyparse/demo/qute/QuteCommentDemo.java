package io.lazyparse.demo.qute;

import java.nio.file.Path;
import java.nio.file.Paths;

import io.lazyparse.bnf.BnfParser;
import io.lazyparse.core.utils.IOUtils;
import io.lazyparse.generator.java.JavaCodeGenerator;
import io.lazyparse.generator.java.JavaCodeGenerator.JavaScannerContext;

public class QuteCommentDemo {

	public static void main(String[] args) throws Exception {
		
		String bnfText = IOUtils.convertStreamToString(QuteCommentDemo.class.getResourceAsStream("/grammars/qute-comment.bnf"));
		BnfParser parser = new BnfParser(bnfText);
		var grammar = parser.parse();

		Path templatesProjectPath = Paths.get("src/main/resources/templates/");
		Path output = Path.of("generated");

		String scannerApiPackageName = "com.redhat.qute.parser.scanner";
		String scannerImplPackageName = "com.redhat.qute.parser.template.scanner";
		String scannerClassName = "TemplateScanner";

		JavaCodeGenerator generator = new JavaCodeGenerator(templatesProjectPath);

		generator.generateScanner(grammar,
				new JavaScannerContext(scannerApiPackageName, scannerImplPackageName, scannerClassName), output);
		System.out.println("Generated code in " + output.toAbsolutePath());
	}
}
