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
    MENU_CONTEXTUAL,
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
    TaackTag taackTag
    String id
    private String classes
    private String attr
    Vector<IHTMLElement> children = new Vector<>()
    IHTMLElement parent

    String getTag() {
        return null
    }

    void putAttr(String key, String value) {
        if (attr && !attr.empty) attr += ' ' + key + '="' + value + '" '
        else attr = key + '="' + value + '" '
    }

    void resetClasses() {
        classes = ''
    }

    void putClass(String value) {
        if (classes && !classes.empty) classes += ' ' + value
        else classes = value
    }

    void setOnClick(IJavascriptDescriptor onc) {
        putAttr('onclick', onc.output)
    }

    void setStyleDescriptor(IStyleDescriptor sd) {
        if (sd.classes) putClass sd.classes
        putAttr('style', sd.styleOutput)

    }

    void addClasses(String... aClasses) {
        if (aClasses) putClass(aClasses.join(' '))
    }

    void addChildren(IHTMLElement... elements) {
        for (IHTMLElement e in elements) {
            e.parent = this
        }

        if (elements) children.addAll(elements)
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
            throw new Exception("ERROR IHTMLElement::toParentTaackTag ${this.taackTag?.toString() + ':' + this.attr} has no parent ${taackTags} ltt = ${ltt*.taackTag}")
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

    @Override
    String toString() {
        """IHTMLElement ${this.taackTag?.toString() + ':' + this.attr}"""
    }

    String indent() {
        String ret = '    '
        IHTMLElement p = this
        while (p.parent) {
            ret += ret
            p = p.parent
        }
        ret
    }

    void getOutput(ByteArrayOutputStream out) {
        if (tag) {
            out << '\n<' + tag
            if (taackTag) out << ' taackTag="' + taackTag.name() + '"'
            if (id) out << ' id="' + id + '"'
            if (classes) out << ' class="' + classes + '"'
            if (attr) out << ' ' + attr.trim()
            out << '>'
        }

        for (IHTMLElement c : children) {
            c.getOutput(out)
        }

        if (tag)
            out << '</' + tag + '>'
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
            putAttribute('id', id)
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
            element.putAttr(key, value)
            this
        }

        HTMLElementBuilder<T> putAttributeIfNotNull(String key, String value) {
            if (value && key) putAttribute key, value
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
