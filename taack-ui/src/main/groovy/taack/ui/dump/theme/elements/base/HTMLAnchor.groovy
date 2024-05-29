package taack.ui.dump.theme.elements.base

import groovy.transform.CompileStatic

@CompileStatic
final class HTMLAnchor implements IHTMLElement {
    HTMLAnchor(boolean isAjax, String url) {
        tag = 'a'
        if (isAjax) {
            addClasses('taackAjaxLink')
            attributes.put('ajaxAction', url)
        }
        else
            attributes.put('href', url)
    }
}
