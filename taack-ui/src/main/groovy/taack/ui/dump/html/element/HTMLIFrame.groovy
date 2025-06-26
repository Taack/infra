package taack.ui.dump.html.element

import groovy.transform.CompileStatic

@CompileStatic
final class HTMLIFrame implements IHTMLElement {
    HTMLIFrame(String url, String height = null) {
        putAttr('src', url)
        putAttr('width', '100%')
        if (height) putAttr('height', height)
    }

    @Override
    String getTag() {
        'iframe'
    }
}
