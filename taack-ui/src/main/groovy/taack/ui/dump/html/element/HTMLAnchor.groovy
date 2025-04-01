package taack.ui.dump.html.element

import groovy.transform.CompileStatic
import org.apache.http.client.utils.URIBuilder

@CompileStatic
final class HTMLAnchor implements IHTMLElement {
    HTMLAnchor(boolean isAjax = true, String url = null) {
        tag = 'a'
        if (url)
            if (isAjax) {
                attributes.put('ajaxAction', url)
                attributes.put('href', new URIBuilder(url).setParameter('isAjax', 'false').build().toString())
            } else {
                attributes.put('href', url)
            }
    }
}
