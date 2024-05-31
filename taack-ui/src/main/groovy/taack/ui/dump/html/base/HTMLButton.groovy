package taack.ui.dump.html.base

import groovy.transform.CompileStatic

@CompileStatic
final class HTMLButton implements IHTMLElement {
    HTMLButton(String url, String i18n) {
        tag = 'button'
        if (url) attributes.put('formaction', url)
        addChildren(new HTMLTxtContent(i18n))
    }

    static HTMLButton reset() {
        new HTMLButton(null, 'Reset').builder.putAttribute('type', 'reset').build() as HTMLButton
    }
}
