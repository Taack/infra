package taack.ui.dump.html.element

import groovy.transform.CompileStatic

@CompileStatic
final class HTMLForm implements IHTMLElement {
    HTMLForm(String action) {
        tag = 'form'
        attributes.put('action', action)
    }
}
