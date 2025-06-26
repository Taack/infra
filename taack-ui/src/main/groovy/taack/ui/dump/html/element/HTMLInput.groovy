package taack.ui.dump.html.element

import groovy.transform.CompileStatic

@CompileStatic
enum InputType {
    STRING('text'),
    DATE('date'),
    DATETIME('datetime-local'),
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
        putAttr('value', value != null ? value.toString() : '')
        putAttr('type', inputType.typeText)
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
        'input'
    }
}