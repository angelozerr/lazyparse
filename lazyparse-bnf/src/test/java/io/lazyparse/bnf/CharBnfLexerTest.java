package io.lazyparse.bnf;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.List;

public class CharBnfLexerTest {

    @Test
    public void testTokenizeIgnoresComments() {
        String bnf = """
            # This is a comment
            Template ::= Text | Expression | Section
            Text ::= "some text" | 'other text'
            # Another comment
            Expression ::= "expr"
            """;

        CharBnfLexer lexer = new CharBnfLexer();
        List<CharBnfLexer.Token> tokens = lexer.tokenize(bnf);

        // Check that EOF token is present
        assertFalse(tokens.isEmpty());
        assertEquals(CharBnfLexer.TokenType.EOF, tokens.get(tokens.size() - 1).type);

        // Check that comments are ignored, so no token "#" exists
        boolean containsCommentToken = tokens.stream()
            .anyMatch(t -> t.text.equals("#"));
        assertFalse(containsCommentToken, "Comments should not generate '#' tokens");

        // Check presence of identifier, operator and string tokens
        boolean hasTemplate = tokens.stream().anyMatch(t -> t.text.equals("Template"));
        boolean hasAssign = tokens.stream().anyMatch(t -> t.type == CharBnfLexer.TokenType.COLON_COLON_EQUALS);
        boolean hasPipe = tokens.stream().anyMatch(t -> t.text.equals("|"));
        boolean hasStringLiteral = tokens.stream().anyMatch(t -> t.type == CharBnfLexer.TokenType.STRING_LITERAL);

        assertTrue(hasTemplate, "Should contain 'Template'");
        assertTrue(hasAssign, "Should contain ::=");
        assertTrue(hasPipe, "Should contain |");
        assertTrue(hasStringLiteral, "Should contain string literals");

        // For example, check that the strings are correctly extracted
        assertTrue(tokens.stream().anyMatch(t -> t.text.equals("some text")));
        assertTrue(tokens.stream().anyMatch(t -> t.text.equals("other text")));
    }
}
