package taack.ui.dump.theme.elements.base


import groovy.transform.CompileStatic
import taack.ui.IEnumOption
import taack.ui.IEnumOptions

@CompileStatic
final class HTMLOption implements IHTMLElement {

    HTMLOption(IEnumOption option, boolean selected) {
        if (option.isSection()) {
            tag = 'optgroup'
            attributes.put('label', option.value)
        } else {
            tag = 'option'
            attributes.put('value', option.key)
            if (selected) attributes.put('selected', null)
            addChildren(new HTMLTxtContent(option.value))
        }
    }
}

@CompileStatic
final class HTMLSelect implements IHTMLElement {

    final List<HTMLOption> options

    HTMLSelect(IEnumOptions options, boolean multiple = false, boolean readOnly = false, boolean disabled = false, String... val) {
        tag = 'select'
        this.options = options.options.collect { new HTMLOption(it, val.contains(it.key)) }
        attributes.put('name', options.paramKey)
        if (readOnly) attributes.put('readonly', null)
        if (disabled) attributes.put('disabled', null)
    }

}