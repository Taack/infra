package taack.ui.dump.html.base


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

    HTMLSelect(IEnumOptions options, boolean multiple = false, boolean readOnly = false, boolean disabled = false) {
        tag = 'select'
        addChildren(options.options.toList().collect {
            new HTMLOption(it, options.currents.contains(it.key))
        } as HTMLOption[])
        attributes.put('name', options.paramKey)
        if (readOnly) attributes.put('readonly', null)
        if (disabled) attributes.put('disabled', null)
    }
}
