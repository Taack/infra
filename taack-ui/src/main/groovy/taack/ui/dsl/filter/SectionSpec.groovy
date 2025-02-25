package taack.ui.dsl.filter

import groovy.transform.CompileStatic
import taack.ast.type.FieldInfo
import taack.ui.IEnumOption
import taack.ui.dsl.UiFilterSpecifier
import taack.ui.dsl.filter.expression.FilterExpression

@CompileStatic
final class SectionSpec extends FilterCommon {
    SectionSpec(final IUiFilterVisitor filterVisitor) {
        super(filterVisitor)
    }

    void filterFieldExpressionBool(final String i18n, final Boolean defaultValue = true, final FilterExpression... filterExpressions) {
        filterVisitor.visitFilterFieldExpressionBool(i18n, defaultValue, filterExpressions)
    }

    void filterField(final String i18n, final IEnumOption[] enumOptions, final FieldInfo... fields) {
        filterVisitor.visitFilterField(i18n, enumOptions, fields)
    }

    void filterField(final IEnumOption[] enumOptions, final FieldInfo... fields) {
        filterVisitor.visitFilterField(null, enumOptions, fields)
    }

    void filterField(final FieldInfo... fields) {
        if (fields == null || fields.size() == 0) return
        filterVisitor.visitFilterField(null, null, fields)
    }

    void filterField(String i18n, final FieldInfo... fields) {
        if (fields == null || fields.size() == 0) return
        filterVisitor.visitFilterField(i18n, null, fields)
    }

    final <T> void filterFieldInverse(final String i18n, final Class<T> reverseClass, final FieldInfo field, final FieldInfo... fields) {
        if (fields == null || fields.size() == 0) return
        filterVisitor.visitFilterFieldReverse(i18n, reverseClass, field, fields)
    }

}
