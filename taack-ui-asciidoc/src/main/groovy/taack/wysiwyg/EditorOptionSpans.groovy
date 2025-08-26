package taack.wysiwyg


import groovy.transform.CompileStatic
import taack.ui.dsl.form.editor.SpanRegex

@CompileStatic
enum TaackBaseAsciidocSpans {
    DOCUMENT(new SpanRegex("= ", "asciidoc-h1", SpanRegex.Mode.START)),
    HEADER1(new SpanRegex("== ", "asciidoc-h2", SpanRegex.Mode.START)),
    HEADER2(new SpanRegex("=== ", "asciidoc-h3", SpanRegex.Mode.START)),
    HEADER3(new SpanRegex("==== ", "asciidoc-h4", SpanRegex.Mode.START)),
    HEADER4(new SpanRegex("===== ", "asciidoc-h5", SpanRegex.Mode.START)),
    TITLE(new SpanRegex(".", "asciidoc-title", SpanRegex.Mode.START)),
    bullet1(new SpanRegex("()(^--\$)()", "asciidoc-bullet1", SpanRegex.Mode.INLINED)),
    bullet2(new SpanRegex("()(^----\$)()", "asciidoc-bullet2", SpanRegex.Mode.INLINED)),
    bullet3(new SpanRegex("()(^------\$)()", "asciidoc-bullet3", SpanRegex.Mode.INLINED)),
    META(new SpanRegex("()(^\\\\[[^[\\\\]]*\\\\]\$)()", "asciidoc-meta", SpanRegex.Mode.INLINED)),
    UNORDERED_LIST1(new SpanRegex("* ", "asciidoc-b1", SpanRegex.Mode.START)),
    UNORDERED_LIST2(new SpanRegex("** ", "asciidoc-b2", SpanRegex.Mode.START)),
    UNORDERED_LIST3(new SpanRegex("*** ", "asciidoc-b3", SpanRegex.Mode.START)),

    UNCONSTRAINED_BOLD(new SpanRegex("([^*]?)(\\\\*\\\\*[^*]*\\\\*\\\\*)([^*]?)", "asciidoc-bold", SpanRegex.Mode.INLINED)),
    UNCONSTRAINED_ITALIC(new SpanRegex("([^_]?)(__[^_]*__)([^_]?)", "asciidoc-italic", SpanRegex.Mode.INLINED)),
    UNCONSTRAINED_MONO(new SpanRegex("[^`]``([^`]*)``[^`]", "asciidoc-mono", SpanRegex.Mode.INLINED)),
    CONSTRAINED_BOLD(new SpanRegex("([^\\\\w\\\\d*])(\\\\*[^*]+\\\\*)([^\\\\w\\\\d*]?)", "asciidoc-bold", SpanRegex.Mode.INLINED)),

    //            LITERAL_PARAGRAPH(new SpanRegex("^ .*", "asciidoc-literal", true)),
    CONSTRAINED_ITALIC(new SpanRegex("([^\\\\w\\\\d_])(_[^_]+_)([^\\\\w\\\\d_]?)", "asciidoc-italic", SpanRegex.Mode.INLINED)),
    CONSTRAINED_MONO(new SpanRegex(" `([^`]*)` ", "asciidoc-mono", SpanRegex.Mode.INLINED)),
    HIGHLIGHT(new SpanRegex("[^`]``([^`]*)``[^`]", "asciidoc-highlight", SpanRegex.Mode.INLINED)),
    UNDERLINE(new SpanRegex("([^\\\\w\\\\d]?)(\\\\[.underline\\\\]#[^#]*#)([^\\\\w\\\\d]?)", "asciidoc-underline", SpanRegex.Mode.INLINED)),
    STRIKETHROUGH(new SpanRegex("([^\\\\w\\\\d]?)(\\\\[.line-through\\\\]#[^#]*#)([^\\\\w\\\\d]?)", "asciidoc-line-through", SpanRegex.Mode.INLINED)),
    SMART_QUOTES(new SpanRegex("\"`#([^\"`]*)`\"", "asciidoc-smart-quotes", SpanRegex.Mode.INLINED)),
    APOSTROPHES(new SpanRegex("'`#([^'`]*)`'", "asciidoc-apostrophe", SpanRegex.Mode.INLINED)),
    URL(new SpanRegex("([^\\\\w\\\\d]?)(http[s]?://[^[]*\\\\[[^\\\\]]*\\\\])([^\\\\w\\\\d]?)", "asciidoc-url", SpanRegex.Mode.INLINED)),
    IMAGE(new SpanRegex("()(^image::[^[:]*\\\\[[^\\\\]]*\\\\]\$)()", "asciidoc-image", SpanRegex.Mode.INLINED)),
    IMAGE_INLINE(new SpanRegex("([^\\\\w\\\\d]?)(image:[^[:]*\\\\[[^\\\\]]*\\\\])([^\\\\w\\\\d]?)", "asciidoc-inline-image", SpanRegex.Mode.INLINED))

    TaackBaseAsciidocSpans(SpanRegex span) {
        this.span = span
    }

    SpanRegex span

    static SpanRegex[] getSpans() {
        values()*.span.toArray() as SpanRegex[]
    }

    static String serializeString(List<SpanRegex> spanRegexes) {
        ((spanRegexes ?: values()*.span)*.serializeString()).join('')
    }
}

@CompileStatic
enum TaackAsciidocTable {
    TABLE_DELIM_START(new SpanRegex("^()(\\\\|===)()\$", "asciidoc-table-sep", SpanRegex.Mode.CONTEXT_START)),
    CELL_SEP(new SpanRegex("()(\\\\|)([^=])", "asciidoc-table-cell-sep", SpanRegex.Mode.INLINED)),
    TABLE_DELIM_END(new SpanRegex("^()(\\\\|===)()\$", "asciidoc-table-sep", SpanRegex.Mode.CONTEXT_END))

    TaackAsciidocTable(SpanRegex span) {
        this.span = span
    }

    SpanRegex span

    static SpanRegex[] getSpans() {
        values()*.span.toArray() as SpanRegex[]
    }

    static String serializeString(List<SpanRegex> spanRegexes) {
        ((spanRegexes ?: values()*.span)*.serializeString()).join('')
    }
}

@CompileStatic
enum TaackAsciidocPlantUML {
    PLANTUML_DELIM_START(new SpanRegex("^\\\\[plantuml]\n----\$", "asciidoc-plantuml-begin", SpanRegex.Mode.CONTEXT_START)),
    PLANTUML_DELIM_END(new SpanRegex("^----\$", "asciidoc-plantuml-end", SpanRegex.Mode.CONTEXT_END))

    TaackAsciidocPlantUML(SpanRegex span) {
        this.span = span
    }

    SpanRegex span
    static SpanRegex[] getSpans() {
        values()*.span.toArray() as SpanRegex[]
    }
}

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

