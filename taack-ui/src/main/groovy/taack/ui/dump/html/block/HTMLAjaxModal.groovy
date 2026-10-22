package taack.ui.dump.html.block

import groovy.transform.CompileStatic
import org.springframework.web.util.HtmlUtils
import taack.ui.dump.html.element.IHTMLElement

@CompileStatic
final class HTMLAjaxModal implements IHTMLElement {

    boolean refresh
    boolean reloadWhenClose
    String title

    HTMLAjaxModal(boolean refresh, boolean reloadWhenClose, String title = null) {
        this.refresh = refresh
        this.reloadWhenClose = reloadWhenClose
        this.title = title
    }

    @Override
    void getOutput(OutputStream out) {
        if (!refresh) {
            out << '__openModal__:'
            if (title) out << '<span hidden taackModalTitle>' + HtmlUtils.htmlEscape(title) + '</span>'
        }
        else out << '__refreshModal__:'
        children*.getOutput(out)
        if (reloadWhenClose) out << '__reload__'
    }
}
