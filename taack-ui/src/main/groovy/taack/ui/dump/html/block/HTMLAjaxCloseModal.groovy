package taack.ui.dump.html.block

import groovy.transform.CompileStatic
import taack.ui.dump.html.base.IHTMLElement

@CompileStatic
final class HTMLAjaxCloseModal implements IHTMLElement {

    HTMLAjaxCloseModal() {
    }

    @Override
    String getOutput() {
        "__closeLastModalAndUpdateBlock__:" + "${children*.output.join("\n")}"
    }
}
