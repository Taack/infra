package taack.ui.dump.theme.elements.block

import groovy.transform.CompileStatic
import taack.ui.dump.theme.elements.base.IHTMLElement

@CompileStatic
final class HTMLAjaxCloseModal implements IHTMLElement {

    HTMLAjaxCloseModal() {
    }

    @Override
    String getOutput() {
        "__closeLastModalAndUpdateBlock__:" + "${children*.output.join("\n")}"
    }
}
