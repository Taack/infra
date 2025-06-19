package taack.ui.dump.html.element

import groovy.transform.CompileStatic

@CompileStatic
final class HTMLIFrame implements IHTMLElement {
    HTMLIFrame(String url, String height = null) {
        tag = 'iframe'
        attributes.put('src', url)
        attributes.put('width', '100%')
        if (height) attributes.put('height', height)
    }
}
