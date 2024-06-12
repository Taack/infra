package taack.ui.dump.html.table

import groovy.transform.CompileStatic
import taack.ui.dump.html.element.IHTMLElement

@CompileStatic
final class HTMLTBody implements IHTMLElement {
    HTMLTBody() {
        tag = 'tbody'
    }
}
