package taack.ui.dump.html.element

import groovy.transform.CompileStatic
import taack.ui.dump.html.script.IJavascriptDescriptor
import taack.ui.dump.html.style.IStyleDescriptor

@CompileStatic
enum TaackTag {
    BLOCK,
    MENU,
    MODAL,
    MENU_SPLIT,
    MENU_OPTION,
    MENU_COL,
    MENU_BLOCK,
    INNER_BLOCK,
    POLL,
    TABLE,
    TABLE_COL,
    TABLE_ROW,
    TABLE_HEAD,
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
        for (IHTMLElement e in elements) {
            e.parent = this
        }

        if (elements) children += elements
    }

    IHTMLElement toParentTaackTag(TaackTag... taackTags) {
        if (taackTags.contains(taackTag)) return this
        IHTMLElement ret = this
        List<IHTMLElement> ltt = []
        while (ret && !taackTags.contains(ret.taackTag)) {
            ret = ret.parent
            ltt << ret
        }
        if (!ret) {
            throw new Exception("ERROR IHTMLElement::toParentTaackTag ${this.tag + ':' + this.taackTag + ':' + this.attributes} has no parent ${taackTags}, tags = ${ltt*.tag}, ltt = ${ltt*.taackTag}")
        }
        ret
    }

    boolean testParentTaackTag(TaackTag... taackTags) {
        if (taackTags.contains(taackTag)) return true
        IHTMLElement ret = this
        while (ret && !taackTags.contains(ret.taackTag)) {
            ret = ret.parent
        }
        taackTags.contains ret?.taackTag
    }

    List<IHTMLElement> getParents() {
        IHTMLElement ret = this
        List<IHTMLElement> ltt = []
        while (ret.parent) {
            ret = ret.parent
            ltt << ret
        }
        ltt
    }

    @Override
    String toString() {
        List<IHTMLElement> p = parents
        """IHTMLElement ${this.tag + ':' + this.taackTag + ':' + this.attributes}, ${p*.tag}, ${p*.taackTag}"""
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
        if (styleDescriptor) {
            if (styleDescriptor.classes) classes += styleDescriptor.classes.trim().split(/ +/)
            res += ['style': styleDescriptor.styleOutput]
        }
        if (classes)
            res += ['class': classes.grep { it != null} .join(' ')]
        if (onClick)
            res += ['onclick': onClick.output]
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
            this.element.addChildren(elements)
            this
        }

        T build() {
            element
        }
    }

}
