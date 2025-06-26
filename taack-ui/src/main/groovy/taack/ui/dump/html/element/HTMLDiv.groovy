package taack.ui.dump.html.element

import groovy.transform.CompileStatic

@CompileStatic
final class HTMLDiv implements IHTMLElement {
    @Override
    String getTag() {
        'div'
    }
}
