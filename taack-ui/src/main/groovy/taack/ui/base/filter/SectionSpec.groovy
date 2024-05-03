package taack.ui.base.filter

import groovy.transform.CompileStatic
import taack.ast.type.FieldInfo
import taack.ui.IEnumOption
import taack.ui.base.filter.expression.FilterExpression

@CompileStatic
class SectionSpec {
    final IUiFilterVisitor filterVisitor

    SectionSpec(final IUiFilterVisitor filterVisitor) {
        this.filterVisitor = filterVisitor
    }

    void filterFieldExpressionBool(final FilterExpression filterExpression) {
        filterVisitor.visitFilterFieldExpressionBool(filterExpression)
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

    final <U> void filterExtension(final String i18n, final FieldInfo... field) {
        filterVisitor.visitFilterExtension(i18n, field)
    }
}
