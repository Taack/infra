package taack.ui.dump.html.layout

import groovy.transform.CompileStatic
import taack.ui.dump.html.element.IHTMLElement

@CompileStatic
final class HTMLEmpty implements IHTMLElement {


    HTMLEmpty() {
    }

    @Override
    void getOutput(OutputStream out) {
        children*.getOutput(out)
    }
}
