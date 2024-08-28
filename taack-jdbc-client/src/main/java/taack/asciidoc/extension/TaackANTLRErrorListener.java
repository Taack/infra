package taack.asciidoc.extension;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

import java.util.BitSet;

class TaackANTLRErrorListener implements ANTLRErrorListener {

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        System.out.println("syntaxError" + "line: " + line + ", at position: " + charPositionInLine + " " + msg);
        throw new RuntimeException("syntaxError" + "line: " + line + ", at position: " + charPositionInLine + " " + msg);
    }

    @Override
    public void reportAmbiguity(Parser parser, DFA dfa, int i, int i1, boolean b, BitSet bitSet, ATNConfigSet atnConfigSet) {
        System.out.println("reportAmbiguity " + parser + " " + dfa + " " + i + " " + b + " " + bitSet + " " + atnConfigSet);
    }

    @Override
    public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex, BitSet conflictingAlts, ATNConfigSet configs) {
        System.out.println("reportAttemptingFullContext " + recognizer + " " + dfa + " " + startIndex + " " + stopIndex + " " + conflictingAlts + " " + configs);
    }

    @Override
    public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction, ATNConfigSet configs) {
        System.out.println("reportAttemptingFullContext " + recognizer + " " + dfa + " " + startIndex + " " + stopIndex + " " + prediction + " " + configs);
    }
}
