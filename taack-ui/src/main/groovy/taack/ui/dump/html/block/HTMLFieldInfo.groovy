package taack.ui.dump.html.block

import groovy.transform.CompileStatic
import taack.ui.dump.html.element.IHTMLElement

@CompileStatic
final class HTMLFieldInfo implements IHTMLElement {

    final String value

    HTMLFieldInfo(String id, String value) {
        this.id = id
        this.value = value
    }

    @Override
    void getOutput(ByteArrayOutputStream out) {
        out << ":__FieldInfo__:${id?:''}:${value?:''}:__FieldInfoEnd__"
    }
}
