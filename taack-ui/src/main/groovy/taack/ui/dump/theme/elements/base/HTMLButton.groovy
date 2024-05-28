package taack.ui.dump.theme.elements.base

import groovy.transform.CompileStatic

enum HTMLButtonType {
    PRIMARY, SECONDARY
}

@CompileStatic
final class HTMLButton implements IHTMLElement {
    HTMLButton(HTMLButtonType buttonType) {
        tag = 'button'
    }

    HTMLElementBuilder<HTMLButton> getBuilder() {
        return new HTMLElementBuilder<HTMLButton>(this)
    }
}
