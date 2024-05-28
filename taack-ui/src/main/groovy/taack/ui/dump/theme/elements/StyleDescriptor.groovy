package taack.ui.dump.theme.elements

import groovy.transform.CompileStatic

@CompileStatic
enum DisplayEnum {
    NONE
}

final class StyleDescriptor {
    private DisplayEnum displayEnum

    StyleDescriptor setDisplay(DisplayEnum displayEnum) {
        this.displayEnum = displayEnum
        this
    }
}