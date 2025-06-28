package taack.ui.dump.html.block

import groovy.transform.CompileStatic
import taack.ui.dump.html.element.IHTMLElement

@CompileStatic
final class HTMLAjaxBlock implements IHTMLElement {


    HTMLAjaxBlock(String id) {
        this.id = id
    }

    @Override
    void getOutput(ByteArrayOutputStream out) {
        out << "__ajaxBlockStart__$id:"
        children*.getOutput(out)
        out << '__ajaxBlockEnd__'
    }
}
