package taack.ui.dump.theme.elements.base

import groovy.transform.CompileStatic

@CompileStatic
final class HTMLButton implements IHTMLElement {
    HTMLButton(String url, String i18n) {
        tag = 'button'
        attributes.put('formaction', url)
        addChildren(new HTMLTxtContent(i18n))
    }
}
