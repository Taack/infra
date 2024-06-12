package taack.ui.dump.html.element

import groovy.transform.CompileStatic

enum ButtonStyle {
    PRIMARY('btn-primary'),
    SUCCESS('btn-success'),
    SECONDARY('btn-secondary')

    ButtonStyle(String classString) {
        this.classString = classString
    }

    final String classString
}

@CompileStatic
final class HTMLButton implements IHTMLElement {
    HTMLButton(String url, String i18n, ButtonStyle style = ButtonStyle.SUCCESS) {
        tag = 'button'
        if (url) attributes.put('formaction', url)

        addChildren(new HTMLTxtContent(i18n))
        addClasses('btn', 'w-75', 'mb-1', style.classString)
    }
}
