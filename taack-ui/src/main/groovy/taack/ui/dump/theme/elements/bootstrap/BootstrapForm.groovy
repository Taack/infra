package taack.ui.dump.theme.elements.bootstrap

import groovy.transform.CompileStatic
import taack.ui.dump.theme.elements.base.IHTMLElement
import taack.ui.dump.theme.elements.form.IFormThemed

@CompileStatic
final class BootstrapForm implements IFormThemed {

    BootstrapForm() {
        constructorIFormThemed()
    }

    @Override
    IHTMLElement enumInput() {
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
