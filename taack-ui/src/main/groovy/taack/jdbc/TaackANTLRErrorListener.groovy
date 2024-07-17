package taack.jdbc

import groovy.transform.CompileStatic
import org.antlr.v4.runtime.ANTLRErrorListener
import org.antlr.v4.runtime.Parser
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import org.antlr.v4.runtime.atn.ATNConfigSet
import org.antlr.v4.runtime.dfa.DFA

@CompileStatic
class TaackANTLRErrorListener implements ANTLRErrorListener {

    @Override
    void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        println "TaackANTLRErrorListener:syntaxError $recognizer $offendingSymbol $line $charPositionInLine $msg $e"
        throw new TaackJdbcError('syntaxError', "line: ${line}, at position: $charPositionInLine, $msg")
    }

    @Override
    void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact, BitSet ambigAlts, ATNConfigSet configs) throws TaackJdbcError {
        println "TaackANTLRErrorListener:reportAmbiguity $recognizer $dfa $startIndex $stopIndex $exact $ambigAlts $configs"
//        errors << new AntlrError(errorStep: 'reportAmbiguity', errorMessage: "line: ${line}, at position: $charPositionInLine, $msg")
        throw new TaackJdbcError('reportAmbiguity', "$recognizer $dfa $startIndex $stopIndex $exact $ambigAlts $configs")
    }

    @Override
    void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex, BitSet conflictingAlts, ATNConfigSet configs) throws TaackJdbcError {
        println "TaackANTLRErrorListener:reportAttemptingFullContext $recognizer $dfa $startIndex $stopIndex $conflictingAlts $configs"
//        throw new TaackJdbcError('reportAttemptingFullContext', "$recognizer $dfa $startIndex $stopIndex $conflictingAlts $configs")
    }

    @Override
    void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction, ATNConfigSet configs) throws TaackJdbcError {
        println "TaackANTLRErrorListener:reportContextSensitivity $recognizer $dfa $startIndex $stopIndex $prediction $configs"
        throw new TaackJdbcError('reportContextSensitivity', "$recognizer $dfa $startIndex $stopIndex $prediction $configs")
    }
}
