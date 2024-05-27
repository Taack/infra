package taack.ui.dump.theme.elements.form

import groovy.transform.CompileStatic
import org.grails.datastore.gorm.GormEntity
import taack.ui.IEnumOptions
import taack.ui.dump.theme.elements.base.*

@CompileStatic
final class BootstrapForm<T extends GormEntity<T>> implements IFormTheme<T> {

    BootstrapForm() {
        constructorIFormThemed()
    }

    @Override
    IHTMLElement enumInput() {
        return null
    }

    @Override
    IHTMLElement inputOverride(String qualifiedName, String val, String txt, String imgSrc, String previousElement) {
        HTMLElementBuilder span = new HTMLSpan().builder.addClasses('M2MParent').addChildren(
                new HTMLInput(InputType.HIDDEN, val, qualifiedName).builder.build(),
                new HTMLSpan().builder.addChildren(
                        new HTMLTxtContent(txt)
                ).build(),
                new HTMLImg('/assets/taack/icons/actions/delete.svg').builder.addClasses('deleteIconM2M', 'taackFormFieldOverrideM2O').putAttribute('taackOnclickInnerHTML', previousElement).build()
        )
        if (imgSrc) {
            span.addChildren(
                    new HTMLImg(imgSrc).builder.putAttribute('style', 'max-height: 112px; max-width: 112px;').build()
            )
        }
        span.build()
    }

    @Override
    IHTMLElement section(IHTMLElement topElement, String... classes) {
        topElement.addChildren(
                new HTMLDiv().builder.setTaackTag(TaackTag.SECTION).addClasses(classes)
                        .addChildren(
                                new HTMLFieldset().builder.addChildren(
                                ).build()
                        ).build()
        )
    }

    @Override
    IHTMLElement booleanInput(IHTMLElement topElement, String qualifiedName, boolean value) {
        topElement.addChildren(
                HTMLInput.inputCheck(value ? '1' : '0', qualifiedName, value).builder.setId("${qualifiedName}Check").build(),
        )
    }

    @Override
    IHTMLElement selects(IHTMLElement topElement, IEnumOptions options, boolean multiple, boolean disable, boolean nullable, String... val) {
        HTMLSelect s = new HTMLSelect(options, multiple, false, disable, val)
        topElement.addChildren(s)
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
