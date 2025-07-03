package taack.ui.dump.html.block

import groovy.transform.CompileStatic
import taack.ui.dump.html.element.IHTMLElement

@CompileStatic
final class HTMLAjaxModal implements IHTMLElement {

    boolean refresh
    boolean reloadWhenClose

    HTMLAjaxModal(boolean refresh, boolean reloadWhenClose) {
        this.refresh = refresh
        this.reloadWhenClose = reloadWhenClose
    }

    @Override
    void getOutput(OutputStream out) {
        if (!refresh) out << '__openModal__:'
        children*.getOutput(out)
        if (reloadWhenClose) out << '__reload__'
    }
}
