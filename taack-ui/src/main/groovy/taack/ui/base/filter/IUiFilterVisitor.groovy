package taack.ui.base.filter

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.ast.type.FieldInfo
import taack.ui.EnumOption
import taack.ui.base.filter.expression.FilterExpression

@CompileStatic
interface IUiFilterVisitor {

    void visitFilter(Class aClass, Map<String, ? extends Object> additionalParams)

    void visitHiddenId(final Long id)

    void visitFilterEnd()

    void visitSection(final String i18n)

    void visitSectionEnd()

    void visitFilterField(final String i18n, final FieldInfo field, EnumOption[] enumOptions)

    void visitFilterField(final String i18n, final FieldInfo[] fields, EnumOption[] enumOptions)

    void visitFilterFieldReverse(String i18n, Class reverseClass, FieldInfo reverseField, FieldInfo... fields)

    void visitFilterFieldExpressionBool(String i18n, FilterExpression filterExpression, Boolean defaultValue)

    void visitFilterFieldExpressionBool(String i18n, FilterExpression[] filterExpressions, Boolean defaultValue)

    void visitFilterAction(String i18n, MethodClosure action)
}