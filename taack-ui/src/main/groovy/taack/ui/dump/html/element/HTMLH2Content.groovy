package taack.ui.dump.html.element

import groovy.transform.CompileStatic

@CompileStatic
final class HTMLH2Content implements IHTMLElement {

    final String content

    HTMLH2Content(String content) {
        this.content = content ?: ''
    }

    @Override
    void getOutput(StringBuffer res) {
        res.append('<h2>' + content + '</h2>')
    }

    @Override
    void addChildren(IHTMLElement... elements) {
        throw new Exception('NO Children for this tag !!')
    }
}
