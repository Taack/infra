package taack.ui.dump.html.base

import groovy.transform.CompileStatic

@CompileStatic
final class HTMLLegend implements IHTMLElement {
    HTMLLegend() {
        tag = 'fieldset'
    }
}
