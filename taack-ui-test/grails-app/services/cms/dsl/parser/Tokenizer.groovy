package cms.dsl.parser

import groovy.transform.CompileStatic

import java.util.regex.Matcher

@CompileStatic
class Tokenizer {
    static class TokenInfo {
        final String sequence
        final Token token
        final int start
        final int end

        TokenInfo(Token token, String sequence, int start, int end) {
            this.sequence = sequence
            this.token = token
            this.start = start
            this.end = end
        }

        @Override
        String toString() {
            return "$token: '$sequence'"
        }
    }

    List<TokenInfo> tokens = []
    int start = 0
    int end = 0

    void tokenize(String str) {
        str = str?.trim()
        if (!str) return
        String s = new String(str)
        tokens.clear()

        while (!s.equals("")) {
            boolean match = false

            for (Token t : Token.values()) {
                Matcher m = t.regex.matcher(s)

                if (m.find()) {
                    match = true

                    end += m.end()
                    String tok = m.group().trim()
                    tokens.add(new TokenInfo(t, tok, start, end))
                    start += m.end()
                    s = m.replaceFirst("")
                    int ws = s.length()
                    s = ltrim(s).replaceFirst("[\n\r]", "")
                    ws -= s.length()
                    start += ws
                    end += ws
                    break
                }
            }
            if (!match) {
                tokens.add(new TokenInfo(Token.ERROR, s, start, start))
                s = ""
//                throw new TokenizerException("Unexpected character in input! ", s)
            }
        }
    }

    static String ltrim(String s) {
        int i = 0
        while (i < s.length() && Character.isWhitespace(s.charAt(i))) {
            i++
        }
        return s.substring(i)
    }
}
