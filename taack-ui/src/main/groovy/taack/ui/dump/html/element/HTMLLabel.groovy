package taack.ui.dump.html.element

import groovy.transform.CompileStatic

@CompileStatic
final class HTMLLabel implements IHTMLElement{
    HTMLLabel(String forEntry) {
        putAttr('for', forEntry)
    }

    HTMLLabel(String forEntry, String i18n) {
        this(forEntry)
        addChildren(new HTMLTxtContent(i18n))
    }

    @Override
    String getTag() {
        'label'
    }
}
