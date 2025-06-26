package taack.ui.dump.html.element

import groovy.transform.CompileStatic

@CompileStatic
final class HTMLSpan implements IHTMLElement {

    @Override
    String getTag() {
        'span'
    }
}
