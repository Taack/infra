package taack.ui.dump.html.table

import groovy.transform.CompileStatic
import taack.ui.dump.html.element.IHTMLElement

@CompileStatic
final class HTMLTr implements IHTMLElement {
    HTMLTr(Integer colspan = null) {
        tag = 'tr'
        if (colspan) attributes.put('colspan', colspan.toString())
    }
}
