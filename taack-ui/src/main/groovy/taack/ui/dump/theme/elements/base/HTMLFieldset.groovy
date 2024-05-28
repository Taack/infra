package taack.ui.dump.theme.elements.base

import groovy.transform.CompileStatic

@CompileStatic
final class HTMLFieldset implements IHTMLElement {
    HTMLFieldset() {
        tag = 'fieldset'
    }

    HTMLElementBuilder<HTMLFieldset> getBuilder() {
        return new HTMLElementBuilder<HTMLFieldset>(this)
    }
}
