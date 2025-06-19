package taack.ui.dump.html.element

import groovy.transform.CompileStatic
import org.springframework.web.util.UriComponentsBuilder

@CompileStatic
final class HTMLAnchor implements IHTMLElement {
    HTMLAnchor(boolean isAjax = true, String url = null) {
        tag = 'a'
        if (url)
            if (isAjax) {
                attributes.put('ajaxAction', url)
                attributes.put('href', UriComponentsBuilder.newInstance().path(url).replaceQueryParam('isAjax', 'false').build().toString())
            } else {
                attributes.put('href', url)
            }
    }
}
