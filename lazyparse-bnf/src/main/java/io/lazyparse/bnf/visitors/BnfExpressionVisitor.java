package io.lazyparse.bnf.visitors;

import io.lazyparse.bnf.BnfParser;
import io.lazyparse.bnf.BnfParser.Alternative;
import io.lazyparse.bnf.BnfParser.Annotation;
import io.lazyparse.bnf.BnfParser.Identifier;
import io.lazyparse.bnf.BnfParser.Literal;
import io.lazyparse.bnf.BnfParser.Repetition;
import io.lazyparse.bnf.BnfParser.Sequence;

public interface BnfExpressionVisitor<R> {

    R visitIdentifier(BnfParser.Identifier identifier);

    R visitLiteral(BnfParser.Literal literal);

    R visitSequence(BnfParser.Sequence sequence);

    R visitAlternative(BnfParser.Alternative alternative);

    R visitRepetition(BnfParser.Repetition repetition);

    R visitAnnotation(BnfParser.Annotation annotation);
}
