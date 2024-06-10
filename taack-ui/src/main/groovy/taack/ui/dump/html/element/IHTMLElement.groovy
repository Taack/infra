package taack.ui.dump.html.element

import groovy.transform.CompileStatic
import taack.ui.dump.html.script.IJavascriptDescriptor
import taack.ui.dump.html.style.IStyleDescriptor

@CompileStatic
enum TaackTag {
    BLOCK,
    MENU,
    MENU_OPTION,
    INNER_BLOCK,
    AJAX_BLOCK,
    FORM,
    FILTER,
    COL,
    ROW,
    TAB,
    TABS,
    LABEL,
    SECTION
}

@CompileStatic
trait IHTMLElement {
    String id
    TaackTag taackTag
    String[] classes = []
    final Map<String, String> attributes = [:]
    IJavascriptDescriptor onClick
    IStyleDescriptor styleDescriptor
    IHTMLElement[] children = []
    IHTMLElement parent
    String tag

    void addClasses(String... aClasses) {
        classes += aClasses
    }

    void addChildren(IHTMLElement... elements) {
        if (elements) children += elements
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
        if (id)
            res += ['id': id]
        if (taackTag)
            res += ['taackTag': taackTag.toString()]
        if (classes)
            res += ['class': classes.join(' ')]
        if (onClick)
            res += ['onclick': onClick.output]
        if (styleDescriptor)
            res += ['style': styleDescriptor.output]
        res
    }

    String getOutput() {
        if (tag)
            "<$tag ${allAttributes.collect { Map.Entry<String, String> it -> it.value ? "${it.key}=\"${it.value}\"" : "${it.key}" }.join(' ')}>" + "${children*.output.join("\n")}" + "\n</$tag>"
        else
            children*.output.join("\n")
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

        HTMLElementBuilder<T> setStyle(IStyleDescriptor styleDescriptor) {
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
