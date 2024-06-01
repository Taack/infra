package taack.ui.dump.html.form

import groovy.transform.CompileStatic
import org.grails.datastore.gorm.GormEntity
import taack.ui.IEnumOptions
import taack.ui.base.form.FormSpec
import taack.ui.dump.html.base.IHTMLElement

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

    abstract IHTMLElement inputOverride(IHTMLElement topElement, String qualifiedName, String trI18n, String val, String txt, String imgSrc, IHTMLElement previousElement)

    abstract IHTMLElement section(IHTMLElement topElement, String trI18n, String... classes)

    abstract IHTMLElement booleanInput(IHTMLElement topElement, String qualifiedName, String trI18n, boolean disable, boolean nullable, Boolean value)

    abstract IHTMLElement selects(IHTMLElement topElement, String trI18n, String qualifiedName, IEnumOptions choices, boolean multiple, boolean disable, boolean nullable)

    abstract IHTMLElement ajaxField(IHTMLElement topElement, String trI18n, IEnumOptions choices, Object val, String qualifiedName, Long modalId, String url, String fieldInfoParams, boolean disable)

    abstract IHTMLElement ajaxField(IHTMLElement topElement, String trI18n, List<Object> vals, String qualifiedName, Long modalId, String url, String fieldInfoParams, boolean disabled, boolean nullable, boolean isMultiple)

    abstract IHTMLElement dateInput(IHTMLElement topElement, String qualifiedName, String trI18n, boolean disable, boolean nullable, Date value)

    abstract IHTMLElement textareaInput(IHTMLElement topElement, String qualifiedName, String trI18n, boolean disable, boolean nullable, String value)

    abstract IHTMLElement fileInput(IHTMLElement topElement, String qualifiedName, String trI18n, boolean disable, boolean nullable, String value)

    abstract IHTMLElement normalInput(IHTMLElement topElement, String qualifiedName, String trI18n, boolean disable, boolean nullable, String value)

    abstract IHTMLElement passwdInput(IHTMLElement topElement, String qualifiedName, String trI18n, boolean disable, boolean nullable, String value)

    abstract IHTMLElement formTabs(IHTMLElement topElement, int tabIds, List<String> names, FormSpec.Width width)

    abstract IHTMLElement formTab(IHTMLElement topElement, int occ)

    abstract IHTMLElement formCol(IHTMLElement topElement)

    abstract IHTMLElement formAction(IHTMLElement topElement, String url, String i18n)
}