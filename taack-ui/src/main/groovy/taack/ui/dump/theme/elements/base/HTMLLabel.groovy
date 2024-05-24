package taack.ui.dump.theme.elements.base

import groovy.transform.CompileStatic

@CompileStatic
class HTMLLabel implements IHTMLElement{
    HTMLLabel(String forEntry) {
        tag = 'label'
        attributes.put('for', forEntry)
    }
}
