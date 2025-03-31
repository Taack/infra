package taack.ui.dump.html.element

import groovy.transform.CompileStatic

@CompileStatic
final class HTMLAnchor implements IHTMLElement {
    HTMLAnchor(boolean isAjax = true, String url = null) {
        tag = 'a'
        if (url)
            attributes.put('href', url)
            if (isAjax)
                attributes.put('ajaxAction', url)
    }
}
