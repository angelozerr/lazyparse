package com.redhat.qute.parser;

import com.redhat.qute.parser.scanner.Scanner;
import com.redhat.qute.parser.template.scanner.ScannerState;
import com.redhat.qute.parser.template.scanner.TemplateScanner;
import com.redhat.qute.parser.template.scanner.TokenType;

public class TemplateScannerTest {

	public static void main(String[] args) {
		displayTokens("{!foo bar!}");
		displayTokens("{foo bar}");
		displayTokens("{!{foo bar}!}{=foo bar}");
		displayTokens("{!{foo bar}}{=foo bar}");
	}

	private static void displayTokens(String template) {
		Scanner<TokenType, ScannerState> scanner = TemplateScanner.createScanner(template);
		TokenType token = scanner.scan();
		while (token != TokenType.EOS) {
			System.err.println(token.toString() + ": " + scanner.getTokenText() + "(" + scanner.getTokenOffset() + ","
					+ scanner.getTokenEnd() + ") -> " + scanner.getScannerState());
			token = scanner.scan();
		}
		System.err.println();
	}
}
