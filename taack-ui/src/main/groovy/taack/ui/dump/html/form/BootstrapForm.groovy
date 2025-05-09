package taack.ui.dump.html.form

import grails.util.Pair
import groovy.transform.CompileStatic
import org.grails.datastore.gorm.GormEntity
import taack.ui.EnumOptions
import taack.ui.IEnumOption
import taack.ui.IEnumOptions
import taack.ui.dsl.common.Style
import taack.ui.dump.common.BlockLog
import taack.ui.dump.html.element.*
import taack.ui.dump.html.layout.BootstrapLayout
import taack.ui.dump.html.script.CheckboxDisableIsZero
import taack.ui.dump.html.script.DeleteM2MParentElement
import taack.ui.dump.html.script.DeleteSiblingInputContent
import taack.ui.dump.html.style.ZIndex100
import taack.ui.dump.html.theme.ThemeMode
import taack.ui.dump.html.theme.ThemeSize

import java.text.SimpleDateFormat

import static taack.render.TaackUiService.tr

@CompileStatic
final class BootstrapForm<T extends GormEntity<T>> extends BootstrapLayout implements IFormTheme<T> {

    final ThemeMode themeMode
    final ThemeSize themeSize
    final boolean floating
    final boolean noLabel
    int sectionCounter = 0

    BootstrapForm(final BlockLog blockLog, boolean floating = true, boolean noLabel = false) {
        super(blockLog)
        this.themeMode = blockLog.ts.themeMode
        this.themeSize = blockLog.ts.themeSize
        this.floating = floating
        this.noLabel = noLabel
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
                'form-control'
                break
        }
    }

    private String getFormSelect() {
        switch (themeSize) {
            case ThemeSize.SM:
                'form-select form-select-sm'
                break
            case ThemeSize.LG:
                'form-select form-select-lg'
                break
            case ThemeSize.NORMAL:
                'form-select'
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
    IHTMLElement section(IHTMLElement topElement, String trI18n, boolean collapse, String... classes) {
        sectionCounter++
        String[] allClasses = classes
        HTMLFieldset fs = new HTMLFieldset().builder.addChildren(
                new HTMLLegend().builder.addChildren(new HTMLTxtContent(trI18n)).build()
        ).build() as HTMLFieldset
        if (collapse) {
            fs = new HTMLFieldset()
            HTMLAnchor unCollapse = new HTMLAnchor().builder.addChildren(new HTMLTxtContent(trI18n)).addClasses('btn', "btn-collapse${collapse ? ' collapsed': ''}").putAttribute('data-bs-toggle', 'collapse').putAttribute('data-bs-target', '#section' + sectionCounter).build() as HTMLAnchor
            topElement.builder.addChildren(unCollapse).build()
            allClasses = classes + 'collapse'
        }
        HTMLDiv sectionDiv = new HTMLDiv().builder.setTaackTag(TaackTag.SECTION).setId('section' + sectionCounter).addClasses(allClasses)
                .addChildren(fs).build() as HTMLDiv
        topElement.builder.addChildren(sectionDiv).build()
        fs
    }

    @Override
    IHTMLElement booleanInput(IHTMLElement topElement, String qualifiedName, String trI18n, boolean disable, boolean nullable, Boolean value) {
        //IHTMLElement el = themeStartInputs(topElement)
        IHTMLElement el = topElement

        if (!nullable)
            el.addChildren(
                    new HTMLInput(InputType.HIDDEN, '0', value ? null : qualifiedName),
                    HTMLInput.inputCheck('1', qualifiedName, value).builder.addClasses('form-check-input').setId("${qualifiedName}Check").setOnclick(new CheckboxDisableIsZero()).build(),
            )
        else
            el.addChildren(
                    new HTMLDiv().builder.addClasses('form-check', 'form-check-inline').addChildren(
                            HTMLInput.inputRadio('1', qualifiedName, value).builder.addClasses('form-check-input').setId("${qualifiedName}Check").build(),
                            new HTMLLabel(qualifiedName, tr('default.boolean.true', null)).builder.addClasses('form-check-label').build(),

                    ).build(),
                    new HTMLDiv().builder.addClasses('form-check', 'form-check-inline').addChildren(
                            HTMLInput.inputRadio('0', qualifiedName, value != null && !value).builder.addClasses('form-check-input').setId("${qualifiedName}Check").build(),
                            new HTMLLabel(qualifiedName, tr('default.boolean.false', null)).builder.addClasses('form-check-label').build(),

                    ).build(),
                    new HTMLDiv().builder.addClasses('form-check', 'form-check-inline').addChildren(
                            HTMLInput.inputRadio(null, qualifiedName, value == null).builder.addClasses('form-check-input').setId("${qualifiedName}Check").build(),
                            new HTMLLabel(qualifiedName, '?').builder.addClasses('form-check-label').build()
                    ).build(),
            )

        el.addChildren(formLabelInput(qualifiedName + 'Check', trI18n))
        el.addChildren(divError(qualifiedName))
        topElement
    }

    @Override
    IHTMLElement selects(IHTMLElement topElement, String qualifiedName, String trI18n, IEnumOptions options, boolean multiple, boolean disable, boolean nullable) {
        IHTMLElement el = themeStartInputs(topElement)
        HTMLSelect s = new HTMLSelect(options, multiple, disable, (multiple || !nullable ? null : floating ? "" : "--${trI18n}--") as String).builder.setId(qualifiedName).addClasses(formSelect).build() as HTMLSelect
        el.addChildren(s)
        if (!noLabel) el.addChildren(formLabelInput(qualifiedName, trI18n))
        el.addChildren(divError(qualifiedName))
        topElement
    }

    @Override
    IHTMLElement ajaxField(IHTMLElement topElement, String trI18n, IEnumOptions choices, String qualifiedName, Long modalId, String url, List<String> fieldInfoParams, boolean disable) {
        IHTMLElement el = themeStartInputs(topElement)
        IEnumOption current = choices?.currents?.size() > 0 ? choices.currents.first() : null
        HTMLInput inputHidden = new HTMLInput(InputType.HIDDEN, current?.key, qualifiedName).builder.setId(qualifiedName + 'Id').addClasses(formControl).build() as HTMLInput
        HTMLInput input = new HTMLInput(InputType.STRING, choices?.options?.find { it.key == current?.key }?.value ?: current?.value, null, null, disable, true).builder.setId(qualifiedName + 'String').putAttribute('taackFieldInfoParams', fieldInfoParams.join(',')).addClasses(formControl).putAttribute('taackajaxformm2oaction', url).build() as HTMLInput
        if (floating || noLabel) input.attributes.put('placeholder', inputEscape(trI18n))
        if (!disable) el.addChildren new HTMLImg('/assets/taack/icons/actions/delete.svg').builder.putAttribute('width', '16px').addClasses('deleteIconM2M').setStyle(new ZIndex100()).setOnclick(new DeleteSiblingInputContent()).build()
        el.addChildren(input)
        if (!noLabel) el.addChildren(formLabelInput(qualifiedName + 'String', trI18n))
        el.addChildren(inputHidden)
        el.addChildren(divError(qualifiedName))
        topElement

//        HTMLSelect s = new HTMLSelect(choices, false, false, disable)
//        HTMLDiv d = new HTMLDiv()
//        if (!disable) {
//            d.addChildren(new HTMLImg('/assets/taack/icons/actions/delete.svg').builder.setId(qualifiedName).addClasses('deleteIconM2M').putAttribute('taackFieldInfoParams', fieldInfoParams.join(',')).build())
//        }
//        d.addChildren(s)
//        el.addChildren(s)
//        el.addChildren(formLabelInput(qualifiedName, trI18n))
//        el.addChildren(divError(qualifiedName))
//        topElement
    }

    @Override
    IHTMLElement ajaxField(IHTMLElement topElement, String trI18n, List<Object> vals, String qualifiedName, Long modalId, String url, List<String> fieldInfoParams, boolean disabled, boolean nullable) {
        IHTMLElement el = new HTMLDiv() as IHTMLElement
        topElement.addChildren(el)

        String idPrefix = "input$modalId-$qualifiedName"
        int occ = 0
        vals?.each {
            String hiddenInputId = idPrefix + "-${occ++}"
            String inputLabelId = hiddenInputId + "-label"
            HTMLDiv m2mParent = themeStartInputs(el).builder.addClasses('M2MParent').build() as HTMLDiv
            if (!disabled) m2mParent.addChildren new HTMLImg('/assets/taack/icons/actions/delete.svg').builder.putAttribute('width', '16px').addClasses('deleteIconM2M').setStyle(new ZIndex100()).setOnclick(new DeleteM2MParentElement()).build()
            HTMLInput input = new HTMLInput(InputType.STRING, it instanceof Enum ? EnumOptions.translateEnumValue(it.toString()) : it?.toString(), null, null, disabled, true).builder.putAttribute('taackFieldInfoParams', fieldInfoParams.join(',')).putAttribute('taackajaxformm2minputid', hiddenInputId).setId(inputLabelId).addClasses(formControl).putAttribute('taackajaxformm2maction', url).build() as HTMLInput
            if (floating || noLabel) input.attributes.put('placeholder', inputEscape(trI18n))
            m2mParent.addChildren(input)
            if (!noLabel) m2mParent.addChildren(formLabelInput(inputLabelId, trI18n))
            HTMLInput inputHidden = new HTMLInput(InputType.HIDDEN, it instanceof GormEntity ? it.ident() : it instanceof Enum ? it.name() : it?.toString(), qualifiedName).builder.putAttribute('attr-name', qualifiedName).setId(hiddenInputId).addClasses(formControl).build() as HTMLInput
            m2mParent.addChildren(inputHidden)
        }
        String hiddenInputId = idPrefix + "-${occ++}"
        String inputLabelId = hiddenInputId + "-label"
        HTMLDiv m2mToDuplicate = themeStartInputs(el).builder.addClasses('M2MToDuplicate').build() as HTMLDiv
        if (!disabled) m2mToDuplicate.addChildren new HTMLImg('/assets/taack/icons/actions/delete.svg').builder.putAttribute('width', '16px').addClasses('deleteIconM2M').setStyle(new ZIndex100()).setOnclick(new DeleteM2MParentElement()).build()
        HTMLInput input = new HTMLInput(InputType.STRING, '', null, null, disabled, true).builder.putAttribute('taackFieldInfoParams', fieldInfoParams.join(',')).putAttribute('taackajaxformm2minputid', hiddenInputId).setId(inputLabelId).addClasses(formControl).putAttribute('taackajaxformm2maction', url).build() as HTMLInput
        if (floating || noLabel) input.attributes.put('placeholder', inputEscape(trI18n))
        m2mToDuplicate.addChildren(input)
        if (!noLabel) m2mToDuplicate.addChildren(formLabelInput(inputLabelId, trI18n))
        HTMLInput inputHidden = new HTMLInput(InputType.HIDDEN, '', null).builder.putAttribute('attr-name', qualifiedName).setId(hiddenInputId).addClasses(formControl).build() as HTMLInput
        m2mToDuplicate.addChildren(inputHidden)
        el.addChildren(divError(qualifiedName))

        topElement
    }

    @Override
    IHTMLElement ajaxField(IHTMLElement topElement, String trI18n, Object val, String qualifiedName, Long modalId, String url, List<String> fieldInfoParams, boolean disabled, boolean nullable) {
        IHTMLElement el = themeStartInputs(topElement)

        HTMLInput inputHidden = new HTMLInput(InputType.HIDDEN, val instanceof GormEntity ? val.ident() : val instanceof Enum ? val.name() : val?.toString(), qualifiedName).builder.setId(qualifiedName + 'Id').addClasses(formControl).build() as HTMLInput
        HTMLInput input = new HTMLInput(InputType.STRING, val instanceof Enum ? EnumOptions.translateEnumValue(val.toString()) : val?.toString(), null, null, disabled, true).builder.setId(qualifiedName + 'String').putAttribute('taackFieldInfoParams', fieldInfoParams.join(',')).addClasses(formControl).putAttribute('taackajaxformm2oaction', url).build() as HTMLInput
        if (floating || noLabel) input.attributes.put('placeholder', inputEscape(trI18n))
        if (!disabled) el.addChildren new HTMLImg('/assets/taack/icons/actions/delete.svg').builder.putAttribute('width', '16px').addClasses('deleteIconM2M').setStyle(new ZIndex100()).setOnclick(new DeleteSiblingInputContent()).build()
        el.addChildren(input)
        if (!noLabel) el.addChildren(formLabelInput(qualifiedName + 'String', trI18n))
        el.addChildren(inputHidden)
        el.addChildren(divError(qualifiedName))
        topElement
    }

    @Override
    IHTMLElement dateInput(IHTMLElement topElement, String qualifiedName, String trI18n, boolean disable, boolean nullable, Date value, boolean isInTime) {
        IHTMLElement el = themeStartInputs(topElement)
        HTMLInput input = new HTMLInput(isInTime ? InputType.DATETIME : InputType.DATE, value ? new SimpleDateFormat('yyyy-MM-dd' + (isInTime ? ' HH:mm' : '')).format(value) : null, qualifiedName, inputEscape(trI18n), disable).builder.setId(qualifiedName).addClasses(formControl).build() as HTMLInput
        el.addChildren(input)
        if (!noLabel) el.addChildren(formLabelInput(qualifiedName, trI18n))
        el.addChildren(divError(qualifiedName))
        topElement
    }

    @Override
    IHTMLElement datePairInputs(IHTMLElement topElement, String qualifiedName, String trI18n, boolean disable, boolean nullable, Pair<Date, Date> value, boolean isInTime) {
        IHTMLElement el = themeStartInputs(topElement)
        HTMLDiv div = new HTMLDiv().builder.setStyle(new Style(null, "display: flex;")).build() as HTMLDiv

        Date value1 = value.aValue
        if (floating || !noLabel) {
            HTMLInput dateInput1 = new HTMLInput(isInTime ? InputType.DATETIME : InputType.DATE, value1 ? new SimpleDateFormat('yyyy-MM-dd' + (isInTime ? ' HH:mm' : '')).format(value1) : null, qualifiedName, inputEscape(trI18n) + ' (Min)', disable).builder.setId(qualifiedName + '-min').addClasses(formControl).build() as HTMLInput
            div.addChildren(dateInput1, formLabelInput(qualifiedName + '-min', trI18n))
        } else {
            HTMLInput dateInput1 = new HTMLInput(InputType.STRING, value1 ? new SimpleDateFormat('yyyy-MM-dd' + (isInTime ? ' HH:mm' : '')).format(value1) : null, qualifiedName, inputEscape(trI18n) + ' (Min)', disable).builder.setId(qualifiedName + '-min').addClasses(formControl).build() as HTMLInput
            dateInput1.attributes.put('onfocus', "this.type='${(isInTime ? InputType.DATETIME : InputType.DATE).typeText}'" as String)
            dateInput1.attributes.put('onblur', "this.type='${InputType.STRING.typeText}'" as String)
            div.addChildren(dateInput1)
        }

        HTMLSpan dash = new HTMLSpan().builder.addChildren(new HTMLTxtContent('-')).setStyle(new Style(null, "padding: .375rem;")).build() as HTMLSpan
        div.addChildren(dash)

        Date value2 = value.bValue
        if (floating || !noLabel) {
            HTMLInput dateInput1 = new HTMLInput(isInTime ? InputType.DATETIME : InputType.DATE, value2 ? new SimpleDateFormat('yyyy-MM-dd' + (isInTime ? ' HH:mm' : '')).format(value2) : null, qualifiedName, inputEscape(trI18n) + ' (Max)', disable).builder.setId(qualifiedName + '-max').addClasses(formControl).build() as HTMLInput
            div.addChildren(dateInput1, formLabelInput(qualifiedName + '-max', trI18n))
        } else {
            HTMLInput dateInput1 = new HTMLInput(InputType.STRING, value2 ? new SimpleDateFormat('yyyy-MM-dd' + (isInTime ? ' HH:mm' : '')).format(value2) : null, qualifiedName, inputEscape(trI18n) + ' (Max)', disable).builder.setId(qualifiedName + '-max').addClasses(formControl).build() as HTMLInput
            dateInput1.attributes.put('onfocus', "this.type='${(isInTime ? InputType.DATETIME : InputType.DATE).typeText}'" as String)
            dateInput1.attributes.put('onblur', "this.type='${InputType.STRING.typeText}'" as String)
            div.addChildren(dateInput1)
        }

        el.addChildren(div)
        el.addChildren(divError(qualifiedName))
        topElement
    }

    @Override
    IHTMLElement textareaInput(IHTMLElement topElement, String qualifiedName, String trI18n, boolean disable, boolean nullable, String value) {
        IHTMLElement el = themeStartInputs(topElement)
        HTMLTextarea input = new HTMLTextarea(value, qualifiedName, null, disable).builder.addClasses(formControl).setId(qualifiedName).build() as HTMLTextarea
        if (floating || noLabel) input.attributes.put('placeholder', inputEscape(trI18n))
        el.addChildren(input)
        if (!noLabel) el.addChildren(formLabelInput(qualifiedName, trI18n))
        el.addChildren(divError(qualifiedName))
        topElement
    }

    @Override
    IHTMLElement markdownInput(IHTMLElement topElement, String qualifiedName, String trI18n, boolean disable, boolean nullable, String value) {
        IHTMLElement el = themeStartInputs(topElement)

        HTMLDiv container = new HTMLDiv().builder.setId("${qualifiedName}-editor").build() as HTMLDiv
        HTMLTextarea textareaInput = new HTMLTextarea(value, qualifiedName, null, disable).builder.addClasses("wysiwyg-content", "markdown").setId(qualifiedName).build() as HTMLTextarea
        container.addChildren(textareaInput)
        HTMLDiv preview = new HTMLDiv().builder.setId("${qualifiedName}-markdown-preview").addClasses("wysiwyg-markdown-preview markdown-body").build() as HTMLDiv
        container.addChildren(preview)
        HTMLInput attachmentSelectInput = new HTMLInput(InputType.STRING, null, null, null, false, true).builder.setId("${qualifiedName}-attachment-select").addClasses(formControl).putAttribute('taackajaxformm2oaction', '/markdown/selectAttachment').build() as HTMLInput
        container.addChildren(attachmentSelectInput)
        HTMLInput attachmentLinkInput = new HTMLInput(InputType.HIDDEN, null, null).builder.setId("${qualifiedName}-attachment-link").build() as HTMLInput
        container.addChildren(attachmentLinkInput)

        el.addChildren(container)
        el.addChildren(divError(qualifiedName))
        topElement
    }

    @Override
    IHTMLElement asciidocInput(IHTMLElement topElement, String qualifiedName, String trI18n, boolean disable, boolean nullable, String value) {
        IHTMLElement el = themeStartInputs(topElement)
        HTMLDiv container = new HTMLDiv().builder.setId("${qualifiedName}-editor").build() as HTMLDiv
        if (!noLabel) container.addChildren(formLabelInput(qualifiedName, trI18n))
        HTMLTextarea textareaInput = new HTMLTextarea(value, qualifiedName, null, disable).builder.addClasses("asciidoctor").setId(qualifiedName).build() as HTMLTextarea
        container.addChildren(textareaInput)
        el.addChildren(container)
        el.addChildren(divError(qualifiedName))
        topElement
    }

    @Override
    IHTMLElement fileInput(IHTMLElement topElement, String qualifiedName, String trI18n, boolean disable, boolean nullable, String value) {
        IHTMLElement el = themeStartInputs(topElement)
        HTMLInput input = new HTMLInput(InputType.FILE, value, qualifiedName, null, disable).builder.setId(qualifiedName).addClasses(formControl).build() as HTMLInput
        if (floating || noLabel) input.attributes.put('placeholder', inputEscape(trI18n))
        el.addChildren(input)
        if (!noLabel) el.addChildren(formLabelInput(qualifiedName, trI18n))
        el.addChildren(divError(qualifiedName))
        topElement
    }

    @Override
    IHTMLElement normalInput(IHTMLElement topElement, String qualifiedName, String trI18n, boolean disable, boolean nullable, String value) {
        IHTMLElement el = themeStartInputs(topElement)
        HTMLInput input = new HTMLInput(InputType.STRING, value, qualifiedName, null, disable).builder.setId(qualifiedName).addClasses(formControl).build() as HTMLInput
        if (floating || noLabel) input.attributes.put('placeholder', inputEscape(trI18n))
        el.addChildren(input)
        if (!noLabel) el.addChildren(formLabelInput(qualifiedName, trI18n))
        el.addChildren(divError(qualifiedName))
        topElement
    }

    @Override
    IHTMLElement passwdInput(IHTMLElement topElement, String qualifiedName, String trI18n, boolean disable, boolean nullable, String value) {
        IHTMLElement el = themeStartInputs(topElement)
        HTMLInput input = new HTMLInput(InputType.PASSWD, value, qualifiedName, null, disable).builder.setId(qualifiedName).addClasses(formControl).build() as HTMLInput
        if (floating || noLabel) input.attributes.put('placeholder', inputEscape(trI18n))
        el.addChildren(input)
        if (!noLabel) el.addChildren(formLabelInput(qualifiedName, trI18n))
        el.addChildren(divError(qualifiedName))
        topElement
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
