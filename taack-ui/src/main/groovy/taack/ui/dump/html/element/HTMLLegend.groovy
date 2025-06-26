package taack.ui.dump.html.element

import groovy.transform.CompileStatic

@CompileStatic
final class HTMLLegend implements IHTMLElement {
    @Override
    String getTag() {
        'legend'
    }
}
