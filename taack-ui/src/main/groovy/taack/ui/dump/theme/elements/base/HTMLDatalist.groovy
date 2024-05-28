package taack.ui.dump.theme.elements.base

import groovy.transform.CompileStatic

@CompileStatic
final class HTMLDatalist implements IHTMLElement {
    HTMLDatalist() {
        tag = 'datalist'
    }

    HTMLElementBuilder<HTMLDatalist> getBuilder() {
        return new HTMLElementBuilder<HTMLDatalist>(this)
    }

}
