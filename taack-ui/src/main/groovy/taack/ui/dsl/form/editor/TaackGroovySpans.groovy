package taack.ui.dsl.form.editor

import groovy.transform.CompileStatic

/**
 * Immediate lexical highlighting of Groovy code in an editor, line by line: comments, strings, annotations, numbers,
 * keywords and types. The classes are those of the semantic tokens of a Language Server ({@code tok-<type>}), which
 * refine the highlighting when the editor has one, see {@link EditorOption.EditorOptionBuilder#onLspAction}.
 * <p>
 * Usage: {@code EditorOption.getBuilder().addSpanRegexes(TaackGroovySpans.spans)}
 * <p>
 * The editor tries the patterns in this order at each character and keeps the first matching there, so a keyword in
 * a string or a comment is not highlighted. The backslashes are doubled: the options are unescaped when serialized.
 */
@CompileStatic
enum TaackGroovySpans {
    COMMENT(new SpanRegex($/()(//.*|/\\*(?:(?!\\*/).)*(?:\\*/)?|^\\s*\\*.*)()/$, 'tok-comment', SpanRegex.Mode.INLINED)),
    STRING(new SpanRegex($/()("""(?:(?!""").)*(?:""")?|'''(?:(?!''').)*(?:''')?|"(?:[^"\\\\]|\\\\.)*"?|'(?:[^'\\\\]|\\\\.)*'?)()/$, 'tok-string', SpanRegex.Mode.INLINED)),
    ANNOTATION(new SpanRegex($/()(@[A-Za-z_][\\w.]*)()/$, 'tok-decorator', SpanRegex.Mode.INLINED)),
    NUMBER(new SpanRegex($/()(\\b(?:0[xX][0-9a-fA-F_]+|\\d[\\d_]*(?:\\.\\d[\\d_]*)?(?:[eE][+-]?\\d+)?)[dDfFlLgGiI]?\\b)()/$, 'tok-number', SpanRegex.Mode.INLINED)),
    KEYWORD(new SpanRegex($/()(\\b(?:abstract|as|assert|break|case|catch|class|const|continue|def|default|do|else|enum|extends|final|finally|for|goto|if|implements|import|in|instanceof|interface|native|new|non-sealed|package|permits|private|protected|public|record|return|sealed|static|strictfp|super|switch|synchronized|this|throw|throws|trait|transient|try|var|void|volatile|while|yield|boolean|byte|char|short|int|long|float|double|true|false|null)\\b)()/$, 'tok-keyword', SpanRegex.Mode.INLINED)),
    // Color is a type, RIGHT a constant
    TYPE(new SpanRegex($/()(\\b[A-Z][A-Z0-9_]*[a-z]\\w*\\b)()/$, 'tok-type', SpanRegex.Mode.INLINED))

    TaackGroovySpans(SpanRegex span) {
        this.span = span
    }

    final SpanRegex span

    static SpanRegex[] getSpans() {
        values()*.span.toArray() as SpanRegex[]
    }
}
