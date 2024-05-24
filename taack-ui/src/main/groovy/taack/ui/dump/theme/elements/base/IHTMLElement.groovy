package taack.ui.dump.theme.elements.base

import taack.ui.dump.theme.elements.IJavascriptDescriptor

trait IHTMLElement {
    String id
    String[] classes = []
    final Map<String, String> attributes = [:]
    IJavascriptDescriptor onClick
    IHTMLElement[] children = []
    IHTMLElement parent
    String tag

    void addClasses(String... aClasses) {
        classes += aClasses
    }

    void addChildren(IHTMLElement... elements) {
        children += elements
    }

    String indent() {
        String ret = "    "
        IHTMLElement p = this
        while (p.parent) {
            ret += ret
            p = p.parent
        }
    }

    String getOutput() {
        """
        """
    }
}