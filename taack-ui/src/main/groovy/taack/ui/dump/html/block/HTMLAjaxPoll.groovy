package taack.ui.dump.html.block

import groovy.transform.CompileStatic
import taack.ui.dump.html.element.IHTMLElement

@CompileStatic
final class HTMLAjaxPoll implements IHTMLElement {

    private final int millis
    private final String url

    HTMLAjaxPoll(int millis, String url) {
        this.millis = millis
        this.url = url
    }

    @Override
    void getOutput(OutputStream out) {
        out << "__poll__:$millis:$url:"
        children*.getOutput(out)
    }
}
