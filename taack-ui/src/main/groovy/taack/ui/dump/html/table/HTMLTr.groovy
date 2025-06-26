package taack.ui.dump.html.table

import groovy.transform.CompileStatic
import taack.ui.dump.html.element.IHTMLElement

@CompileStatic
final class HTMLTr implements IHTMLElement {
    HTMLTr(Integer colspan = null) {
        if (colspan) attributes.put('colspan', colspan.toString())
    }

    @Override
    String getTag() {
        'tr'
    }
}
