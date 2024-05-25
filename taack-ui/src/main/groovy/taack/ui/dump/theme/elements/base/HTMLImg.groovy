package taack.ui.dump.theme.elements.base

import groovy.transform.CompileStatic

@CompileStatic
final class HTMLImg implements IHTMLElement {
    HTMLImg(String src) {
        tag = 'img'
        attributes.put('src', src)
    }
}
