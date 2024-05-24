package taack.ui.dump.theme.elements.base

import groovy.transform.CompileStatic

enum HTMLButtonType {
    PRIMARY, SECONDARY
}

@CompileStatic
class HTMLButton implements IHTMLElement {
    HTMLButton(HTMLButtonType buttonType) {
        tag = 'button'
    }
}
