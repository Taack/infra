package taack.ui.dump.html.element

import groovy.transform.CompileStatic
import org.springframework.web.util.UriComponentsBuilder

@CompileStatic
final class HTMLAnchor implements IHTMLElement {
    HTMLAnchor(boolean isAjax = true, String url = null) {
        if (url)
            if (isAjax) {
                putAttr('ajaxAction', url)
                putAttr('href', UriComponentsBuilder.newInstance().path(url).replaceQueryParam('isAjax', 'false').build().toString())
            } else {
                putAttr('href', url)
            }
    }

    @Override
    String getTag() {
        'a'
    }
}
