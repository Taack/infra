package taack.ui.dump.html.block

import groovy.transform.CompileStatic
import taack.ui.dump.html.element.IHTMLElement

@CompileStatic
final class HTMLAjaxCloseModal implements IHTMLElement {

    HTMLAjaxCloseModal() {
    }

    @Override
    void getOutput(StringBuffer res) {
        res.append '__closeLastModalAndUpdateBlock__:'
        children*.getOutput(res)
    }
}
