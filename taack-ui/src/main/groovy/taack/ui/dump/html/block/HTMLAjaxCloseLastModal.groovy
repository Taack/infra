package taack.ui.dump.html.block

import groovy.transform.CompileStatic
import taack.ui.dump.html.element.IHTMLElement

@CompileStatic
final class HTMLAjaxCloseLastModal implements IHTMLElement {
    Map<String, String> idValueMap

    HTMLAjaxCloseLastModal(Map<String, String> idValueMap) {
        this.idValueMap = idValueMap
    }

    @Override
    void getOutput(ByteArrayOutputStream out) {
        out << '__closeLastModal__:'
        idValueMap.each { String id, String value ->
            out << "|${id?:''}:${value?:''}"
        }
    }
}
