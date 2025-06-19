package samples;

public class MathsLexer {

    public void parse() {
        // Règles extraites de la BNF:
        // - expr ::= (term (("+" | "-") term)*)
        // - term ::= (factor (("*" | "/") factor)*)
        // - factor ::= (NUMBER | ("(" expr ")"))
    }
}
