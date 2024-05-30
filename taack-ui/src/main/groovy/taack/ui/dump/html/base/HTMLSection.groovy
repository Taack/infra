package taack.ui.dump.html.base

import groovy.transform.CompileStatic

@CompileStatic
final class HTMLSection implements IHTMLElement {
    HTMLSection() {
        tag = 'section'
    }
}
