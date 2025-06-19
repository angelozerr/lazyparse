package io.lazyparse.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Lexer tolérant, générant une liste de tokens au fur et à mesure.
 */
public class LazyLexer {
    private final String input;
    private int pos = 0;

    public LazyLexer(String input) {
        this.input = input;
    }

    public boolean hasNext() {
        return pos < input.length();
    }

    public Token nextToken() {
        if (!hasNext()) return null;

        // Skip whitespaces
        while (pos < input.length() && Character.isWhitespace(input.charAt(pos))) {
            pos++;
        }
        if (!hasNext()) return null;

        char current = input.charAt(pos);

        // Simple tokenization logic
        if (Character.isLetter(current)) {
            int start = pos;
            while (pos < input.length() && Character.isLetterOrDigit(input.charAt(pos))) pos++;
            String text = input.substring(start, pos);
            return new Token(TokenType.IDENTIFIER, text);
        } else if (Character.isDigit(current)) {
            int start = pos;
            while (pos < input.length() && Character.isDigit(input.charAt(pos))) pos++;
            String text = input.substring(start, pos);
            return new Token(TokenType.NUMBER, text);
        } else {
            pos++;
            return new Token(TokenType.SYMBOL, Character.toString(current));
        }
    }

    public List<Token> tokenizeAll() {
        List<Token> tokens = new ArrayList<>();
        Token token;
        while ((token = nextToken()) != null) {
            tokens.add(token);
        }
        return tokens;
    }
}
