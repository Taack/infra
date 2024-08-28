package taack.ui.dump.html.element

import groovy.transform.CompileStatic

@CompileStatic
final class HTMLTxtContent implements IHTMLElement {

    final String content

    HTMLTxtContent(String content) {
        tag = null
        this.content = content ?: ''
    }

    @Override
    String getOutput() {
        content
    }
}
