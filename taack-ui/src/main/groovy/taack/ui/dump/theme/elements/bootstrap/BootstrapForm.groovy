package taack.ui.dump.theme.elements.bootstrap

import groovy.transform.CompileStatic
import taack.ui.dump.theme.elements.base.HTMLDiv
import taack.ui.dump.theme.elements.base.HTMLFieldset
import taack.ui.dump.theme.elements.base.HTMLImg
import taack.ui.dump.theme.elements.base.HTMLInput
import taack.ui.dump.theme.elements.base.HTMLSpan
import taack.ui.dump.theme.elements.base.HTMLTxtContent
import taack.ui.dump.theme.elements.base.IHTMLElement
import taack.ui.dump.theme.elements.base.InputType
import taack.ui.dump.theme.elements.base.TaackTag
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
    IHTMLElement inputOverride(String qualifiedName, String val, String txt, String imgSrc, String previousElement) {
        IHTMLElement.HTMLElementBuilder span = new HTMLSpan().builder.addClasses('M2MParent').addChildren(
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
        new HTMLDiv().builder.setTaackTag(TaackTag.SECTION).addClasses(classes)
                .addChildren(
                        new HTMLFieldset().builder.addChildren(
                                topElement
                        ).build()
                ).build()
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
