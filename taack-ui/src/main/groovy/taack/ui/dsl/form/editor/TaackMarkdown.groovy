package taack.ui.dsl.form.editor

import groovy.transform.CompileStatic

@CompileStatic
enum TaackMarkdown {
    DOCUMENT(new SpanRegex("# ", "asciidoc-h1", SpanRegex.Mode.START)),
    HEADER1(new SpanRegex("## ", "asciidoc-h2", SpanRegex.Mode.START)),
    HEADER2(new SpanRegex("### ", "asciidoc-h3", SpanRegex.Mode.START)),
    HEADER3(new SpanRegex("#### ", "asciidoc-h4", SpanRegex.Mode.START)),
    HEADER4(new SpanRegex("##### ", "asciidoc-h5", SpanRegex.Mode.START)),
    TITLE(new SpanRegex(".", "asciidoc-title", SpanRegex.Mode.START)),
    UNORDERED_LIST1(new SpanRegex("* ", "asciidoc-b1", SpanRegex.Mode.START)),
    UNORDERED_LIST2(new SpanRegex("  * ", "asciidoc-b2", SpanRegex.Mode.START)),
    UNORDERED_LIST3(new SpanRegex("    * ", "asciidoc-b3", SpanRegex.Mode.START)),

    UNCONSTRAINED_BOLD(new SpanRegex("([^*]?)(\\\\*\\\\*[^*]*\\\\*\\\\*)([^*]?)", "asciidoc-bold", SpanRegex.Mode.INLINED)),
    UNCONSTRAINED_ITALIC(new SpanRegex("([^_]?)(_[^_]*_)([^_]?)", "asciidoc-italic", SpanRegex.Mode.INLINED)),
    UNCONSTRAINED_MONO(new SpanRegex("[^`]`([^`]*)`[^`]", "asciidoc-mono", SpanRegex.Mode.INLINED)),
    HIGHLIGHT(new SpanRegex("[^#]#([^#]*)#[^#]", "asciidoc-highlight", SpanRegex.Mode.INLINED)),
    UNDERLINE(new SpanRegex("([^\\\\w\\\\d]?)(<u>.*</u>)([^\\\\w\\\\d]?)", "asciidoc-underline", SpanRegex.Mode.INLINED)),
    STRIKETHROUGH(new SpanRegex("([^\\\\w\\\\d]?)(<del>.*</del>)([^\\\\w\\\\d]?)", "asciidoc-line-through", SpanRegex.Mode.INLINED)),
    URL(new SpanRegex("([^\\\\w\\\\d]?)([^[]*\\\\[[^\\\\]]*\\\\]\\\\(.*\\\\))([^\\\\w\\\\d]?)", "asciidoc-url", SpanRegex.Mode.INLINED))

    TaackMarkdown(SpanRegex span) {
        this.span = span
    }

    SpanRegex span

    static SpanRegex[] getSpans() {
        values()*.span.toArray() as SpanRegex[]
    }

    static String serializeString(List<SpanRegex> spanRegexes) {
        "TaackMarkdown\n" + ((spanRegexes ?: values()*.span)*.serializeString()).join('')
    }
}

