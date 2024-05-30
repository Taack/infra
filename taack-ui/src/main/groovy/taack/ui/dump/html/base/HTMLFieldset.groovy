package taack.ui.dump.html.base

import groovy.transform.CompileStatic

@CompileStatic
final class HTMLFieldset implements IHTMLElement {
    HTMLFieldset() {
        tag = 'fieldset'
    }
}
