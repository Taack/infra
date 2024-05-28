package taack.ui.dump.theme.elements.form

import groovy.transform.CompileStatic
import org.grails.datastore.gorm.GormEntity
import taack.ui.IEnumOptions
import taack.ui.base.form.FormSpec
import taack.ui.dump.theme.elements.DisplayEnum
import taack.ui.dump.theme.elements.StyleDescriptor
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
    IHTMLElement inputOverride(IHTMLElement topElement, String qualifiedName, String val, String txt, String imgSrc, String previousElement) {
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
        topElement.addChildren(span.build())
        topElement
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
        topElement.children.last()
    }

    @Override
    IHTMLElement booleanInput(IHTMLElement topElement, String qualifiedName, boolean value) {
        topElement.addChildren(
                HTMLInput.inputCheck(value ? '1' : '0', qualifiedName, value).builder.setId("${qualifiedName}Check").build(),
        )
        topElement
    }

    @Override
    IHTMLElement selects(IHTMLElement topElement, IEnumOptions options, boolean multiple, boolean disable, boolean nullable, String... val) {
        HTMLSelect s = new HTMLSelect(options, multiple, false, disable, val)
        topElement.addChildren(s)
        topElement
    }

    @Override
    IHTMLElement listOrSetInput() {
        return null
    }

    @Override
    IHTMLElement dateInput(IHTMLElement topElement, String qualifiedName, Date value) {
        return null
    }

    @Override
    IHTMLElement textareaInput(IHTMLElement topElement, String qualifiedName, String value) {
        return null
    }

    @Override
    IHTMLElement fileInput(IHTMLElement topElement, String qualifiedName, String value) {
        return null
    }

    @Override
    IHTMLElement normalInput(IHTMLElement topElement, String qualifiedName, String value) {
        return null
    }

    @Override
    IHTMLElement formLabel(IHTMLElement topElement, String qualifiedName, String value) {
        topElement.addChildren(
                new HTMLDiv().builder
                        .addClasses('taackFieldError')
                        .putAttribute('taackFieldError', qualifiedName)
                        .setStyle(new StyleDescriptor()
                                .setDisplay(DisplayEnum.NONE)).build(),
                new HTMLLabel(qualifiedName).builder.addChildren(new HTMLTxtContent(value)).build())
        topElement
    }

    @Override
    IHTMLElement formTabs(IHTMLElement topElement, int tabIds, List<String> names, FormSpec.Width width) {

        HTMLInput[] radioList = new HTMLInput[names.size()]
        HTMLLi[] liList = new HTMLLi[names.size()]
        names.eachWithIndex { it, occ ->
            radioList[occ] = HTMLInput.inputRadio("pct-${tabIds}", occ == 0).builder.setId("tab${occ + 1}-f${tabIds}").addClasses("inputTab${occ + 1}").build() as HTMLInput
            liList[occ] = new HTMLLi().builder.addClasses("tab${occ + 1}").addChildren(
                    new HTMLLabel("tab${occ + 1}-f${tabIds}").builder.addChildren(
                            new HTMLTxtContent(it)
                    ).build()
            ).build() as HTMLLi
        }

        topElement.addChildren(
                new HTMLDiv().builder
                        .setTaackTag(TaackTag.TABS)
                        .addClasses('pc-tab', width.sectionCss)
                        .addChildren(radioList)
                        .addChildren(
                                new HTMLNav().builder.addChildren(
                                        new HTMLUl().builder.addChildren(liList).build()
                                ).build())
                        .addChildren(new HTMLSection())
                        .build()
        )
        topElement.children.last().children.last()
    }

    @Override
    IHTMLElement formTab(IHTMLElement topElement, int occ) {
        topElement.addChildren(
                new HTMLDiv().builder.setTaackTag(TaackTag.TAB).addClasses('tab' + occ).build()
        )
        topElement.children.last()
    }

    @Override
    IHTMLElement formCol(IHTMLElement topElement) {
//        . setTaackTag(TaackTag.COL)
        topElement.children.last()
    }

    @Override
    IHTMLElement formAction() {
        return null
    }

}
