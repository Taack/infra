package taack.ui.dump.html.element

import groovy.transform.CompileStatic

@CompileStatic
final class HTMLTxtContent implements IHTMLElement {

    final String content

    HTMLTxtContent(String content) {
        this.content = content ?: ''
    }

    @Override
    void getOutput(OutputStream out) {
        out << content
    }

    @Override
    void addChildren(IHTMLElement... elements) {
        throw new Exception('NO Children for this tag !!')
    }
}
