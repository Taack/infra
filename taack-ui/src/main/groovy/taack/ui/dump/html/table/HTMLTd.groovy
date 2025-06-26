package taack.ui.dump.html.table

import groovy.transform.CompileStatic
import taack.ui.dump.html.element.IHTMLElement

@CompileStatic
final class HTMLTd implements IHTMLElement {
    HTMLTd(Integer colspan = null, Integer rowspan = null) {
        if (colspan) attributes.put('colspan', colspan.toString())
        if (rowspan) attributes.put('rowspan', rowspan.toString())
    }

    @Override
    String getTag() {
        'td'
    }
}
