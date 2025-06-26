package taack.ui.dump.html.element


import groovy.transform.CompileStatic
import taack.ui.dump.html.style.Height240

@CompileStatic
final class HTMLTextarea implements IHTMLElement {


    HTMLTextarea(Object value, String name, String placeHolder = null, boolean disabled = false, boolean readonly = false) {
        if (value) {
            addChildren(
                    new HTMLTxtContent(value.toString())
            )
        }
        addClasses('form-control')
//        putAttr('value', value?.toString())
        putAttr('rows', '8')
        styleDescriptor = new Height240()
        if (name) putAttr('name', name)
        if (placeHolder) putAttr('placeHolder', placeHolder)
        if (readonly) putAttr('readonly', null)
        if (disabled) putAttr('disabled', null)
    }

    static HTMLInput inputCheck(Object value, String name, boolean checked = false, boolean disabled = false) {
        HTMLInput ret = new HTMLInput(InputType.CHECK, value, name, null, disabled)
        if (checked) ret.putAttr('checked', null)
        ret
    }

    static HTMLInput inputRadio(Object value, String name, boolean checked = false, boolean disabled = false) {
        HTMLInput ret = new HTMLInput(InputType.RADIO, value, name, null,  disabled)
        if (checked) ret.putAttr('checked', 'checked')
        ret
    }

    @Override
    String getTag() {
        'textarea'
    }
}