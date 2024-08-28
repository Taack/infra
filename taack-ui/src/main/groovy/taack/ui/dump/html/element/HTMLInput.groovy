package taack.ui.dump.html.element

import groovy.transform.CompileStatic

@CompileStatic
enum InputType {
    STRING('text'),
    DATE('date'),
    EMAIL('email'),
    CHECK('checkbox'),
    RADIO('radio'),
    PASSWD('password'),
    HIDDEN('hidden'),
    FILE('file'),
    TEXTAREA('textarea')

    InputType(String typeText) {
        this.typeText = typeText
    }

    final String typeText
}

@CompileStatic
final class HTMLInput implements IHTMLElement {


    HTMLInput(InputType inputType, Object value, String name, String placeHolder = null, boolean disabled = false, boolean readonly = false) {
        tag = 'input'
        attributes.put('value', value?.toString())
        attributes.put('type', inputType.typeText)
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