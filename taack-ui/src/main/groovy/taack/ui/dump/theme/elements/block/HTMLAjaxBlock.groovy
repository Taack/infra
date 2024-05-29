package taack.ui.dump.theme.elements.block

import groovy.transform.CompileStatic
import taack.ui.dump.theme.elements.base.IHTMLElement

@CompileStatic
final class HTMLAjaxBlock implements IHTMLElement {


    HTMLAjaxBlock(String id) {
        this.id = id
    }

    @Override
    String getOutput() {
        "__ajaxBlockStart__$id:" + "${children*.output.join("\n")}" + "__ajaxBlockEnd__"
    }
}
