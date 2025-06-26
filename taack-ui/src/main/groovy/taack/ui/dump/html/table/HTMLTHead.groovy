package taack.ui.dump.html.table

import groovy.transform.CompileStatic
import taack.ui.dump.html.element.IHTMLElement

@CompileStatic
final class HTMLTHead implements IHTMLElement {
    @Override
    String getTag() {
        'thead'
    }
}
