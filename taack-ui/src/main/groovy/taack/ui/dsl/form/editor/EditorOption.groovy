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

    static enum Mode {
        INLINED, INLINED_BREAK, START, CONTEXT_START, START_CHAR_SEQ, CONTEXT_END, META
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
@ValueBased
final class AutoComplete {
    private final String pattern
    private final String className
    private final SpanRegex.Mode inlined


    AutoComplete(String pattern, String className, SpanRegex.Mode inlined) {
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
@ValueBased
final class MenuEntry {
    private final String pattern
    private final String className
    private final SpanRegex.Mode inlined


    MenuEntry(String pattern, String className, SpanRegex.Mode inlined) {
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
final class EditorOption {
    MethodClosure uploadFileAction
    Map uploadFileActionParams
    List<SpanRegex> spanRegexes = []
    List<AutoComplete> autoCompletes = []
    List<MenuEntry> menuEntries = []

    static EditorOptionBuilder getBuilder() {
        return new EditorOptionBuilder()
    }

    static final class EditorOptionBuilder {
        private EditorOption editorOption

        EditorOptionBuilder() {
            this.editorOption = new EditorOption()
        }

        EditorOptionBuilder onDropAction(MethodClosure c, Map<String, ? extends Serializable> parameters) {
            editorOption.uploadFileAction = c
            editorOption.uploadFileActionParams = parameters
            this
        }

        EditorOptionBuilder addSpanRegexes(SpanRegex... spanRegexes) {
            editorOption.spanRegexes.addAll(spanRegexes)
            this
        }

        EditorOptionBuilder addSAutocompletes(AutoComplete... autoCompletes) {
            editorOption.autoCompletes.addAll(autoCompletes)
            this
        }

        EditorOptionBuilder addMenuEntries(MenuEntry... menuEntries) {
            editorOption.menuEntries.addAll(menuEntries)
            this
        }

        EditorOption build() {
            editorOption
        }
    }

    String serializeString() {
        "${uploadFileAction ? '/' + Utils.getControllerName(uploadFileAction) + '/' + uploadFileAction.method : ''}${uploadFileActionParams ? '?' + Utils.paramsString(uploadFileActionParams): ''}\n\n§§Style\n${((spanRegexes*.serializeString()).join(''))}\n§§Autocomplete\n${((autoCompletes*.serializeString()).join(''))}\n§§MenuEntry\n${((menuEntries*.serializeString()).join(''))}"
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