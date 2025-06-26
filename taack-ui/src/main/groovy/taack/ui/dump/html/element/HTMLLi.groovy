package taack.ui.dump.html.element

import groovy.transform.CompileStatic

@CompileStatic
final class HTMLLi implements IHTMLElement {
    @Override
    String getTag() {
        'li'
    }
}
