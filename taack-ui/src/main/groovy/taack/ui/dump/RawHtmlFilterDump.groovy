package taack.ui.dump

import grails.util.Pair
import grails.util.Triple
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.ast.type.FieldInfo
import taack.ast.type.WidgetKind
import taack.domain.TaackFilter
import taack.ui.EnumOptions
import taack.ui.IEnumOption
import taack.ui.dsl.UiFilterSpecifier
import taack.ui.dsl.filter.IUiFilterVisitor
import taack.ui.dsl.filter.expression.FilterExpression
import taack.ui.dump.common.BlockLog
import taack.ui.dump.html.element.*
import taack.ui.dump.html.form.BootstrapForm

@CompileStatic
final class RawHtmlFilterDump implements IUiFilterVisitor {

    final private Parameter parameter
    final private List<Triple<String, ButtonStyle, String>> filterActions = []
    final String blockId
    private final Map<String, HTMLInput> mapAdditionalHiddenParams = [:]

    BootstrapForm formThemed
    final BlockLog blockLog

    RawHtmlFilterDump(final BlockLog blockLog, final String id, final Parameter parameter) {
        this.blockLog = blockLog
        this.parameter = parameter
        filterActions.add new Triple<String, ButtonStyle, String>('Filter', ButtonStyle.SUCCESS, "/${parameter.applicationTagLib.controllerName}/${parameter.applicationTagLib.actionName}" as String)
        this.blockId = id ?: '' + parameter.modalId

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
        formThemed = new BootstrapForm(blockLog, false, true)
        formThemed.attributes.put('action', parameter.urlMapped())
        blockLog.topElement.setTaackTag(TaackTag.FILTER)

        if (parameter.sort) mapAdditionalHiddenParams.put 'sort', new HTMLInput(InputType.HIDDEN, parameter.sort, 'sort')
        if (parameter.order) mapAdditionalHiddenParams.put 'order', new HTMLInput(InputType.HIDDEN, parameter.order, 'order')
        if (parameter.offset) mapAdditionalHiddenParams.put 'offset', new HTMLInput(InputType.HIDDEN, parameter.offset, 'offset')
        if (parameter.max) mapAdditionalHiddenParams.put 'max', new HTMLInput(InputType.HIDDEN, parameter.max, 'max')
        if (parameter.additionalId) mapAdditionalHiddenParams.put 'additionalId', new HTMLInput(InputType.HIDDEN, parameter.additionalId, 'additionalId')
        if (parameter.brand) mapAdditionalHiddenParams.put 'brand', new HTMLInput(InputType.HIDDEN, parameter.brand, 'brand')
        if (aClass.name) mapAdditionalHiddenParams.put 'className', new HTMLInput(InputType.HIDDEN, aClass.name, 'className')
        if (parameter.fieldName) mapAdditionalHiddenParams.put 'fieldName', new HTMLInput(InputType.HIDDEN, parameter.fieldName, 'fieldName')
        if (parameter.tabIndex != null) mapAdditionalHiddenParams.put 'tabIndex', new HTMLInput(InputType.HIDDEN, parameter.tabIndex, 'tabIndex')

        additionalParams?.each {
            mapAdditionalHiddenParams.put it.key, new HTMLInput(InputType.HIDDEN, it.value, it.key)
        }

        blockLog.topElement.addChildren(
                formThemed.builder.addClasses('filter', 'rounded-3').putAttribute('taackFilterId', blockId).build()
        )

        blockLog.topElement = formThemed
    }

    @Override
    void visitHiddenId(Long id) {
        formThemed.builder.addChildren(
                new HTMLInput(InputType.HIDDEN, id, 'id'),
        )
    }

    @Override
    void visitFilterEnd() {
        blockLog.topElement = formThemed.formActionBlock(blockLog.topElement)
        filterActions.each {
            formThemed.addFormAction(blockLog.topElement, it.cValue, it.aValue, it.bValue)
        }
        blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.FILTER)
    }

    @Override
    void visitSection(final String i18n) {
        blockLog.topElement = formThemed.section(blockLog.topElement, i18n)
    }

    @Override
    void visitSectionEnd() {
        blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.SECTION).parent
    }

    private filterField(final String i18n, final String qualifiedName, final String value, final FieldInfo fieldInfo = null, final IEnumOption[] enumOptions = null) {
        final Class type = fieldInfo?.fieldConstraint?.field?.type
        final boolean isEnum = type?.isEnum()
        if (enumOptions) {
            EnumOptions eos = value != null ? new EnumOptions(enumOptions, qualifiedName, value) : isEnum ? new EnumOptions(enumOptions, qualifiedName, fieldInfo?.value as Enum) : new EnumOptions(enumOptions, qualifiedName, fieldInfo?.value?.toString())
            blockLog.topElement = formThemed.selects(blockLog.topElement, qualifiedName, i18n, eos, false, false, true)
        } else if (isEnum) {
            EnumOptions eos = value != null ? new EnumOptions(type as Class<Enum>, qualifiedName, value) : new EnumOptions(type as Class<Enum>, qualifiedName, fieldInfo?.value as Enum)
            blockLog.topElement = formThemed.selects(blockLog.topElement, qualifiedName, i18n, eos, false, false, true)
        } else if (type == boolean || type == Boolean) {
            Boolean isChecked = value != null ? (value == "1" ? true : value == "0" ? false : null) : (fieldInfo?.value as Boolean)
            blockLog.topElement = formThemed.booleanInput(blockLog.topElement, qualifiedName, i18n, false, true, isChecked)
        } else if (type == Date) {
            blockLog.topElement = formThemed.datePairInputs(blockLog.topElement, qualifiedName, i18n, false, true, value != null ? TaackFilter.parseDate(value) : new Pair(fieldInfo?.value, null), fieldInfo?.fieldConstraint?.widget == WidgetKind.DATETIME.name)
        } else {
            blockLog.topElement = formThemed.normalInput(blockLog.topElement, qualifiedName, i18n, false, true, value)
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
        blockLog.topElement = formThemed.booleanInput(blockLog.topElement, qualifiedName, i18n, false, false, isChecked)
        mapAdditionalHiddenParams.put qualifiedName + 'Default', new HTMLInput(InputType.HIDDEN, '1', "${qualifiedName}Default")
    }

    @Override
    void visitFilterAction(String i18n, MethodClosure action, ButtonStyle style = ButtonStyle.SUCCESS) {
        filterActions.add new Triple<String, ButtonStyle, String>(i18n, style, "/${action.toString()}/${action.method}" as String)
    }

    @Override
    void setAdditionalParams(String key, String value) {
        mapAdditionalHiddenParams.put(key, new HTMLInput(InputType.HIDDEN, value, key))
    }

    @Override
    void addHiddenInputs() {
        formThemed.builder.addChildren(mapAdditionalHiddenParams.values() as IHTMLElement[])
    }

    @Override
    void visitInnerFilter(UiFilterSpecifier uiFilterSpecifier, FieldInfo... fieldInfos) {

    }
}
