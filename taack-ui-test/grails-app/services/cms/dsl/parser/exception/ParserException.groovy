package cms.dsl.parser.exception

class ParserException extends Exception {
    Iterator<cms.dsl.parser.Tokenizer.TokenInfo> tokens
    cms.dsl.parser.Tokenizer.TokenInfo lookahead

    ParserException(String message, Iterator<cms.dsl.parser.Tokenizer.TokenInfo> tokens, cms.dsl.parser.Tokenizer.TokenInfo lookahead) {
        super(message)
        this.tokens = tokens
        this.lookahead = lookahead
    }
}
