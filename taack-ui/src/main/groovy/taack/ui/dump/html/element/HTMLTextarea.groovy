package taack.ui.dump.html.element


import groovy.transform.CompileStatic
import taack.ui.dump.html.style.Height240

@CompileStatic
final class HTMLTextarea implements IHTMLElement {


    HTMLTextarea(Object value, String name, String placeHolder = null, boolean disabled = false, boolean readonly = false) {
        tag = 'textarea'
        if (value) {
            addChildren(
                    new HTMLTxtContent(value.toString())
            )
        }
        attributes.put('value', value?.toString())
        attributes.put('rows', '6')
        styleDescriptor = new Height240()
        if (name) attributes.put('name', name)
        if (placeHolder) attributes.put('placeHolder', placeHolder)
        if (readonly) attributes.put('readonly', null)
        if (disabled) attributes.put('disabled', null)
    }

    static HTMLInput inputCheck(Object value, String name, boolean checked = false, boolean disabled = false) {
        HTMLInput ret = new HTMLInput(InputType.CHECK, value, name, null, disabled)
        if (checked) ret.attributes.put('checked', null)
        ret
    }

    static HTMLInput inputRadio(Object value, String name, boolean checked = false, boolean disabled = false) {
        HTMLInput ret = new HTMLInput(InputType.RADIO, value, name, null,  disabled)
        if (checked) ret.attributes.put('checked', 'checked')
        ret
    }
}