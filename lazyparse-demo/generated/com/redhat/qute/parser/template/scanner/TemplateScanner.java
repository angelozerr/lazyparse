package com.redhat.qute.parser.template.scanner;

import com.redhat.qute.parser.scanner.AbstractScanner;
import com.redhat.qute.parser.scanner.Scanner;

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

		case WithinContent: {
			if (stream.advanceIfChar('{')) {
				if (stream.advanceIfChar('=')) {
					state = ScannerState.WithinExpression;
					return finishToken(offset, TokenType.StartExpression);
				}

				if (stream.advanceIfChar('!')) {
					state = ScannerState.WithinComment;
					return finishToken(offset, TokenType.StartComment);
				}

			}

			stream.advanceUntilChar('{');
			return finishToken(offset, TokenType.Content);

		}

		case WithinComment: {
			if (stream.advanceIfChars(new int[] { '!', '}' })) {
				state = ScannerState.WithinContent;
				return finishToken(offset, TokenType.EndComment);
			}

			stream.advanceUntilChars(new int[] { '!', '}' });
			state = ScannerState.WithinComment;
			return finishToken(offset, TokenType.CommentContent);

		}

		case WithinExpression: {
			if (stream.advanceIfChar('}')) {
				state = ScannerState.WithinContent;
				return finishToken(offset, TokenType.EndExpression);
			}

			stream.advanceUntilChar('}');
			state = ScannerState.WithinExpression;
			return finishToken(offset, TokenType.ExpressionContent);

		}
		}
		return finishToken(offset, TokenType.Unknown, errorMessage);
	}

}