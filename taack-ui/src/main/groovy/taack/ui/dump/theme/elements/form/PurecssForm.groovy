package taack.ui.dump.theme.elements.form

import groovy.transform.CompileStatic
import org.grails.datastore.gorm.GormEntity
import taack.ui.IEnumOptions
import taack.ui.dump.theme.elements.base.IHTMLElement

@CompileStatic
final class PurecssForm<T extends GormEntity<T>> implements IFormTheme<T> {

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
    IHTMLElement booleanInput(IHTMLElement topElement, String qualifiedName, boolean value) {
        return null
    }

    @Override
    IHTMLElement selects(IHTMLElement topElement, IEnumOptions choices, boolean multiple, boolean disable, boolean nullable, String... val) {
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
