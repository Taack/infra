package taack.ui.dump.html.table

import groovy.transform.CompileStatic
import taack.ui.dump.html.element.IHTMLElement

@CompileStatic
final class HTMLHead implements IHTMLElement {
    HTMLHead() {
        tag = 'thead'
    }
}
