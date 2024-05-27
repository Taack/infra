package taack.ui.dump.theme.elements.form

import groovy.transform.CompileStatic
import org.grails.datastore.gorm.GormEntity
import taack.ui.IEnumOptions
import taack.ui.dump.theme.elements.base.IHTMLElement

@CompileStatic
enum InputMode {
    POST('post'), GET('get')

    InputMode(String modeText) {
        this.modeText = modeText
    }

    final String modeText

}

@CompileStatic
enum EncType {
    DATA('multipart/form-data')

    EncType(String text) {
        this.text = text
    }

    final String text
}

enum FormMethod {
    INLINE, STACKED, Aligned
}

@CompileStatic
trait IFormTheme<T extends GormEntity<T>> implements IHTMLElement {

    void constructorIFormThemed(InputMode inputMode = InputMode.POST, EncType encType = EncType.DATA) {
        attributes.put('method', inputMode.modeText)
        attributes.put('enctype', encType.text)
        addClasses('taackForm')
    }

    abstract <T extends IHTMLElement> HTMLElementBuilder<T> getBuilder()

    abstract IHTMLElement enumInput()
    abstract IHTMLElement inputOverride(String qualifiedName, String val, String txt, String imgSrc, String previousElement)
    abstract IHTMLElement section(IHTMLElement topElement, String... classes)
    abstract IHTMLElement booleanInput(IHTMLElement topElement, String qualifiedName, boolean value)
    abstract IHTMLElement selects(IHTMLElement topElement, IEnumOptions choices, boolean multiple, boolean disable, boolean nullable)
    abstract IHTMLElement listOrSetInput()
    abstract IHTMLElement dateInput()
    abstract IHTMLElement textareaInput()
    abstract IHTMLElement fileInput()
    abstract IHTMLElement normalInput()
    abstract IHTMLElement formSection(IHTMLElement inner)
    abstract IHTMLElement formTabs(IHTMLElement inner)
    abstract IHTMLElement formTab(IHTMLElement inner)
    abstract IHTMLElement formCol(IHTMLElement inner)
    abstract IHTMLElement formAction()
}