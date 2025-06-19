package com.redhat.qute.parser.template.scanner;

import com.redhat.qute.parser.scanner.Scanner;
import com.redhat.qute.parser.scanner.AbstractScanner;

public class TemplateScanner extends AbstractScanner<TokenType, ScannerState> {
    
	public static Scanner<TokenType, ScannerState> createScanner(String input) {
		return createScanner(input, 0);
	}

	public static Scanner<TokenType, ScannerState> createScanner(String input, int initialOffset) {
		return createScanner(input, initialOffset, ScannerState.WithinContent);
	}

	public static Scanner<TokenType, ScannerState> createScanner(String input, int initialOffset,
			ScannerState initialState) {
		return new TemplateScanner(input, initialOffset, initialState);
	}

	TemplateScanner(String input, int initialOffset, ScannerState initialState) {
		super(input, initialOffset, initialState, TokenType.Unknown, TokenType.EOS);
	}

    @Override
    protected TokenType internalScan() {
      int offset = stream.pos();
      if (stream.eos()) {
        return finishToken(offset, TokenType.EOS);
      }
      String errorMessage = null;
      switch (state) {
	  }
	  return finishToken(offset, TokenType.Unknown, errorMessage);
	}

}
