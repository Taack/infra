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
    private final boolean inlined
    private final boolean delimiter

    SpanRegex(String pattern, String className, boolean inlined, boolean delimiter = false) {
        this.pattern = pattern
        this.className = className
        this.inlined = inlined
        this.delimiter = delimiter
    }

    String serializeString() {
        String data = "§$className§$pattern§$inlined§$delimiter§"
        return "§$data\n"
    }
}

@CompileStatic
enum Asciidoc {
    DOCUMENT(new SpanRegex("= ", "asciidoc-h1", false)),
    HEADER1(new SpanRegex("== ", "asciidoc-h2", false)),
    HEADER2(new SpanRegex("=== ", "asciidoc-h3", false)),
    HEADER3(new SpanRegex("==== ", "asciidoc-h4", false)),
    HEADER4(new SpanRegex("===== ", "asciidoc-h5", false)),
    TITLE(new SpanRegex(".", "asciidoc-title", false)),
    bullet1(new SpanRegex("()(^--\$)()", "asciidoc-bullet1", true)),
    bullet2(new SpanRegex("()(^----\$)()", "asciidoc-bullet2", true)),
    bullet3(new SpanRegex("()(^------\$)()", "asciidoc-bullet3", true)),
    META(new SpanRegex("()(^\\\\[[^[\\\\]]*\\\\]\$)()", "asciidoc-meta", true)),
    UNORDERED_LIST1(new SpanRegex("* ", "asciidoc-b1", false)),
    UNORDERED_LIST2(new SpanRegex("** ", "asciidoc-b2", false)),
    UNORDERED_LIST3(new SpanRegex("*** ", "asciidoc-b3", false)),

    UNCONSTRAINED_BOLD(new SpanRegex("([^*]?)(\\\\*\\\\*[^*]*\\\\*\\\\*)([^*]?)", "asciidoc-bold", true, true)),
    UNCONSTRAINED_ITALIC(new SpanRegex("([^_]?)(__[^_]*__)([^_]?)", "asciidoc-italic", true)),
    UNCONSTRAINED_MONO(new SpanRegex("[^`]``([^`]*)``[^`]", "asciidoc-mono", true)),
    CONSTRAINED_BOLD(new SpanRegex("([^\\\\w\\\\d*])(\\\\*[^*]+\\\\*)([^\\\\w\\\\d*]?)", "asciidoc-bold", true, true)),

    //            LITERAL_PARAGRAPH(new SpanRegex("^ .*", "asciidoc-literal", true)),
    CONSTRAINED_ITALIC(new SpanRegex("([^\\\\w\\\\d_])(_[^_]+_)([^\\\\w\\\\d_]?)", "asciidoc-italic", true, true)),
    CONSTRAINED_MONO(new SpanRegex(" `([^`]*)` ", "asciidoc-mono", true)),
    HIGHLIGHT(new SpanRegex("[^`]``([^`]*)``[^`]", "asciidoc-highlight", true)),
    UNDERLINE(new SpanRegex("([^\\\\w\\\\d]?)(\\\\[.underline\\\\]#[^#]*#)([^\\\\w\\\\d]?)", "asciidoc-underline", true, true)),
    STRIKETHROUGH(new SpanRegex("([^\\\\w\\\\d]?)(\\\\[.line-through\\\\]#[^#]*#)([^\\\\w\\\\d]?)", "asciidoc-line-through", true)),
    SMART_QUOTES(new SpanRegex("\"`#([^\"`]*)`\"", "asciidoc-smart-quotes", true)),
    APOSTROPHES(new SpanRegex("'`#([^'`]*)`'", "asciidoc-apostrophe", true)),
    URL(new SpanRegex("([^\\\\w\\\\d]?)(http[s]?://[^[]*\\\\[[^\\\\]]*\\\\])([^\\\\w\\\\d]?)", "asciidoc-url", true)),
    IMAGE(new SpanRegex("()(^image::[^[:]*\\\\[[^\\\\]]*\\\\]\$)()", "asciidoc-image", true)),
    IMAGE_INLINE(new SpanRegex("([^\\\\w\\\\d]?)(image:[^[:]*\\\\[[^\\\\]]*\\\\])([^\\\\w\\\\d]?)", "asciidoc-inline-image", true))

    Asciidoc(SpanRegex span) {
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
        "${uploadFileAction ? Utils.getControllerName(uploadFileAction) + '/' + uploadFileAction.method : ''}${uploadFileActionParams ? '?' + Utils.paramsString(uploadFileActionParams): ''}\n\n${Asciidoc.serializeString(spanRegexes)}"
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