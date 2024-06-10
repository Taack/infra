package taack.ui.dump


import grails.util.Triple
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.ast.type.FieldInfo
import taack.ui.EnumOptions
import taack.ui.IEnumOption
import taack.ui.dsl.filter.IUiFilterVisitor
import taack.ui.dsl.filter.expression.FilterExpression
import taack.ui.dump.html.base.*
import taack.ui.dump.html.form.BootstrapForm
import taack.ui.dump.html.form.IFormTheme
import taack.ui.dump.html.theme.ThemeSelector

@CompileStatic
final class RawHtmlFilterDump implements IUiFilterVisitor {

    final private ByteArrayOutputStream out
    final private Parameter parameter
    final private List<Triple<String, ButtonStyle, String>> filterActions = []

    IFormTheme formThemed
    IHTMLElement topElement

    RawHtmlFilterDump(final ByteArrayOutputStream out, final Parameter parameter) {
        this.out = out
        this.parameter = parameter
        filterActions.add new Triple<String, ButtonStyle, String>('Filter', ButtonStyle.SUCCESS, "/${parameter.applicationTagLib.controllerName}/${parameter.applicationTagLib.actionName}" as String)
        filterActions.add new Triple<String, ButtonStyle, String>('Reset', ButtonStyle.SECONDARY, null)
    }

    static String getQualifiedName(final FieldInfo fieldInfo) {
        fieldInfo.fieldName
    }

    static String getQualifiedName(final FieldInfo... fieldInfoList) {
        fieldInfoList*.fieldName.join('.')
    }

    static String getQualifiedName(Class reverseClass, FieldInfo reverseField, FieldInfo... fields) {
        "_reverse_${reverseClass.name}_${reverseField.fieldName}_${getQualifiedName(fields)}"
    }

    @Override
    void visitFilter(Class aClass, Map<String, ? extends Object> additionalParams) {
        parameter.aClassSimpleName = aClass.simpleName
        ThemeSelector ts = parameter.uiThemeService.themeSelector
        formThemed = new BootstrapForm(ts.themeMode, ts.themeSize, false, true).builder.setTaackTag(TaackTag.FILTER).addClasses('filter', 'rounded-3').putAttribute('taackFilterId', parameter.modalId?.toString()).addChildren(
                new HTMLInput(InputType.HIDDEN, parameter.sort, 'sort'),
                new HTMLInput(InputType.HIDDEN, parameter.order, 'order'),
                new HTMLInput(InputType.HIDDEN, parameter.offset, 'offset'),
                new HTMLInput(InputType.HIDDEN, parameter.max, 'max'),
                new HTMLInput(InputType.HIDDEN, parameter.additionalId, 'additionalId'),
                new HTMLInput(InputType.HIDDEN, parameter.brand, 'brand'),
                new HTMLInput(InputType.HIDDEN, aClass.name, 'className'),
                new HTMLInput(InputType.HIDDEN, parameter.fieldName, 'fieldName'),
        ).build() as IFormTheme

        HTMLInput[] addedInputs = additionalParams?.collect {
            new HTMLInput(InputType.HIDDEN, it.key, it.value?.toString())
        } as HTMLInput[]

        if (addedInputs)
            formThemed.addChildren(addedInputs)

        topElement = formThemed
    }

    @Override
    void visitHiddenId(Long id) {
        formThemed.addChildren(
                new HTMLInput(InputType.HIDDEN, id, 'id'),
        )
    }

    @Override
    void visitFilterEnd() {
        topElement = formThemed.formActionBlock(topElement)
//        addFormAction(topElement, "/${parameter.applicationTagLib.controllerName}/${parameter.applicationTagLib.actionName}", 'Filter', ButtonStyle.SUCCESS)
//        formThemed.addFormAction(topElement, null, 'Reset', ButtonStyle.SECONDARY)
        filterActions.each {
            formThemed.addFormAction(topElement, it.cValue, it.aValue, it.bValue)
        }
        topElement = closeTags(TaackTag.FILTER)
        out << formThemed.output
    }

    @Override
    void visitSection(final String i18n) {
        topElement = formThemed.section(topElement, i18n)
    }

    @Override
    void visitSectionEnd() {
        topElement = closeTags(TaackTag.SECTION)
    }

    private filterField(final String i18n, final String qualifiedName, final String value, final FieldInfo fieldInfo = null, final IEnumOption[] enumOptions = null) {
        final boolean isBoolean = fieldInfo?.fieldConstraint?.field?.type == Boolean
        final boolean isEnum = fieldInfo?.fieldConstraint?.field?.type?.isEnum()
        final String qualifiedId = qualifiedName + "-" + parameter.modalId
        if (enumOptions) {
            topElement = formThemed.selects(topElement, qualifiedName, i18n, new EnumOptions(enumOptions, qualifiedName), true, false, true)
        } else if (isEnum) {
            final Class type = fieldInfo.fieldConstraint.field.type
            topElement = formThemed.selects(topElement, qualifiedName, i18n, new EnumOptions(type as Class<Enum>, qualifiedName, value), true, false, true)
        } else if (isBoolean) {
            Boolean isChecked = parameter.applicationTagLib.params[qualifiedName + 'Default'] ?
                    ((parameter.applicationTagLib.params[qualifiedName] && parameter.applicationTagLib.params[qualifiedName] == '1') ? true : (parameter.applicationTagLib.params[qualifiedName] && parameter.applicationTagLib.params[qualifiedName] == '0') ? false : null) : fieldInfo.value
            topElement = formThemed.booleanInput(topElement, qualifiedName, i18n, false, true, isChecked)
        } else {
            topElement = formThemed.normalInput(topElement, qualifiedName, i18n, false, true, value)
        }
    }

    @Override
    void visitFilterField(String i18n, IEnumOption[] enumOptions, FieldInfo[] fields) {
        final String qualifiedName = getQualifiedName(fields)
        filterField(i18n ?: parameter.trField(fields), qualifiedName, parameter.applicationTagLib.params[qualifiedName]?.toString(), fields?.last(), enumOptions)
    }

    @Override
    void visitFilterFieldReverse(String i18n, Class reverseClass, FieldInfo reverseField, FieldInfo... fields) {
        final String qualifiedName = getQualifiedName(reverseClass, reverseField, fields)
        filterField(i18n, qualifiedName, parameter.applicationTagLib.params[qualifiedName]?.toString())
    }

    @Override
    void visitFilterFieldExpressionBool(FilterExpression... filterExpression) {
        visitFilterFieldExpressionBool(null, null, filterExpression)
    }

    @Override
    void visitFilterFieldExpressionBool(String i18n, Boolean defaultValue, FilterExpression[] filterExpressions) {
        String qualifiedName = filterExpressions*.qualifiedName.join('_')
        boolean isChecked = parameter.applicationTagLib.params[qualifiedName + 'Default'] ? parameter.applicationTagLib.params[qualifiedName] == '1' : defaultValue
        topElement = formThemed.booleanInput(topElement, qualifiedName, i18n, false, false, isChecked)
        topElement.addChildren(new HTMLInput(InputType.HIDDEN, '1', "${qualifiedName}Default"))
    }

    @Override
    void visitFilterAction(String i18n, MethodClosure action, ButtonStyle style = ButtonStyle.SUCCESS) {
        filterActions.add new Triple<String, ButtonStyle, String>(i18n, style, "/${action.toString()}/${action.method}" as String)
    }

}
