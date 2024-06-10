package taack.ui.dump.html.element

import groovy.transform.CompileStatic

@CompileStatic
final class HTMLLabel implements IHTMLElement{
    HTMLLabel(String forEntry) {
        tag = 'label'
        attributes.put('for', forEntry)
    }

    HTMLLabel(String forEntry, String i18n) {
        this(forEntry)
        addChildren(new HTMLTxtContent(i18n))
    }
}
