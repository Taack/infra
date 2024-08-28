package taack.ui.dump.html.block

import groovy.transform.CompileStatic
import taack.ui.dump.html.element.IHTMLElement

@CompileStatic
final class HTMLAjaxCloseLastModal implements IHTMLElement {

    String value

    HTMLAjaxCloseLastModal(String id, String value) {
        this.id = id
        this.value = value
    }

    @Override
    String getOutput() {
        "__closeLastModal__:${id?:''}:${value?:''}"
    }
}
