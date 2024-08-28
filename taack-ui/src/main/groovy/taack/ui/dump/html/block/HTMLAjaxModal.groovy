package taack.ui.dump.html.block

import groovy.transform.CompileStatic
import taack.ui.dump.html.element.IHTMLElement

@CompileStatic
final class HTMLAjaxModal implements IHTMLElement {

    boolean refresh

    HTMLAjaxModal(boolean refresh) {
        this.refresh = refresh
    }

    @Override
    String getOutput() {
//        (refresh ? "__refreshModal__:" : "__openModal__:") + children*.output.join("\n")
        (refresh ? "" : "__openModal__:") + children*.output.join("\n")
    }
}
