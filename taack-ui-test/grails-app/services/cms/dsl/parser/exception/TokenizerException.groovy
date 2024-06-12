package cms.dsl.parser.exception

import groovy.transform.CompileStatic

@CompileStatic
class TokenizerException extends Exception {
    String problematicString

    TokenizerException(String message, String problematicString) {
        super(message)
        this.problematicString = problematicString
    }
}
