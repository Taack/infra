package taack.ui.dump.theme.elements.base

import groovy.transform.CompileStatic

@CompileStatic
class HTMLTxtContent implements IHTMLElement {

    final String content

    HTMLTxtContent(String content) {
        tag = null
        this.content = content
    }
}
