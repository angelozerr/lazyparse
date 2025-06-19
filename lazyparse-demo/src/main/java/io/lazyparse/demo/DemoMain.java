package io.lazyparse.demo;

import io.lazyparse.bnf.BnfParser;
import io.lazyparse.generator.CodeGenerator;
import io.lazyparse.generator.context.Context;
import io.lazyparse.generator.java.JavaFileContext;

import java.nio.file.Path;
import java.nio.file.Paths;

public class DemoMain {

    public static void main(String[] args) throws Exception {
        String bnfExample = "expr ::= term (('+' | '-') term)*\n" +
                            "term ::= factor (('*' | '/') factor)*\n" +
                            "factor ::= NUMBER | '(' expr ')'";
        BnfParser parser = new BnfParser(bnfExample);
		var grammar = parser.parse();
		
        Path templatesProjectPath = Paths.get("src/main/resources/templates/");
        
        Context context = new JavaFileContext("samples", "DemoLexer");
        CodeGenerator codeGenerator = new CodeGenerator(templatesProjectPath);
        
        Path output = Path.of("generated");
        codeGenerator.generate(grammar, "parser_template.qute", context, output);

        System.out.println("Generated code in " + output.toAbsolutePath());
    }
}
