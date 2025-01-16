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
    void getOutput(StringBuffer res) {
        if (refresh) res.append("__openModal__:")
        children*.getOutput(res)
    }
}
