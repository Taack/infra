package taack.ui.dump.html.element

import groovy.transform.CompileStatic

@CompileStatic
final class HTMLImg implements IHTMLElement {
    HTMLImg(String src) {
        attributes.put('src', src)
    }

    @Override
    String getTag() {
        'img'
    }
}
