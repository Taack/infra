package taack.ui.dump.html.element

import groovy.transform.CompileStatic

@CompileStatic
final class HTMLFieldset implements IHTMLElement {
    @Override
    String getTag() {
        'fieldset'
    }
}
