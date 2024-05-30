package taack.ui.dump.html.form

import groovy.transform.CompileStatic
import org.grails.datastore.gorm.GormEntity
import taack.ui.IEnumOptions
import taack.ui.base.form.FormSpec
import taack.ui.dump.html.base.HTMLButton
import taack.ui.dump.html.base.HTMLDiv
import taack.ui.dump.html.base.HTMLFieldset
import taack.ui.dump.html.base.HTMLImg
import taack.ui.dump.html.base.HTMLInput
import taack.ui.dump.html.base.HTMLLabel
import taack.ui.dump.html.base.HTMLLi
import taack.ui.dump.html.base.HTMLNav
import taack.ui.dump.html.base.HTMLSection
import taack.ui.dump.html.base.HTMLSelect
import taack.ui.dump.html.base.HTMLSpan
import taack.ui.dump.html.base.HTMLTxtContent
import taack.ui.dump.html.base.HTMLUl
import taack.ui.dump.html.base.IHTMLElement
import taack.ui.dump.html.base.InputType
import taack.ui.dump.html.base.TaackTag
import taack.ui.dump.html.theme.ThemeMode
import taack.ui.dump.html.theme.ThemeSize

@CompileStatic
final class BootstrapForm<T extends GormEntity<T>> implements IFormTheme<T> {

    final ThemeMode themeMode
    final ThemeSize themeSize
    final boolean floating

    BootstrapForm(ThemeMode themeMode, ThemeSize themeSize, boolean floating = true) {
        this.themeMode = themeMode
        this.themeSize = themeSize
        this.floating = floating
        if (floating) addClasses 'form-floating', 'mb-1'
        constructorIFormThemed()
    }

    @Override
    IHTMLElement inputOverride(IHTMLElement topElement, String qualifiedName, String trI18n, String val, String txt, String imgSrc, IHTMLElement previousElement) {
        topElement.addChildren(formLabelInput(qualifiedName, trI18n))
        HTMLElementBuilder span = new HTMLSpan().builder.addClasses('M2MParent').addChildren(
                new HTMLInput(InputType.HIDDEN, val, qualifiedName).builder.addClasses('form-control').build(),
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
        topElement.addChildren(divError(qualifiedName))
        topElement
    }

    @Override
    IHTMLElement section(IHTMLElement topElement, String trI18n, String... classes) {
        topElement.addChildren(
                new HTMLDiv().builder.setTaackTag(TaackTag.SECTION).addClasses(classes)
                        .addChildren(
                                new HTMLFieldset().builder.addChildren(
                                        new HTMLTxtContent(trI18n)
                                ).build()
                        ).build()
        )
        topElement.children.last().children.last()
    }

    @Override
    IHTMLElement booleanInput(IHTMLElement topElement, String qualifiedName, String trI18n, boolean disable, boolean nullable, boolean value) {
        IHTMLElement el = themeStartInputs(topElement)
        el.addChildren(formLabelInput(qualifiedName, trI18n))
        el.addChildren(
                HTMLInput.inputCheck(value ? '1' : '0', qualifiedName, value).builder.addClasses('form-check-input').setId("${qualifiedName}Check").build(),
        )
        el.addChildren(divError(qualifiedName))
        topElement
    }

    @Override
    IHTMLElement selects(IHTMLElement topElement, String qualifiedName, String trI18n, IEnumOptions options, boolean multiple, boolean disable, boolean nullable) {
        IHTMLElement el = themeStartInputs(topElement)
        el.addChildren(formLabelInput(qualifiedName, trI18n))
        HTMLSelect s = new HTMLSelect(options, multiple, disable, nullable).builder.addClasses('form-select').build() as HTMLSelect
        el.addChildren(s)
        el.addChildren(divError(qualifiedName))
        topElement
    }

    @Override
    IHTMLElement ajaxField(IHTMLElement topElement, String trI18n, IEnumOptions choices, Object val, String qualifiedName, Long modalId, String url, String fieldInfoParams, boolean disable) {
        IHTMLElement el = themeStartInputs(topElement)
        el.addChildren(formLabelInput(qualifiedName, trI18n))
        HTMLSelect s = new HTMLSelect(choices, false, false, disable)
        HTMLDiv d = new HTMLDiv()
        if (!disable) {
            d.addChildren(new HTMLImg('/assets/taack/icons/actions/delete.svg').builder.addClasses('deleteIconM2M').build())
        }
        d.addChildren(s)
        el.addChildren(s)
        el.addChildren(divError(qualifiedName))
        topElement
    }

    private static String inputEscape(final String val) {
        val?.replace('"', '&quot;')?.replace('\'', '&#39;')?.replace('\n', '')?.replace('\r', '')
    }

    @Override
    IHTMLElement ajaxField(IHTMLElement topElement, String trI18n, List<Object> vals, String qualifiedName, Long modalId, String url, String fieldInfoParams, boolean disabled, boolean nullable, boolean isMultiple) {
         IHTMLElement el = themeStartInputs(topElement)
        el.addChildren(formLabelInput(qualifiedName, trI18n))

        vals?.each {
            boolean isString = String.isAssignableFrom(it.class)

            HTMLSpan span = new HTMLSpan().builder.addClasses('M2MParent').build() as HTMLSpan
            if (disabled) span.addChildren new HTMLImg('/assets/taack/icons/actions/delete.svg')
            span.addChildren(new HTMLInput(InputType.STRING, it ? inputEscape(it.toString()) : '', qualifiedName, null, disabled))
            span.addChildren(new HTMLInput(InputType.HIDDEN, it ? (isString ? it : it['id']) : '', qualifiedName, null, disabled))
            topElement.addChildren(span)
        }
        if (!disabled && isMultiple) {
            HTMLSpan span2 = new HTMLSpan().builder.addClasses('M2MToDuplicate').build() as HTMLSpan
            span2.addChildren new HTMLImg('/assets/taack/icons/actions/delete.svg')
            span2.addChildren(new HTMLInput(InputType.STRING, '', qualifiedName, null, disabled))
            span2.addChildren(new HTMLInput(InputType.HIDDEN, '', qualifiedName, null, disabled))
            topElement.addChildren(span2)

        }
        el.addChildren(divError(qualifiedName))
        topElement
    }

    @Override
    IHTMLElement dateInput(IHTMLElement topElement, String qualifiedName, String trI18n, boolean disable, boolean nullable, Date value) {
        IHTMLElement el = themeStartInputs(topElement)
        el.addChildren(formLabelInput(qualifiedName, trI18n))
        el.addChildren(new HTMLInput(InputType.DATE, value?.toString(), qualifiedName, null, disable).builder.addClasses('form-control').build())
        el.addChildren(divError(qualifiedName))
        topElement
    }

    @Override
    IHTMLElement textareaInput(IHTMLElement topElement, String qualifiedName, String trI18n, boolean disable, boolean nullable, String value) {
        IHTMLElement el = themeStartInputs(topElement)
        el.addChildren(formLabelInput(qualifiedName, trI18n))
        el.addChildren(new HTMLInput(InputType.TEXTAREA, value, qualifiedName, null, disable).builder.addClasses('form-control').build())
        el.addChildren(divError(qualifiedName))
        topElement
    }

    @Override
    IHTMLElement fileInput(IHTMLElement topElement, String qualifiedName, String trI18n, boolean disable, boolean nullable, String value) {
        IHTMLElement el = themeStartInputs(topElement)
        el.addChildren(formLabelInput(qualifiedName, trI18n))
        el.addChildren(new HTMLInput(InputType.FILE, value, qualifiedName, null, disable).builder.addClasses('form-control').build())
        el.addChildren(divError(qualifiedName))
        topElement
    }

    @Override
    IHTMLElement normalInput(IHTMLElement topElement, String qualifiedName, String trI18n, boolean disable, boolean nullable, String value) {
        IHTMLElement el = themeStartInputs(topElement)
        el.addChildren(formLabelInput(qualifiedName, trI18n))
        el.addChildren(new HTMLInput(InputType.STRING, value, qualifiedName, null, disable).builder.addClasses('form-control').build())
        el.addChildren(divError(qualifiedName))
        topElement
    }

    @Override
    IHTMLElement passwdInput(IHTMLElement topElement, String qualifiedName, String trI18n, boolean disable, boolean nullable, String value) {
        IHTMLElement el = themeStartInputs(topElement)
        el.addChildren(formLabelInput(qualifiedName, trI18n))
        el.addChildren(new HTMLInput(InputType.PASSWD, value, qualifiedName, null, disable).builder.addClasses('form-control').build())
        el.addChildren(divError(qualifiedName))
        topElement
    }


    private static IHTMLElement formLabelInput(String qualifiedName, String i18n) {
        new HTMLLabel(qualifiedName).builder.addChildren(new HTMLTxtContent(i18n)).addClasses('form-label').build()
    }

    private IHTMLElement themeStartInputs(IHTMLElement topElement) {
        IHTMLElement ret = topElement
        if (floating) {
            ret = new HTMLDiv().builder.addClasses('form-floating', 'mb-1').build() as IHTMLElement
            topElement.addChildren(ret)
        }
        ret
    }

    private static IHTMLElement divError(String qualifiedName) {
        new HTMLDiv().builder.putAttribute('taackfielderror', qualifiedName).addClasses('form-text').build()
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
