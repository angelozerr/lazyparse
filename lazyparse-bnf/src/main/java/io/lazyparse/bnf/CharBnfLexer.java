package io.lazyparse.bnf;

import java.util.ArrayList;
import java.util.List;

public class CharBnfLexer {

    public enum TokenType {
        IDENTIFIER,
        ANNOTATION,
        STRING_LITERAL,
        COLON_COLON_EQUALS,
        PIPE,
        STAR,
        PLUS,
        QUESTION,
        SYMBOL,
        //AT,         // '@'
        LPAREN,     // '('
        RPAREN,     // ')'
        COMMA,      // ','
        TILDE,      // '~'
        EOF
    }

    public static class Token {
        public final TokenType type;
        public final String text;
        public final int position;

        public Token(TokenType type, String text, int position) {
            this.type = type;
            this.text = text;
            this.position = position;
        }

        public String toString() {
            return type + "('" + text + "')";
        }
    }

    public List<Token> tokenize(String input) {
        List<Token> tokens = new ArrayList<>();
        int pos = 0;
        int length = input.length();

        while (pos < length) {
            char c = input.charAt(pos);

            // Comment line starting with //
            if (c == '/' && pos + 1 < length && input.charAt(pos + 1) == '/') {
                int start = pos;
                pos += 2;
                while (pos < length) {
                    char ch = input.charAt(pos);
                    if (ch == '\n' || ch == '\r') {
                        break;
                    }
                    pos++;
                }
                continue;
            }

            
            // Ignore comments starting with '#' until end of line
            if (c == '#') {
                pos++;
                while (pos < length && input.charAt(pos) != '\n' && input.charAt(pos) != '\r') {
                    pos++;
                }
                continue;
            }

            // Ignore whitespace
            if (Character.isWhitespace(c)) {
                pos++;
                continue;
            }

            // Special single char tokens
            switch (c) {
                //case '@': tokens.add(new Token(TokenType.AT, "@", pos)); pos++; continue;
                case '(': tokens.add(new Token(TokenType.LPAREN, "(", pos)); pos++; continue;
                case ')': tokens.add(new Token(TokenType.RPAREN, ")", pos)); pos++; continue;
                case ',': tokens.add(new Token(TokenType.COMMA, ",", pos)); pos++; continue;
                case '~': tokens.add(new Token(TokenType.TILDE, "~", pos)); pos++; continue;
                case '|': tokens.add(new Token(TokenType.PIPE, "|", pos)); pos++; continue;
                case '*': tokens.add(new Token(TokenType.STAR, "*", pos)); pos++; continue;
                case '+': tokens.add(new Token(TokenType.PLUS, "+", pos)); pos++; continue;
                case '?': tokens.add(new Token(TokenType.QUESTION, "?", pos)); pos++; continue;
            }

            // IDENTIFIER (letter, _, @) then letters/digits/_
            if (Character.isLetter(c) || c == '_' || c == '@') {
                int start = pos;
                pos++;
                while (pos < length) {
                    char cc = input.charAt(pos);
                    if (Character.isLetterOrDigit(cc) || cc == '_') {
                        pos++;
                    } else {
                        break;
                    }
                }
                String text = input.substring(start, pos);
                if (c == '@') {
                	tokens.add(new Token(TokenType.ANNOTATION, text, start));
                } else {
                	tokens.add(new Token(TokenType.IDENTIFIER, text, start));
                }
                continue;
            }

            // COLON_COLON_EQUALS ::= "::="
            if (c == ':' && pos + 2 < length && input.charAt(pos + 1) == ':' && input.charAt(pos + 2) == '=') {
                tokens.add(new Token(TokenType.COLON_COLON_EQUALS, "::=", pos));
                pos += 3;
                continue;
            }

            // STRING_LITERAL double quotes
            if (c == '"') {
                int start = pos;
                pos++;
                StringBuilder sb = new StringBuilder();
                boolean closed = false;
                while (pos < length) {
                    char cc = input.charAt(pos);
                    if (cc == '"') {
                        pos++;
                        closed = true;
                        break;
                    }
                    if (cc == '\\' && pos + 1 < length) { // simple escape
                        sb.append(cc);
                        pos++;
                        cc = input.charAt(pos);
                    }
                    sb.append(cc);
                    pos++;
                }
                if (!closed) {
                    throw new RuntimeException("Unterminated string literal at position " + start);
                }
                tokens.add(new Token(TokenType.STRING_LITERAL, sb.toString(), start));
                continue;
            }

            // STRING_LITERAL single quotes
            if (c == '\'') {
                int start = pos;
                pos++;
                StringBuilder sb = new StringBuilder();
                boolean closed = false;
                while (pos < length) {
                    char cc = input.charAt(pos);
                    if (cc == '\'') {
                        pos++;
                        closed = true;
                        break;
                    }
                    if (cc == '\\' && pos + 1 < length) {
                        sb.append(cc);
                        pos++;
                        cc = input.charAt(pos);
                    }
                    sb.append(cc);
                    pos++;
                }
                if (!closed) {
                    throw new RuntimeException("Unterminated string literal at position " + start);
                }
                tokens.add(new Token(TokenType.STRING_LITERAL, sb.toString(), start));
                continue;
            }

            if (c == ';') {
            	pos++;
            	continue;
            }
            
            // SYMBOL (any other char)
            tokens.add(new Token(TokenType.SYMBOL, Character.toString(c), pos));
            pos++;
        }
        
        tokens.add(new Token(TokenType.EOF, "", pos));
        return tokens;
    }
}
