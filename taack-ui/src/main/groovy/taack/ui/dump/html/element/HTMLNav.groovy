package taack.ui.dump.html.element

import groovy.transform.CompileStatic

@CompileStatic
final class HTMLNav implements IHTMLElement {
    @Override
    String getTag() {
        'nav'
    }
}
