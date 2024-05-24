package taack.ui.dump.theme.elements.base

import groovy.transform.CompileStatic

@CompileStatic
enum HTMLInputType {
    STRING('text'), EMAIL('email'), TEXTAREA('textarea')

    HTMLInputType(String typeText) {
        this.typeText = typeText
    }

    final String typeText
}

@CompileStatic
class HTMLInput implements IHTMLElement {

    final Object value

    HTMLInput(HTMLInputType inputType, Object value, String placeHolder, boolean readOnly, boolean disabled) {
        tag = 'input'
        this.value = value
        attributes.put('type', inputType.typeText)
        if (placeHolder) attributes.put('placeHolder', placeHolder)
        if (readOnly) attributes.put('readonly', null)
        if (disabled) attributes.put('disabled', null)
    }
}