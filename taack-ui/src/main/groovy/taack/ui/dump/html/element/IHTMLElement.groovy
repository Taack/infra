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
    private final StringBuffer attr = new StringBuffer()
    private final StringBuffer classes = new StringBuffer()
    IJavascriptDescriptor onClick
    IStyleDescriptor styleDescriptor
    Vector<IHTMLElement> children = new Vector<>()
    IHTMLElement parent

    String getTag() {
        return null
    }

    void putAttr(String key, String value) {
        attr.append(' ' + key + '="' + value + '"')
    }

    void resetClasses() {
        classes.setLength(0)
    }

    void putClass(String value) {
        classes.append(' ' + value)

    }

    void setId(String id) {
        putAttr('id',id)
    }

    String getId() {
        int p = attr.indexOf('id=')
        if (p != -1) {
            int p1 = attr.indexOf('"', p)
            int p2 = attr.indexOf(' ', p)
            if (p2 != -1 && p1 > p2) return attr.substring(p, p2)
            else return attr.substring(p)
        }
        null
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

    void getOutput(StringBuffer childrenOutput = new StringBuffer(1024)) {
        if (tag) {
            if (taackTag) {
                putAttr('taackTag', taackTag.name())
            }
            if (styleDescriptor) {
                if (styleDescriptor.classes) putClass styleDescriptor.classes
                putAttr('style', styleDescriptor.styleOutput)
            }
            if (classes.length() > 0)
                putAttr('class', classes.toString())

            if (onClick) {
                putAttr('onclick', onClick.output)
            }

            childrenOutput.append('<' + tag)
            childrenOutput.append(attr)
            childrenOutput.append('>')
        }

        for (IHTMLElement c : children) {
            c.getOutput(childrenOutput)
        }

        if (tag)
            childrenOutput.append('</' + tag + '>')
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
