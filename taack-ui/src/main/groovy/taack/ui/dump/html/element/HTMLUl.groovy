package taack.ui.dump.html.element

import groovy.transform.CompileStatic

@CompileStatic
final class HTMLUl implements IHTMLElement {
    @Override
    String getTag() {
        'ul'
    }
}
