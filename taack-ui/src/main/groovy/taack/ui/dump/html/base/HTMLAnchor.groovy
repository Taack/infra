package taack.ui.dump.html.base

import groovy.transform.CompileStatic

@CompileStatic
final class HTMLAnchor implements IHTMLElement {
    HTMLAnchor(boolean isAjax, String url) {
        tag = 'a'
        if (isAjax) {
            attributes.put('ajaxAction', url)
        }
        else
            attributes.put('href', url)
    }
}
