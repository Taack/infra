package taack.ui.dump.html.element


import groovy.transform.CompileStatic
import taack.ui.EnumOption
import taack.ui.IEnumOption
import taack.ui.IEnumOptions

@CompileStatic
final class HTMLOption implements IHTMLElement {
    boolean isOptGroup = false
    HTMLOption(IEnumOption option, Boolean selected) {
        if (option.isSection()) {
            isOptGroup = true
            putAttr('label', option.value)
        } else {
            putAttr('value', option.key)
            if (selected) putAttr('selected', null)
            addChildren(new HTMLTxtContent(option.value))
        }
    }

    @Override
    String getTag() {
        if (isOptGroup) {
            'optgroup'
        } else {
            'option'
        }
    }
}

@CompileStatic
final class HTMLSelect implements IHTMLElement {

    HTMLSelect(IEnumOptions options, boolean multiple = false, boolean disabled = false, String defaultOptionLabel = null) {
        if (defaultOptionLabel != null) addChildren(new HTMLOption(new EnumOption(null, defaultOptionLabel), false))
        addChildren(options.options.toList().collect {
            new HTMLOption(it, options.currents*.key?.contains(it.key))
        } as HTMLOption[])
        putAttr('name', options.paramKey)
        if (disabled) putAttr('disabled', null)
        if (multiple) {
            putAttr('multiple', null)
            putAttr('style', 'height: 200px')
        }
    }

    @Override
    String getTag() {
        'select'
    }
}
