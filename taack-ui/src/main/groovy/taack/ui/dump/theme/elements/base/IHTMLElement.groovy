package taack.ui.dump.theme.elements.base

import groovy.transform.CompileStatic
import taack.ui.dump.theme.elements.IJavascriptDescriptor

@CompileStatic
enum TaackTag {
    FORM,
    SECTION
}

@CompileStatic
trait IHTMLElement {
    String id
    TaackTag taackTag
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

    <T extends IHTMLElement> HTMLElementBuilder<T> getBuilder() {
        return new HTMLElementBuilder(this)
    }

    static final class HTMLElementBuilder<T extends IHTMLElement> {
        private T element

        HTMLElementBuilder(T element) {
            this.element = element
        }

        HTMLElementBuilder setId(String id) {
            element.id = id
            this
        }

        HTMLElementBuilder setTaackTag(TaackTag taackTag) {
            element.taackTag = taackTag
            this
        }

        HTMLElementBuilder addClasses(String... aClasses) {
            element.addClasses aClasses
            this
        }

        HTMLElementBuilder putAttribute(String key, String value) {
            element.attributes.put key, value
            this
        }

        HTMLElementBuilder setOnclick(IJavascriptDescriptor onClick) {
            element.onClick = onClick
            this
        }

        HTMLElementBuilder addChildren(IHTMLElement... elements) {
            for (IHTMLElement e in elements) {
                e.parent = this.element
            }
            element.children += elements
            this
        }

        T build() {
            element
        }
    }
}