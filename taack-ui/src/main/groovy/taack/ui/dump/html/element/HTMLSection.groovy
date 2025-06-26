package taack.ui.dump.html.element

import groovy.transform.CompileStatic

@CompileStatic
final class HTMLSection implements IHTMLElement {
    @Override
    String getTag() {
        'section'
    }
}
