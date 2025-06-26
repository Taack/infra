package taack.ui.dump.html.table

import groovy.transform.CompileStatic
import taack.ui.dump.html.element.IHTMLElement

@CompileStatic
final class HTMLTd implements IHTMLElement {
    HTMLTd(Integer colspan = null, Integer rowspan = null) {
        if (colspan) putAttr('colspan', colspan.toString())
        if (rowspan) putAttr('rowspan', rowspan.toString())
    }

    @Override
    String getTag() {
        'td'
    }
}
