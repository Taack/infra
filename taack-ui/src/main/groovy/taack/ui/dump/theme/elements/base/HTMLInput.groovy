package taack.ui.dump.theme.elements.base

import groovy.transform.CompileStatic

@CompileStatic
enum InputType {
    STRING('text'),
    EMAIL('email'),
    HIDDEN('hidden'),
    TEXTAREA('textarea')

    InputType(String typeText) {
        this.typeText = typeText
    }

    final String typeText
}

@CompileStatic
class HTMLInput implements IHTMLElement {

    final Object value

    HTMLInput(InputType inputType, Object value, String name, String placeHolder = null, boolean readOnly = false, boolean disabled = false) {
        tag = 'input'
        this.value = value
        attributes.put('type', inputType.typeText)
        attributes.put('name', name)
        if (placeHolder) attributes.put('placeHolder', placeHolder)
        if (readOnly) attributes.put('readonly', null)
        if (disabled) attributes.put('disabled', null)
    }

}