package taack.ui.dump.html.base

import groovy.transform.CompileStatic
import taack.ui.dump.html.IJavascriptDescriptor
import taack.ui.dump.html.StyleDescriptor

@CompileStatic
enum TaackTag {
    BLOCK,
    INNER_BLOCK,
    AJAX_BLOCK,
    FORM,
    COL,
    TAB,
    TABS,
    SECTION
}

@CompileStatic
trait IHTMLElement {
    String id
    TaackTag taackTag
    String[] classes = []
    final Map<String, String> attributes = [:]
    IJavascriptDescriptor onClick
    StyleDescriptor styleDescriptor
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
        ret
    }

    Map<String, String> getAllAttributes() {
        Map res = this.attributes
        if (taackTag)
            res += ['taackTag': taackTag.toString()]
        if (classes)
            res += ['class': classes.join(' ')]
        res
    }

    String getOutput() {
        "<$tag ${allAttributes.collect { Map.Entry<String, String> it -> it.value ? "${it.key}=\"${it.value}\"" : "${it.key}" }.join(' ')}>" + "${children*.output.join("\n")}" + "\n</$tag>"
    }

    <T extends IHTMLElement> HTMLElementBuilder<T> getBuilder() {
        return new HTMLElementBuilder<T>(this as T)
    }

    static final class HTMLElementBuilder<T extends IHTMLElement> {
        private T element

        HTMLElementBuilder(T element) {
            this.element = element
        }

        HTMLElementBuilder<T> setId(String id) {
            element.id = id
            this
        }

        HTMLElementBuilder<T> setTaackTag(TaackTag taackTag) {
            element.taackTag = taackTag
            this
        }

        HTMLElementBuilder<T> addClasses(String... aClasses) {
            element.addClasses aClasses
            this
        }

        HTMLElementBuilder<T> putAttribute(String key, String value) {
            element.attributes.put key, value
            this
        }

        HTMLElementBuilder<T> setOnclick(IJavascriptDescriptor onClick) {
            element.onClick = onClick
            this
        }

        HTMLElementBuilder<T> setStyle(StyleDescriptor styleDescriptor) {
            element.styleDescriptor = styleDescriptor
            this
        }

        HTMLElementBuilder<T> addChildren(IHTMLElement... elements) {
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
