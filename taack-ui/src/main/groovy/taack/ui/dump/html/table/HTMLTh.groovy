package taack.ui.dump.html.table

import groovy.transform.CompileStatic
import taack.ui.dump.html.element.IHTMLElement

@CompileStatic
final class HTMLTh implements IHTMLElement {
    HTMLTh(Integer colspan = null, Integer rowspan = null) {
        if (rowspan) attributes.put('rowspan', rowspan.toString())
        if (colspan) attributes.put('colspan', colspan.toString())
    }

    @Override
    String getTag() {
        'th'
    }
}
