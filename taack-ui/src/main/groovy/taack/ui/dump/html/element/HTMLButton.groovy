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

    HTMLButton(String i18n) {
        this(null, i18n)
        attributes.put('type', 'button')
    }

    HTMLButton(String url, String i18n, ButtonStyle style = null) {
        if (url) {
            attributes.put('formaction', url)
            addClasses('btn', 'w-75', 'mb-1')
        }

        addChildren(new HTMLTxtContent(i18n))
        if (style) addClasses(style.classString)
    }

    @Override
    String getTag() {
        'button'
    }
}
