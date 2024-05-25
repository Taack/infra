package taack.ui.dump.theme.elements.purecss

import groovy.transform.CompileStatic
import taack.ui.dump.theme.elements.base.IHTMLElement
import taack.ui.dump.theme.elements.form.IFormThemed

@CompileStatic
final class PurecssForm implements IFormThemed {
    @Override
    IHTMLElement enumInput() {
        return null
    }

    @Override
    IHTMLElement inputOverride(String qualifiedName, String val, String txt, String imgSrc, String previousElement) {
        return null
    }

    @Override
    IHTMLElement section(IHTMLElement topElement, String... classes) {
        return null
    }

    @Override
    IHTMLElement booleanInput() {
        return null
    }

    @Override
    IHTMLElement listOrSetInput() {
        return null
    }

    @Override
    IHTMLElement dateInput() {
        return null
    }

    @Override
    IHTMLElement textareaInput() {
        return null
    }

    @Override
    IHTMLElement fileInput() {
        return null
    }

    @Override
    IHTMLElement normalInput() {
        return null
    }

    @Override
    IHTMLElement formSection(IHTMLElement inner) {
        return null
    }

    @Override
    IHTMLElement formTabs(IHTMLElement inner) {
        return null
    }

    @Override
    IHTMLElement formTab(IHTMLElement inner) {
        return null
    }

    @Override
    IHTMLElement formCol(IHTMLElement inner) {
        return null
    }

    @Override
    IHTMLElement formAction() {
        return null
    }
}
