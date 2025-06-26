package taack.ui.dump.html.element

import groovy.transform.CompileStatic

@CompileStatic
final class HTMLDatalist implements IHTMLElement {

    @Override
    String getTag() {
        'datalist'
    }
}
