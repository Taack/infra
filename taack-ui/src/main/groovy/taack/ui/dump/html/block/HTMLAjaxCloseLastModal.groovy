package taack.ui.dump.html.block

import groovy.transform.CompileStatic
import taack.ui.dump.html.base.IHTMLElement

@CompileStatic
final class HTMLAjaxCloseLastModal implements IHTMLElement {

    final String value

    HTMLAjaxCloseLastModal(String id) {
        this.id = id
        this.value = value
    }

    @Override
    String getOutput() {
        "__closeLastModal__:${id?:''}:${value?:''}"
    }
}
