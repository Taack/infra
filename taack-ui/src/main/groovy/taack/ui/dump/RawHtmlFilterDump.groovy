package taack.ui.dump

import grails.util.Pair
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.ast.type.FieldInfo
import taack.base.TaackSimpleFilterService
import taack.ui.EnumOption
import taack.ui.base.filter.IUiFilterVisitor
import taack.ui.base.filter.expression.FilterExpression
import taack.ui.base.helper.Utils
import taack.ui.theme.BootstrapHtmlTheme

@CompileStatic
final class RawHtmlFilterDump implements IUiFilterVisitor {

    final private ByteArrayOutputStream out
    final private Parameter parameter
    final private List<Pair<String, MethodClosure>> filterActions = []
    final private BootstrapHtmlTheme htmlTheme = new BootstrapHtmlTheme()
    final private boolean testI18n

    RawHtmlFilterDump(final ByteArrayOutputStream out, final Parameter parameter) {
        this.out = out
        this.parameter = parameter
        this.testI18n = parameter.map['lang'] == 'test'
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
        out << htmlTheme.formContainerHeader()
        out << """
                <form name="${parameter.aClassSimpleName}_Filter" class="${htmlTheme.getFilterFormCssTheme(aClass)}" taackFilterId="${parameter.modalId}">
                <input type="hidden" name="sort" value="${parameter.sort ?: ''}">
                <input type="hidden" name="order" value="${parameter.order ?: ''}">
                <input type="hidden" name="offset" value="${parameter.offset}">
                <input type="hidden" name="max" value="${parameter.max}">
                <input type="hidden" name="additionalId" value="${parameter.additionalId ?: ''}">
                <input type="hidden" name="brand" value="${parameter.brand ?: ''}">
                <input type="hidden" name="className" value="${aClass.name ?: ''}">
                <input type="hidden" name="fieldName" value="${parameter.fieldName ?: ''}">
                ${Utils.getAdditionalInputs(additionalParams)}
                """
    }

    @Override
    void visitHiddenId(Long id) {
        out << """<input type="hidden" name="id" value="$id">"""
    }

    @Override
    void visitFilterEnd() {
        out << """
            ${htmlTheme.filterButtons(parameter, filterActions)}
            ${htmlTheme.formContainerFooter()}   
            """
    }

    @Override
    void visitSection(final String i18n) {
        out << htmlTheme.filterSectionHeader(i18n)
    }

    @Override
    void visitSectionEnd() {
        out << htmlTheme.filterSectionFooter()
    }

    private filterField(final String i18n, final String qualifiedName, final String value, final FieldInfo fieldInfo = null, final EnumOption[] enumOptions = null) {
        final boolean isBoolean = fieldInfo?.fieldConstraint?.field?.type == Boolean
        final boolean isEnum = fieldInfo?.fieldConstraint?.field?.type?.isEnum()
        final String qualifiedId = qualifiedName + "-" + parameter.modalId
        out << htmlTheme.filterFieldHeader(i18n, qualifiedId, (!isBoolean))
        if (enumOptions) {
            EnumOption[] enumConstraints = enumOptions

            out << htmlTheme.selectHeader()
            out << """
                <select class="${htmlTheme.getSelectCssTheme()}" name="${qualifiedName}" id="${qualifiedId}Select">
                <option value="">-${i18n}-</option>
                """
            enumConstraints.each {
                out << """<option value="${it.key}" ${parameter.map[qualifiedName]?.toString()?.equals(it.key) || fieldInfo?.value?.toString()?.equals(it.key) ? 'selected="selected"' : ''}>${it.value}</option>"""
            }
            out << '</select>'
            out << htmlTheme.selectFooter()
        } else if (isEnum) {
            final Class type = fieldInfo.fieldConstraint.field.type

            out << htmlTheme.selectHeader()
            out << """
                <select class="${htmlTheme.getSelectCssTheme()}" name="${qualifiedName}" id="${qualifiedId}Select">
                <option value="">-${i18n}-</option>
                """

            def values = type.invokeMethod('values', null) as List
            values.each {
                out << """<option value="${it}" ${parameter.map[qualifiedName]?.toString()?.equals(it.toString()) || fieldInfo?.value?.toString()?.equals(it.toString()) ? 'selected="selected"' : ''}>${it}</option>"""
            }
            out << '</select>'
            out << htmlTheme.selectFooter()
        } else if (isBoolean) {
            Boolean isChecked = parameter.map[qualifiedName + 'Default'] ?
                    ((parameter.map[qualifiedName] && parameter.map[qualifiedName] == '1') ? true : (parameter.map[qualifiedName] && parameter.map[qualifiedName] == '0') ? false : null) : fieldInfo.value
            out << htmlTheme.radioHeader()
            out << """
                <div class="${htmlTheme.getRadioDivCssTheme()}">
                    <input type="radio" name="${qualifiedName}" value="1" id="${qualifiedId}Check1" class="${htmlTheme.getRadioCssTheme()}" ${isChecked ? 'checked=""' : ''}>${htmlTheme.radioLabel('Yes', qualifiedName, '1')}
                </div>
                <div class="${htmlTheme.getRadioDivCssTheme()}">
                    <input type="radio" name="${qualifiedName}" value="0" id="${qualifiedId}Check0" class="${htmlTheme.getRadioCssTheme()}" ${(isChecked != null && isChecked == false) ? 'checked=""' : ''}>${htmlTheme.radioLabel('No', qualifiedName, '0')}
                </div>
                <div class="${htmlTheme.getRadioDivCssTheme()}">
                    <input type="radio" name="${qualifiedName}" value="" id="${qualifiedId}CheckNull" class="${htmlTheme.getRadioCssTheme()}" ${isChecked == null ? 'checked=""' : ''}>${htmlTheme.radioLabel("Unset", qualifiedName, "")}
                </div>
                <input type="hidden" name="${qualifiedName}Default" value="1" id="${qualifiedId}Default">
                """
            out << htmlTheme.radioFooter()
        } else {
            out << """
                <input class="${htmlTheme.getFilterInputCssTheme()}" id="${qualifiedId}" name="${qualifiedName}" type="text" value="${value ?: fieldInfo?.value ?: ''}" autocomplete="off" autofocus placeholder="${i18n?.replace('"', '\\u0027')}">
                """
        }
        out << htmlTheme.filterFieldFooter(i18n, qualifiedId, (!enumOptions && !isEnum && !isBoolean))
    }

    @Override
    void visitFilterField(final String i18n, final FieldInfo field, EnumOption[] enumOptions) {
        final String qualifiedName = getQualifiedName(field)
        filterField(i18n ?: parameter.trField(field), qualifiedName, parameter.map[qualifiedName]?.toString(), field, enumOptions)
    }

    @Override
    void visitFilterField(String i18n, FieldInfo[] fields, EnumOption[] enumOptions) {
        final String qualifiedName = getQualifiedName(fields)
        filterField(i18n ?: parameter.trField(fields), qualifiedName, parameter.map[qualifiedName]?.toString(), fields?.last(), enumOptions)
    }

    @Override
    void visitFilterFieldReverse(String i18n, Class reverseClass, FieldInfo reverseField, FieldInfo... fields) {
        final String qualifiedName = getQualifiedName(reverseClass, reverseField, fields)
        filterField(i18n, qualifiedName, parameter.map[qualifiedName]?.toString())
    }

    @Override
    void visitFilterFieldExpressionBool(FilterExpression filterExpression) {
        visitFilterFieldExpressionBool(null, filterExpression, true)
    }

    @Override
    void visitFilterFieldExpressionBool(String i18n, FilterExpression filterExpression, Boolean defaultValue) {
        final String qualifiedId = filterExpression.qualifiedName + '-' + parameter.modalId
        out << htmlTheme.expressionBoolLabel(filterExpression.qualifiedName, i18n)
        boolean isChecked = parameter.map[filterExpression.qualifiedName + 'Default'] ? parameter.map[filterExpression.qualifiedName] == '1' : defaultValue
        out << """
                ${htmlTheme.expressionBoolHeader()}
                    <input type="checkbox" name="${filterExpression.qualifiedName}" value="1" id="${qualifiedId}Check" ${isChecked ? 'checked=""' : ''} class="${htmlTheme.getCheckboxCssTheme()}">
                    <input type="hidden" name="${filterExpression.qualifiedName}Default" value="1" id="${qualifiedId}Default">
                </div>
                    """
        out << htmlTheme.expressionBoolFooter()
    }

    @Override
    void visitFilterFieldExpressionBool(String i18n, FilterExpression[] filterExpressions, Boolean defaultValue) {
        String qualifiedName = filterExpressions*.qualifiedName.join('_')
        final String qualifiedId = qualifiedName + '-' + parameter.modalId
        out << htmlTheme.expressionBoolLabel(qualifiedName, i18n)
        boolean isChecked = parameter.map[qualifiedName + 'Default'] ? parameter.map[qualifiedName] == '1' : defaultValue
        out << """
                ${htmlTheme.expressionBoolHeader()}
                    <input type="checkbox" name="${qualifiedName}" value="1" id="${qualifiedId}Check" ${isChecked ? 'checked=""' : ''} class="${htmlTheme.getCheckboxCssTheme()}">
                    <input type="hidden" name="${qualifiedName}Default" value="1" id="${qualifiedId}Default">
                </div>
                    """
        out << htmlTheme.expressionBoolFooter()
    }

    @Override
    void visitFilterAction(String i18n, MethodClosure action) {
        filterActions.add new Pair<String, MethodClosure>(i18n, action)
    }

    @Override
    void visitFilterExtension(String i18n, FieldInfo... fieldInfo) {
        def qualifiedName = "_extension_${getQualifiedName(fieldInfo)}"

        final String qualifiedId = qualifiedName + '-' + parameter.modalId
        out << htmlTheme.filterFieldHeader(i18n, qualifiedId, false)

        def choices = TaackSimpleFilterService.filterExtensionMap[fieldInfo.last().fieldConstraint.field.type].enumChoices()

        if (choices) {
            out << htmlTheme.selectHeader()
            out << """
                <select class="${htmlTheme.getSelectCssTheme()}" name="${qualifiedName}" id="${qualifiedId}Select">
                <option value="">-${i18n}-</option>
                """
            choices.each {
                out << """<option value="${it.key}" ${parameter.map[qualifiedName.toString()]?.toString()?.equals(it.key?.toString()) ? 'selected="selected"' : ''}>${it.value}</option>"""
            }
            out << '</select>'
            out << htmlTheme.selectFooter()
        } else {
            out << """
                <input class="${htmlTheme.getFilterInputCssTheme()}" id="${qualifiedId}" name="${qualifiedName}" type="text" value="" autocomplete="off" autofocus placeholder="${i18n?.replace('"', '\\u0027')}">
                """
            out << htmlTheme.filterFieldFooter(i18n, qualifiedId, false)
        }
    }
}
