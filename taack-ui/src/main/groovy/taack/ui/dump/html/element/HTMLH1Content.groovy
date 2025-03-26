package taack.ui.dump.html.element

import groovy.transform.CompileStatic

@CompileStatic
final class HTMLH1Content implements IHTMLElement {

    final String content

    HTMLH1Content(String content) {
        tag = null
        this.content = content ?: ''
    }

    @Override
    void getOutput(StringBuffer res) {
        res.append('<h1>' + content + '</h1>')
    }

    @Override
    void addChildren(IHTMLElement... elements) {
        throw new Exception("NO Children for this tag !!")
    }
}
