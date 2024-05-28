package taack.ui.dump.theme.elements.base

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

    final Object value

    HTMLInput(InputType inputType, Object value, String name, String placeHolder = null, boolean readOnly = false, boolean disabled = false) {
        tag = 'input'
        this.value = value
        attributes.put('type', inputType.typeText)
        attributes.put('name', name)
        if (placeHolder) attributes.put('placeHolder', placeHolder)
        if (readOnly) attributes.put('readOnly', null)
        if (disabled) attributes.put('disabled', null)
    }

    static HTMLInput inputCheck(Object value, String name, boolean checked = false, boolean readOnly = false, boolean disabled = false) {
        HTMLInput ret = new HTMLInput(InputType.CHECK, value, name, null, readOnly, disabled)
        if (checked) ret.attributes.put('checked', null)
        ret
    }

    static HTMLInput inputRadio(String name, boolean checked = false, boolean readOnly = false, boolean disabled = false) {
        HTMLInput ret = new HTMLInput(InputType.RADIO, null, name, null, readOnly, disabled)
        if (checked) ret.attributes.put('checked', 'checked')
        ret
    }
}