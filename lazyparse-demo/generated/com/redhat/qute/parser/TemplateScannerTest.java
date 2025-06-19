package com.redhat.qute.parser;

import com.redhat.qute.parser.scanner.Scanner;
import com.redhat.qute.parser.template.scanner.ScannerState;
import com.redhat.qute.parser.template.scanner.TemplateScanner;
import com.redhat.qute.parser.template.scanner.TokenType;

public class TemplateScannerTest {

	public static void main(String[] args) {
		Scanner<TokenType, ScannerState> scanner = TemplateScanner.createScanner("foo bar");
		TokenType token = scanner.scan();
		while (token != TokenType.EOS) {
			System.err.println(token.toString() + ": " + scanner.getTokenText() + "(" + scanner.getTokenOffset() + ","
					+ scanner.getTokenEnd() + ")");
			token = scanner.scan();
		}
	}
}
