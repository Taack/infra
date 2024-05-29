package taack.ui.dump.theme.elements.block

import groovy.transform.CompileStatic
import taack.ui.dump.theme.elements.base.IHTMLElement

@CompileStatic
final class HTMLFieldInfo implements IHTMLElement {

    final String value

    HTMLFieldInfo(String id) {
        this.id = id
        this.value = value
    }

    @Override
    String getOutput() {
        ":__FieldInfo__:${id?:''}:${value?:''}:__FieldInfoEnd__"
    }
}
