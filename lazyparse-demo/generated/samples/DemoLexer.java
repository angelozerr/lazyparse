package samples;

public class DemoLexer {

    public void parse() {
        // Règles extraites de la BNF:
        // - expr ::= (term (("+" | "-") term)*)
        // - term ::= (factor (("*" | "/") factor)*)
        // - factor ::= (NUMBER | ("(" expr ")"))
    }
}
