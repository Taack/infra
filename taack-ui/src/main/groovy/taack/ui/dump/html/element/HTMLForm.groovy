package taack.ui.dump.html.element

import groovy.transform.CompileStatic

@CompileStatic
final class HTMLForm implements IHTMLElement {
    HTMLForm(String action) {
        putAttr('action', action)
    }

    @Override
    String getTag() {
        'form'
    }
}
