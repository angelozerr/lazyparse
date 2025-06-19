package io.lazyparse.generator.java;

import java.io.IOException;
import java.nio.file.Path;

import io.lazyparse.bnf.visitors.BnfGrammar;
import io.lazyparse.generator.CodeGenerator;
import io.lazyparse.generator.context.Context;

public class JavaCodeGenerator extends CodeGenerator {

	public record JavaScannerContext(String scannerApiPackageName, String scannerImplPackageName,
			String scannerClassName) {
	}

	public JavaCodeGenerator(Path templatesProjectPath) {
		super(templatesProjectPath);
	}

	public void generateScanner(BnfGrammar grammar, JavaScannerContext javaScannerContext, Path output)
			throws IOException {
		String scannerApiPackageName = javaScannerContext.scannerApiPackageName();
		String scannerImplPackageName = javaScannerContext.scannerImplPackageName();

		// Generate Scanner API
		JavaFileContext scannerApiContext = new JavaFileContext(scannerApiPackageName, "Scanner");
		generate("java/scanner/Scanner.java.qute", scannerApiContext, output);

		// Generate AbstractScanner
		JavaFileContext abstractScannerContext = new JavaFileContext(scannerApiPackageName, "AbstractScanner");
		generate("java/scanner/AbstractScanner.java.qute", abstractScannerContext, output);

		// Generate MultiLineStream
		JavaFileContext multiLineStreamContext = new JavaFileContext(scannerApiPackageName, "MultiLineStream");
		generate("java/scanner/MultiLineStream.java.qute", multiLineStreamContext, output);

		// Generate ScannerState
		JavaFileContext scannerStateContext = new JavaFileContext(scannerImplPackageName, "ScannerState");
		generate(grammar, "java/scanner/ScannerState.java.qute", scannerStateContext, output);

		// Generate TokenType
		JavaFileContext tokenTypeContext = new JavaFileContext(scannerImplPackageName, "TokenType");
		generate(grammar, "java/scanner/TokenType.java.qute", tokenTypeContext, output);

		// Generate XScanner
		Context scannerImplContext = new JavaFileContext(scannerImplPackageName, javaScannerContext.scannerClassName());
		scannerImplContext.put("scannerApiPackageName", scannerApiPackageName);
		generate(grammar, "java/scanner/ScannerImpl.java.qute", scannerImplContext, output);
	}

}
