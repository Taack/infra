package taack.ui.dsl.form.editor

import groovy.json.StringEscapeUtils
import groovy.transform.CompileStatic
import jdk.internal.ValueBased
import org.codehaus.groovy.runtime.MethodClosure
import taack.ui.dsl.helper.Utils

import java.util.zip.Deflater

@CompileStatic
@ValueBased
final class SpanRegex {
    private final String pattern
    private final String className
    private final Mode inlined

    enum Mode {
        INLINED, INLINED_BREAK, START, CONTEXT_START, CONTEXT_END, META
    }

    SpanRegex(String pattern, String className, Mode inlined) {
        this.pattern = pattern
        this.className = className
        this.inlined = inlined
    }

    String serializeString() {
        String data = "§$className§$pattern§$inlined§"
        return "§$data\n"
    }
}

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

@CompileStatic
@ValueBased
final class AutocompleteChoice {
    private final String selection
    private final int caretPosition

    AutocompleteChoice(String selection, int caretPosition) {
        this.selection = selection
        this.caretPosition = caretPosition
    }
}

@CompileStatic
final class EditorOption {
    MethodClosure uploadFileAction
    Map uploadFileActionParams
    List<SpanRegex> spanRegexes = []
    Map<SpanRegex, AutocompleteChoice[]> autocompleteChoices = [:]

    static EditorOptionBuilder getBuilder() {
        return new EditorOptionBuilder()
    }

    static final class EditorOptionBuilder {
        private EditorOption editorOption

        EditorOptionBuilder() {
            this.editorOption = new EditorOption()
        }

        EditorOptionBuilder uploadFileAction(MethodClosure c, Map<String, ? extends Serializable> parameters) {
            editorOption.uploadFileAction = c
            editorOption.uploadFileActionParams = parameters
            this
        }

        EditorOptionBuilder addSpanRegexes(SpanRegex... spanRegexes) {
            editorOption.spanRegexes.addAll(spanRegexes)
            this
        }

        EditorOptionBuilder putAutocompleteChoices(SpanRegex spanRegex, AutocompleteChoice... choices) {
            editorOption.autocompleteChoices.put(spanRegex, choices)
            this
        }

        EditorOption build() {
            editorOption
        }
    }

    String serializeString() {
        "${uploadFileAction ? '/' + Utils.getControllerName(uploadFileAction) + '/' + uploadFileAction.method : ''}${uploadFileActionParams ? '?' + Utils.paramsString(uploadFileActionParams): ''}\n\n${TaackBaseAsciidocSpans.serializeString(spanRegexes)}"
    }

    String compress() {
        String s = serializeString()
//        s = StringEscapeUtils.unescapeJava(UriEncoder.encode(s))
        s = StringEscapeUtils.unescapeJava(s)
        println s
        Deflater deflater = new Deflater()
        deflater.setInput(s.getBytes())
        deflater.finish()

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        byte[] buffer = new byte[4096]

//        while (!deflater.finished()) {
        int compressedSize = deflater.deflate(buffer)
        outputStream.write(buffer, 0, compressedSize)
//        }
        String b64 = new String(Base64.encoder.encode(outputStream.toByteArray()))
        return b64
//        return new String(Base64.encoder.encode(StringEscapeUtils.unescapeJava(UriEncoder.encode(s)).bytes))
    }
}