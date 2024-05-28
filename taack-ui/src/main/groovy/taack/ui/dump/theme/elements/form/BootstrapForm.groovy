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
    IHTMLElement inputOverride(IHTMLElement topElement, String qualifiedName, String val, String txt, String imgSrc, IHTMLElement previousElement) {
        HTMLElementBuilder span = new HTMLSpan().builder.addClasses('M2MParent').addChildren(
                new HTMLInput(InputType.HIDDEN, val, qualifiedName).builder.build(),
                new HTMLSpan().builder.addChildren(
                        new HTMLTxtContent(txt)
                ).build(),
                new HTMLImg('/assets/taack/icons/actions/delete.svg').builder.addClasses('deleteIconM2M', 'taackFormFieldOverrideM2O')./*putAttribute('taackOnclickInnerHTML', previousElement).*/build()
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
    IHTMLElement booleanInput(IHTMLElement topElement, String qualifiedName, boolean disable, boolean nullable, boolean value) {
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
    IHTMLElement ajaxField(IHTMLElement topElement, IEnumOptions choices, Object val, String qualifiedName, Long modalId, String url, String fieldInfoParams, boolean disable) {
        HTMLSelect s = new HTMLSelect(choices, false, false, disable, val as String)
        HTMLDiv d = new HTMLDiv()
        if (!disable) {
            d.addChildren(new HTMLImg('/assets/taack/icons/actions/delete.svg').builder.addClasses('deleteIconM2M').build())
        }
        d.addChildren(s)
        topElement.addChildren(s)
        topElement
    }

    private static String inputEscape(final String val) {
        val?.replace('"', '&quot;')?.replace('\'', '&#39;')?.replace('\n', '')?.replace('\r', '')
    }

    @Override
    IHTMLElement ajaxField(IHTMLElement topElement, List<Object> vals, String qualifiedName, Long modalId, String url, String fieldInfoParams, boolean disabled, boolean nullable, boolean isMultiple) {

        vals.each {
            boolean isString = String.isAssignableFrom(it.class)

            HTMLSpan span = new HTMLSpan().builder.addClasses('M2MParent').build() as HTMLSpan
            if (disabled) span.addChildren new HTMLImg('/assets/taack/icons/actions/delete.svg')
            span.addChildren(new HTMLInput(InputType.STRING, it ? inputEscape(it.toString()) : '', qualifiedName, null, nullable, disabled))
            span.addChildren(new HTMLInput(InputType.HIDDEN, it ? (isString ? it : it['id']) : '', qualifiedName, null, nullable, disabled))
            topElement.addChildren(span)
        }
        if (!disabled && isMultiple) {
            HTMLSpan span2 = new HTMLSpan().builder.addClasses('M2MToDuplicate').build() as HTMLSpan
            span2.addChildren new HTMLImg('/assets/taack/icons/actions/delete.svg')
            span2.addChildren(new HTMLInput(InputType.STRING, '', qualifiedName, null, nullable, disabled))
            span2.addChildren(new HTMLInput(InputType.HIDDEN, '', qualifiedName, null, nullable, disabled))
            topElement.addChildren(span2)

        }
        topElement
    }

    @Override
    IHTMLElement dateInput(IHTMLElement topElement, String qualifiedName, boolean disable, boolean nullable, Date value) {
        return null
    }

    @Override
    IHTMLElement textareaInput(IHTMLElement topElement, String qualifiedName, boolean disable, boolean nullable, String value) {
        topElement.addChildren(new HTMLInput(InputType.TEXTAREA, qualifiedName, value, null, disable, nullable))
        topElement
    }

    @Override
    IHTMLElement fileInput(IHTMLElement topElement, String qualifiedName, boolean disable, boolean nullable, String value) {
        topElement.addChildren(new HTMLInput(InputType.FILE, qualifiedName, value, null, disable, nullable))
        topElement
    }

    @Override
    IHTMLElement normalInput(IHTMLElement topElement, String qualifiedName, boolean disable, boolean nullable, String value) {
        topElement.addChildren(new HTMLInput(InputType.STRING, qualifiedName, value, null, disable, nullable))
        topElement
    }

    @Override
    IHTMLElement passwdInput(IHTMLElement topElement, String qualifiedName, boolean disable, boolean nullable, String value) {
        topElement.addChildren(new HTMLInput(InputType.PASSWD, qualifiedName, value, null, disable, nullable))
        topElement
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
        topElement.addChildren(new HTMLDiv().builder.setTaackTag(TaackTag.COL).build())
        topElement.children.last()
    }

    @Override
    IHTMLElement formAction(IHTMLElement topElement, String url, String i18n) {
        topElement.addChildren(
                new HTMLButton(url, i18n)
        )
        topElement
    }

}
