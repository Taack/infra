package taack.ui.dump.html.form

import grails.util.Pair
import groovy.transform.CompileStatic
import jdk.jshell.spi.ExecutionControl
import org.grails.datastore.gorm.GormEntity
import taack.ui.EnumOptions
import taack.ui.IEnumOption
import taack.ui.IEnumOptions
import taack.ui.dsl.common.Style
import taack.ui.dsl.form.editor.EditorOption
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
final class BootstrapTableEdit<T extends GormEntity<T>> implements IFormTheme<T> {

    final ThemeMode themeMode
    final ThemeSize themeSize
    final boolean floating
    final boolean noLabel
    int sectionCounter = 0

    BootstrapTableEdit(final BlockLog blockLog, boolean floating = true, boolean noLabel = false) {
        this.themeMode = blockLog.ts.themeMode
        this.themeSize = blockLog.ts.themeSize
        this.floating = floating
        this.noLabel = noLabel
        constructorIFormThemed()
    }

    private static String inputEscape(final String val) {
        val?.replace('"', '&quot;')?.replace('\'', '&#39;')?.replace('\n', '')?.replace('\r', '')
    }

    @Override
    IHTMLElement inputOverride(IHTMLElement topElement, String qualifiedName, String trI18n, String val, String txt, String imgSrc, IHTMLElement previousElement) {
        throw new ExecutionControl.NotImplementedException("inputOverride not implemented for tables")
    }

    @Override
    IHTMLElement section(IHTMLElement topElement, String trI18n, boolean collapse, String... classes) {
        throw new ExecutionControl.NotImplementedException("section not implemented for tables")
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
        topElement
    }

    @Override
    IHTMLElement selects(IHTMLElement topElement, String qualifiedName, String trI18n, IEnumOptions options, boolean multiple, boolean disable, boolean nullable) {
        IHTMLElement el = topElement
        HTMLSelect s = new HTMLSelect(options, multiple, disable, (multiple || !nullable ? null : floating ? '' : "--${trI18n}--") as String).builder.setId(qualifiedName).build() as HTMLSelect
        el.addChildren(s)
        if (disable) {
            options.currents?.each { IEnumOption it ->
                el.addChildren(new HTMLInput(InputType.HIDDEN, it?.key, qualifiedName))
            }
        }
        topElement
    }

    @Override
    IHTMLElement ajaxField(IHTMLElement topElement, String trI18n, IEnumOptions choices, String qualifiedName, Long modalId, String url, List<String> fieldInfoParams, boolean disable) {
        IHTMLElement el = topElement
        IEnumOption current = choices?.currents?.size() > 0 ? choices.currents.first() : null
        HTMLInput inputHidden = new HTMLInput(InputType.HIDDEN, current?.key, qualifiedName).builder.setId(qualifiedName + 'Id').build() as HTMLInput
        HTMLInput input = new HTMLInput(InputType.STRING, choices?.options?.find { it.key == current?.key }?.value ?: current?.value, null, null, disable, true).builder.setId(qualifiedName + 'String').putAttribute('taackFieldInfoParams', fieldInfoParams.join(',')).putAttribute('taackajaxformm2oaction', url).build() as HTMLInput
        if (!disable) el.addChildren new HTMLImg('/assets/taack/icons/actions/delete.svg').builder.putAttribute('width', '16px').addClasses('deleteIconM2M').setStyle(new ZIndex100()).setOnclick(new DeleteSiblingInputContent()).build()
        el.addChildren(input)
        el.addChildren(inputHidden)
        topElement
    }

    @Override
    IHTMLElement ajaxField(IHTMLElement topElement, String trI18n, List<Object> vals, String qualifiedName, Long modalId, String url, List<String> fieldInfoParams, boolean disabled, boolean nullable) {
        throw new ExecutionControl.NotImplementedException("ajaxField not implemented for tables")
    }

    @Override
    IHTMLElement ajaxField(IHTMLElement topElement, String trI18n, Object val, String qualifiedName, Long modalId, String url, List<String> fieldInfoParams, boolean disabled, boolean nullable) {
        IHTMLElement el = topElement

        HTMLInput inputHidden = new HTMLInput(InputType.HIDDEN, val instanceof GormEntity ? val.ident() : val instanceof Enum ? val.name() : val?.toString(), qualifiedName).builder.setId(qualifiedName + 'Id').build() as HTMLInput
        HTMLInput input = new HTMLInput(InputType.STRING, val instanceof Enum ? EnumOptions.translateEnumValue(val.toString()) : val?.toString(), null, null, disabled, true).builder.setId(qualifiedName + 'String').putAttribute('taackFieldInfoParams', fieldInfoParams.join(',')).putAttribute('taackajaxformm2oaction', url).build() as HTMLInput
        if (floating || noLabel) input.putAttr('placeholder', inputEscape(trI18n))
        if (!disabled) el.addChildren new HTMLImg('/assets/taack/icons/actions/delete.svg').builder.putAttribute('width', '16px').addClasses('deleteIconM2M').setStyle(new ZIndex100()).setOnclick(new DeleteSiblingInputContent()).build()
        el.addChildren(input)
        el.addChildren(inputHidden)
        topElement
    }

    @Override
    IHTMLElement dateInput(IHTMLElement topElement, String qualifiedName, String trI18n, boolean disable, boolean nullable, Date value, boolean isInTime) {
        IHTMLElement el = topElement
        HTMLInput input = new HTMLInput(isInTime ? InputType.DATETIME : InputType.DATE, value ? new SimpleDateFormat('yyyy-MM-dd' + (isInTime ? ' HH:mm' : '')).format(value) : null, qualifiedName, inputEscape(trI18n), disable).builder.setId(qualifiedName).build() as HTMLInput
        el.addChildren(input)
        topElement
    }

    @Override
    IHTMLElement datePairInputs(IHTMLElement topElement, String qualifiedName, String trI18n, boolean disable, boolean nullable, Pair<Date, Date> value, boolean isInTime) {
        throw new ExecutionControl.NotImplementedException("datePairInputs not implemented for tables")
    }

    @Override
    IHTMLElement textareaInput(IHTMLElement topElement, String qualifiedName, String trI18n, boolean disable, boolean nullable, String value, EditorOption editorOption = null) {
        throw new ExecutionControl.NotImplementedException("textareaInput not implemented for tables")
    }

    @Override
    IHTMLElement markdownInput(IHTMLElement topElement, String qualifiedName, String trI18n, boolean disable, boolean nullable, String value) {
        throw new ExecutionControl.NotImplementedException("markdownInput not implemented for tables")
    }

    @Override
    IHTMLElement asciidocInput(IHTMLElement topElement, String qualifiedName, String trI18n, boolean disable, boolean nullable, String value) {
        throw new ExecutionControl.NotImplementedException("asciidocInput not implemented for tables")
    }

    @Override
    IHTMLElement fileInput(IHTMLElement topElement, String qualifiedName, String trI18n, boolean disable, boolean nullable, String value) {
        IHTMLElement el = topElement
        HTMLInput input = new HTMLInput(InputType.FILE, value, qualifiedName, null, disable).builder.setId(qualifiedName).build() as HTMLInput
        if (floating || noLabel) input.putAttr('placeholder', inputEscape(trI18n))
        el.addChildren(input)
        topElement
    }

    @Override
    IHTMLElement normalInput(IHTMLElement topElement, String qualifiedName, String trI18n, boolean disable, boolean nullable, String value) {
        IHTMLElement el = topElement
        HTMLInput input = new HTMLInput(InputType.STRING, value, qualifiedName, null, disable).builder.setId(qualifiedName).build() as HTMLInput
        if (floating || noLabel) input.putAttr('placeholder', inputEscape(trI18n))
        el.addChildren(input)
        topElement
    }

    @Override
    IHTMLElement passwdInput(IHTMLElement topElement, String qualifiedName, String trI18n, boolean disable, boolean nullable, String value) {
        throw new ExecutionControl.NotImplementedException("passwdInput not implemented for tables")
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
