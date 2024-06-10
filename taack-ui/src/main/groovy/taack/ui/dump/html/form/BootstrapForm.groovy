package taack.ui.dump.html.form

import groovy.transform.CompileStatic
import org.grails.datastore.gorm.GormEntity
import taack.ui.IEnumOptions
import taack.ui.dsl.form.FormSpec
import taack.ui.dump.html.base.ButtonStyle
import taack.ui.dump.html.base.HTMLButton
import taack.ui.dump.html.base.HTMLDiv
import taack.ui.dump.html.base.HTMLFieldset
import taack.ui.dump.html.base.HTMLImg
import taack.ui.dump.html.base.HTMLInput
import taack.ui.dump.html.base.HTMLLabel
import taack.ui.dump.html.base.HTMLLegend
import taack.ui.dump.html.base.HTMLLi
import taack.ui.dump.html.base.HTMLNav
import taack.ui.dump.html.base.HTMLSection
import taack.ui.dump.html.base.HTMLSelect
import taack.ui.dump.html.base.HTMLSpan
import taack.ui.dump.html.base.HTMLTextarea
import taack.ui.dump.html.base.HTMLTxtContent
import taack.ui.dump.html.base.HTMLUl
import taack.ui.dump.html.base.IHTMLElement
import taack.ui.dump.html.base.InputType
import taack.ui.dump.html.base.TaackTag
import taack.ui.dump.html.script.DeleteSiblingInputContent
import taack.ui.dump.html.style.ZIndex100
import taack.ui.dump.html.theme.ThemeMode
import taack.ui.dump.html.theme.ThemeSize

@CompileStatic
final class BootstrapForm<T extends GormEntity<T>> implements IFormTheme<T> {

    final ThemeMode themeMode
    final ThemeSize themeSize
    final boolean floating
    final boolean noLabel

    BootstrapForm(ThemeMode themeMode, ThemeSize themeSize, boolean floating = true, boolean noLabel = false) {
        this.themeMode = themeMode
        this.themeSize = themeSize
        this.floating = floating
        this.noLabel = noLabel
        if (floating) addClasses 'form-floating', 'mb-1'
        constructorIFormThemed()
    }

    private static String inputEscape(final String val) {
        val?.replace('"', '&quot;')?.replace('\'', '&#39;')?.replace('\n', '')?.replace('\r', '')
    }

    private static IHTMLElement formLabelInput(String qualifiedName, String i18n) {
        new HTMLLabel(qualifiedName).builder.addChildren(new HTMLTxtContent(i18n)).addClasses('form-label').build()
    }

    private IHTMLElement themeStartInputs(IHTMLElement topElement) {
        IHTMLElement ret = topElement
        if (floating) {
            ret = new HTMLDiv().builder.addClasses('form-floating', 'mb-1').build() as IHTMLElement
            topElement.builder.addChildren(ret)
        }
        ret
    }

    private static IHTMLElement divError(String qualifiedName) {
        new HTMLDiv().builder.putAttribute('taackfielderror', qualifiedName).addClasses('form-text').build()
    }

    private String getFormControl() {
        switch (themeSize) {
            case ThemeSize.SM:
                'form-control form-control-sm'
                break
            case ThemeSize.LG:
                'form-control form-control-lg'
                break
            case ThemeSize.NORMAL:
                'form-control form-control'
                break
        }
    }

    @Override
    IHTMLElement inputOverride(IHTMLElement topElement, String qualifiedName, String trI18n, String val, String txt, String imgSrc, IHTMLElement previousElement) {
        HTMLElementBuilder span = new HTMLSpan().builder.addClasses('M2MParent').addChildren(
                new HTMLInput(InputType.HIDDEN, val, qualifiedName).builder.addClasses(formControl).build(),
                new HTMLSpan().builder.addChildren(
                        new HTMLTxtContent(txt)
                ).build(),
                new HTMLImg('/assets/taack/icons/actions/delete.svg').builder.addClasses('deleteIconM2M', 'taackFormFieldOverrideM2O')./*putAttribute('taackOnclickInnerHTML', previousElement).*/ build()
        )
        if (imgSrc) {
            span.addChildren(
                    new HTMLImg(imgSrc).builder.putAttribute('style', 'max-height: 112px; max-width: 112px;').build()
            )
        }
        topElement.builder.addChildren(span.build())
        topElement.builder.addChildren(formLabelInput(qualifiedName, trI18n))
        topElement.builder.addChildren(divError(qualifiedName))
        topElement
    }

    @Override
    IHTMLElement section(IHTMLElement topElement, String trI18n, String... classes) {
        topElement.builder.addChildren(
                new HTMLDiv().builder.setTaackTag(TaackTag.SECTION).addClasses(classes)
                        .addChildren(
                                new HTMLFieldset().builder.addChildren(
                                        new HTMLLegend().builder.addChildren(new HTMLTxtContent(trI18n)).build()
                                ).build()
                        ).build()
        )
        topElement.children.last().children.last()
    }

    @Override
    IHTMLElement booleanInput(IHTMLElement topElement, String qualifiedName, String trI18n, boolean disable, boolean nullable, Boolean value) {
        //IHTMLElement el = themeStartInputs(topElement)
        IHTMLElement el = topElement

        if (!nullable)
            el.addChildren(
                    HTMLInput.inputCheck(value ? '1' : '0', qualifiedName, value).builder.addClasses('form-check-input').setId("${qualifiedName}Check").build(),
            )
        else
            el.addChildren(
                    new HTMLDiv().builder.addClasses('form-check', 'form-check-inline').addChildren(
                            HTMLInput.inputRadio('1', qualifiedName, value).builder.addClasses('form-check-input').setId("${qualifiedName}Check").build(),
                            new HTMLLabel(qualifiedName, '1').builder.addClasses('form-check-label').build(),

                    ).build(),
                    new HTMLDiv().builder.addClasses('form-check', 'form-check-inline').addChildren(
                            HTMLInput.inputRadio('0', qualifiedName, value != null && !value).builder.addClasses('form-check-input').setId("${qualifiedName}Check").build(),
                            new HTMLLabel(qualifiedName, '0').builder.addClasses('form-check-label').build(),

                    ).build(),
                    new HTMLDiv().builder.addClasses('form-check', 'form-check-inline').addChildren(
                            HTMLInput.inputRadio('?', qualifiedName, value == null).builder.addClasses('form-check-input').setId("${qualifiedName}Check").build(),
                            new HTMLLabel(qualifiedName, '?').builder.addClasses('form-check-label').build()
                    ).build(),
            )

        el.addChildren(formLabelInput(qualifiedName, trI18n))
        el.addChildren(divError(qualifiedName))
        topElement
    }

    @Override
    IHTMLElement selects(IHTMLElement topElement, String qualifiedName, String trI18n, IEnumOptions options, boolean multiple, boolean disable, boolean nullable) {
        IHTMLElement el = themeStartInputs(topElement)
        HTMLSelect s = new HTMLSelect(options, multiple, disable, nullable).builder.addClasses('form-select').build() as HTMLSelect
        el.addChildren(s)
        el.addChildren(formLabelInput(qualifiedName, trI18n))
        el.addChildren(divError(qualifiedName))
        topElement
    }

    @Override
    IHTMLElement ajaxField(IHTMLElement topElement, String trI18n, IEnumOptions choices, Object val, String qualifiedName, Long modalId, String url, String fieldInfoParams, boolean disable) {
        IHTMLElement el = themeStartInputs(topElement)
        HTMLSelect s = new HTMLSelect(choices, false, false, disable)
        HTMLDiv d = new HTMLDiv()
        if (!disable) {
            d.addChildren(new HTMLImg('/assets/taack/icons/actions/delete.svg').builder.addClasses('deleteIconM2M').build())
        }
        d.addChildren(s)
        el.addChildren(s)
        el.addChildren(formLabelInput(qualifiedName, trI18n))
        el.addChildren(divError(qualifiedName))
        topElement
    }

    @Override
    IHTMLElement ajaxField(IHTMLElement topElement, String trI18n, List<Object> vals, String qualifiedName, Long modalId, String url, String fieldInfoParams, boolean disabled, boolean nullable) {
        IHTMLElement el = themeStartInputs(topElement)

        vals?.each {
            boolean isString = String.isAssignableFrom(it.class)

            HTMLSpan span = new HTMLSpan().builder.addClasses('M2MParent').build() as HTMLSpan
            if (disabled) span.addChildren new HTMLImg('/assets/taack/icons/actions/delete.svg')
            span.addChildren(new HTMLInput(InputType.STRING, it ? inputEscape(it.toString()) : '', qualifiedName, null, disabled))
            span.addChildren(new HTMLInput(InputType.HIDDEN, it ? (isString ? it : it['id']) : '', qualifiedName, null, disabled))
            el.addChildren(span)
        }
        if (!disabled) {
            HTMLSpan span2 = new HTMLSpan().builder.addClasses('M2MToDuplicate').build() as HTMLSpan
            span2.addChildren new HTMLImg('/assets/taack/icons/actions/delete.svg')
            span2.addChildren(new HTMLInput(InputType.STRING, '', qualifiedName, null, disabled))
            span2.addChildren(new HTMLInput(InputType.HIDDEN, '', qualifiedName, null, disabled))
            el.addChildren(span2)

        }
        el.addChildren(formLabelInput(qualifiedName, trI18n))
        el.addChildren(divError(qualifiedName))
        topElement
    }

    @Override
    def <T1 extends GormEntity> IHTMLElement ajaxField(IHTMLElement topElement, String trI18n, T1 val, String qualifiedName, Long modalId, String url, String fieldInfoParams, boolean disabled, boolean nullable) {
        IHTMLElement el = themeStartInputs(topElement)

        HTMLInput inputHidden = new HTMLInput(InputType.HIDDEN, val?.ident(), qualifiedName, null, disabled).builder.addClasses(formControl).build() as HTMLInput
        HTMLInput input = new HTMLInput(InputType.STRING, val?.toString(), null, null, disabled, true).builder.addClasses(formControl).putAttribute('taackajaxformm2oaction', url).build() as HTMLInput
        if (floating || noLabel) input.attributes.put('placeholder', inputEscape(trI18n))
        if (!disabled) el.addChildren new HTMLImg('/assets/taack/icons/actions/delete.svg').builder.putAttribute('width', '16px').addClasses('deleteIconM2M').setStyle(new ZIndex100()).setOnclick(new DeleteSiblingInputContent()).build()
        el.addChildren(input)
        if (!noLabel) el.addChildren(formLabelInput(qualifiedName, trI18n))
        el.addChildren(inputHidden)
        el.addChildren(divError(qualifiedName))
        topElement
    }

    @Override
    IHTMLElement dateInput(IHTMLElement topElement, String qualifiedName, String trI18n, boolean disable, boolean nullable, Date value) {
        IHTMLElement el = themeStartInputs(topElement)
        HTMLInput input = new HTMLInput(InputType.DATE, value?.toString(), qualifiedName, null, disable).builder.addClasses(formControl).build() as HTMLInput
        if (floating) input.attributes.put('placeholder', inputEscape(trI18n))
        el.addChildren(input)
        el.addChildren(formLabelInput(qualifiedName, trI18n))
        el.addChildren(divError(qualifiedName))
        topElement
    }

    @Override
    IHTMLElement textareaInput(IHTMLElement topElement, String qualifiedName, String trI18n, boolean disable, boolean nullable, String value) {
        IHTMLElement el = themeStartInputs(topElement)
        HTMLTextarea input = new HTMLTextarea(value, qualifiedName, null, disable).builder.addClasses(formControl).build() as HTMLTextarea
        if (floating || noLabel) input.attributes.put('placeholder', inputEscape(trI18n))
        el.addChildren(input)
        if (!noLabel) el.addChildren(formLabelInput(qualifiedName, trI18n))
        el.addChildren(divError(qualifiedName))
        topElement
    }

    @Override
    IHTMLElement fileInput(IHTMLElement topElement, String qualifiedName, String trI18n, boolean disable, boolean nullable, String value) {
        IHTMLElement el = themeStartInputs(topElement)
        HTMLInput input = new HTMLInput(InputType.FILE, value, qualifiedName, null, disable).builder.addClasses(formControl).build() as HTMLInput
        if (floating || noLabel) input.attributes.put('placeholder', inputEscape(trI18n))
        el.addChildren(input)
        if (!noLabel) el.addChildren(formLabelInput(qualifiedName, trI18n))
        el.addChildren(divError(qualifiedName))
        topElement
    }

    @Override
    IHTMLElement normalInput(IHTMLElement topElement, String qualifiedName, String trI18n, boolean disable, boolean nullable, String value) {
        IHTMLElement el = themeStartInputs(topElement)
        HTMLInput input = new HTMLInput(InputType.STRING, value, qualifiedName, null, disable).builder.addClasses(formControl).build() as HTMLInput
        if (floating || noLabel) input.attributes.put('placeholder', inputEscape(trI18n))
        el.addChildren(input)
        if (!noLabel) el.addChildren(formLabelInput(qualifiedName, trI18n))
        el.addChildren(divError(qualifiedName))
        topElement
    }

    @Override
    IHTMLElement passwdInput(IHTMLElement topElement, String qualifiedName, String trI18n, boolean disable, boolean nullable, String value) {
        IHTMLElement el = themeStartInputs(topElement)
        HTMLInput input = new HTMLInput(InputType.PASSWD, value, qualifiedName, null, disable).builder.addClasses(formControl).build() as HTMLInput
        if (floating || noLabel) input.attributes.put('placeholder', inputEscape(trI18n))
        el.addChildren(input)
        if (!noLabel) el.addChildren(formLabelInput(qualifiedName, trI18n))
        el.addChildren(divError(qualifiedName))
        topElement
    }

    @Override
    IHTMLElement formTabs(IHTMLElement topElement, int tabIds, List<String> names, FormSpec.Width width) {
        HTMLInput[] radioList = new HTMLInput[names.size()]
        HTMLLi[] liList = new HTMLLi[names.size()]
        names.eachWithIndex { it, occ ->
            int tabOcc = occ + 1
            radioList[occ] = HTMLInput.inputRadio(null, "pct-${tabIds}", occ == 0).builder.addClasses("inputTab${tabOcc}").setId("tab$tabOcc-f${tabIds}").build() as HTMLInput
            liList[occ] = new HTMLLi().builder.addClasses("tab${tabOcc}").addChildren(
                    new HTMLLabel("tab${tabOcc}-f${tabIds}").builder.addChildren(
                            new HTMLTxtContent(it)
                    ).build()
            ).build() as HTMLLi
        }

        topElement.builder.addChildren(
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
        topElement.builder.addChildren(
                new HTMLDiv().builder.setTaackTag(TaackTag.TAB).addClasses('tab' + occ).build()
        )
        topElement.children.last()
    }

    @Override
    IHTMLElement formCol(IHTMLElement topElement) {
        topElement.builder.addChildren(new HTMLDiv().builder.setTaackTag(TaackTag.COL).addClasses('flex-fill').build())
        topElement.children.last()
    }

    @Override
    IHTMLElement formRow(IHTMLElement topElement) {
        topElement.builder.addChildren(new HTMLDiv().builder.setTaackTag(TaackTag.ROW).addClasses('d-flex', 'flex-row').build())
        topElement.children.last()
    }

    @Override
    IHTMLElement formActionBlock(IHTMLElement topElement) {
        topElement.builder.addChildren(new HTMLDiv().builder.addClasses('d-flex', 'flex-nowrap', 'justify-content-end').build())
        topElement.children.last()
    }

    @Override
    IHTMLElement addFormAction(IHTMLElement topElement, String url, String i18n, ButtonStyle style) {
        topElement.builder.addChildren(
                new HTMLButton(url, i18n, style)
        )
        topElement
    }

}
