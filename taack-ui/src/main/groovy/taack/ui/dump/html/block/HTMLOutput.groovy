package taack.ui.dump.html.block

import taack.ui.dump.html.element.IHTMLElement

import java.nio.charset.Charset

class HTMLOutput implements IHTMLElement {

    final ByteArrayOutputStream out

    HTMLOutput(ByteArrayOutputStream out) {
        this.out = out
    }

    @Override
    void getOutput(StringBuffer res) {
        res.append out.toString(Charset.defaultCharset())
    }
}
