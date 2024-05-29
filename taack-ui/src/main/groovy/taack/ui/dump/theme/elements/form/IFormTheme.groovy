package taack.ui.dump.theme.elements.form

import groovy.transform.CompileStatic
import org.grails.datastore.gorm.GormEntity
import taack.ui.IEnumOptions
import taack.ui.base.form.FormSpec
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
        tag = 'form'
        attributes.put('method', inputMode.modeText)
        attributes.put('enctype', encType.text)
        addClasses('taackForm')
    }

    abstract <T extends IHTMLElement> HTMLElementBuilder<T> getBuilder()

    abstract IHTMLElement inputOverride(IHTMLElement topElement, String qualifiedName, String val, String txt, String imgSrc, IHTMLElement previousElement)

    abstract IHTMLElement section(IHTMLElement topElement, String... classes)

    abstract IHTMLElement booleanInput(IHTMLElement topElement, String qualifiedName, boolean disable, boolean nullable, boolean value)

    abstract IHTMLElement selects(IHTMLElement topElement, IEnumOptions choices, boolean multiple, boolean disable, boolean nullable)

    abstract IHTMLElement ajaxField(IHTMLElement topElement, IEnumOptions choices, Object val, String qualifiedName, Long modalId, String url, String fieldInfoParams, boolean disable)

    abstract IHTMLElement ajaxField(IHTMLElement topElement, List<Object> vals, String qualifiedName, Long modalId, String url, String fieldInfoParams, boolean disabled, boolean nullable, boolean isMultiple)

    abstract IHTMLElement dateInput(IHTMLElement topElement, String qualifiedName, boolean disable, boolean nullable, Date value)

    abstract IHTMLElement textareaInput(IHTMLElement topElement, String qualifiedName, boolean disable, boolean nullable, String value)

    abstract IHTMLElement fileInput(IHTMLElement topElement, String qualifiedName, boolean disable, boolean nullable, String value)

    abstract IHTMLElement normalInput(IHTMLElement topElement, String qualifiedName, boolean disable, boolean nullable, String value)

    abstract IHTMLElement passwdInput(IHTMLElement topElement, String qualifiedName, boolean disable, boolean nullable, String value)

    abstract IHTMLElement formLabel(IHTMLElement topElement, String qualifiedName, String value)

    abstract IHTMLElement formTabs(IHTMLElement topElement, int tabIds, List<String> names, FormSpec.Width width)

    abstract IHTMLElement formTab(IHTMLElement topElement, int occ)

    abstract IHTMLElement formCol(IHTMLElement topElement)

    abstract IHTMLElement formAction(IHTMLElement topElement, String url, String i18n)
}