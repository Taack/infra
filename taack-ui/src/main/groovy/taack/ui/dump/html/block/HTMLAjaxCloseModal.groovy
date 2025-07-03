package taack.ui.dump.html.block

import groovy.transform.CompileStatic
import taack.ui.dump.html.element.IHTMLElement

@CompileStatic
final class HTMLAjaxCloseModal implements IHTMLElement {

    HTMLAjaxCloseModal() {
    }

    @Override
    void getOutput(OutputStream out) {
        out << '__closeLastModalAndUpdateBlock__:'
        children*.getOutput(out)
    }
}
