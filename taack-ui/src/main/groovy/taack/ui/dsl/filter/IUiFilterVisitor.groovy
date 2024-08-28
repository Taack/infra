package taack.ui.dsl.filter

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.ast.type.FieldInfo
import taack.ui.IEnumOption
import taack.ui.dsl.filter.expression.FilterExpression
import taack.ui.dump.html.element.ButtonStyle

@CompileStatic
interface IUiFilterVisitor  {

    void visitFilter(Class aClass, Map<String, ? extends Object> additionalParams)

    void visitHiddenId(final Long id)

    void visitFilterEnd()

    void visitSection(final String i18n)

    void visitSectionEnd()

    void visitFilterField(final String i18n, IEnumOption[] enumOptions, final FieldInfo... fields)

    void visitFilterFieldReverse(String i18n, Class reverseClass, FieldInfo reverseField, FieldInfo... fields)

    void visitFilterFieldExpressionBool(FilterExpression... filterExpression)

    void visitFilterFieldExpressionBool(String i18n, Boolean defaultValue, FilterExpression... filterExpressions)

    void visitFilterAction(String i18n, MethodClosure action, ButtonStyle style)
}