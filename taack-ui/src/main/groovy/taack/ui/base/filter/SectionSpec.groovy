package taack.ui.base.filter

import groovy.transform.CompileStatic
import taack.ast.type.FieldInfo
import taack.ui.EnumOption
import taack.ui.base.filter.expression.FilterExpression

@CompileStatic
class SectionSpec {
    final IUiFilterVisitor filterVisitor

    SectionSpec(final IUiFilterVisitor filterVisitor) {
        this.filterVisitor = filterVisitor
    }

    void filterField(final FieldInfo field, final EnumOption[] enumOptions = null) {
        filterVisitor.visitFilterField(null, field, enumOptions)
    }

    void filterField(final String i18n, final FieldInfo field, final EnumOption[] enumOptions = null) {
        filterVisitor.visitFilterField(i18n, field, enumOptions)
    }

    void filterFieldExpressionBool(final FilterExpression filterExpression) {
        filterVisitor.visitFilterFieldExpressionBool(filterExpression)
    }

    void filterFieldExpressionBool(final String i18n, final FilterExpression filterExpression, final Boolean defaultValue = false) {
        filterVisitor.visitFilterFieldExpressionBool(i18n, filterExpression, defaultValue)
    }

    void filterFieldExpressionBool(final String i18n, final Boolean defaultValue = true, final FilterExpression... filterExpressions) {
        filterVisitor.visitFilterFieldExpressionBool(i18n, filterExpressions, defaultValue)
    }

    void filterField(final String i18n = null, final FieldInfo[] fields, final EnumOption[] enumOptions) {
        filterVisitor.visitFilterField(i18n, fields, enumOptions)
    }

    void filterField(final String i18n = null, final List<FieldInfo> fields, final EnumOption[] enumOptions) {
        filterVisitor.visitFilterField(i18n, fields as FieldInfo[], enumOptions)
    }

    void filterField(final EnumOption[] enumOptions, final FieldInfo... fields) {
        filterVisitor.visitFilterField(null, fields as FieldInfo[], enumOptions)
    }

    void filterField(final FieldInfo... fields) {
        if (fields == null || fields.size() == 0) return
        filterVisitor.visitFilterField(null, fields, null)
    }

    void filterField(String i18n, final FieldInfo... fields) {
        if (fields == null || fields.size() == 0) return
        filterVisitor.visitFilterField(i18n, fields, null)
    }

    @Deprecated
    void filterField(final String i18n = null, final List<FieldInfo> fields) {
        filterVisitor.visitFilterField(i18n, fields as FieldInfo[], null)
    }

    final <T> void filterFieldInverse(final String i18n, final Class<T> reverseClass, final FieldInfo field, final FieldInfo... fields) {
        if (fields == null || fields.size() == 0) return
        filterVisitor.visitFilterFieldReverse(i18n, reverseClass, field, fields)
    }

    final <U> void filterExtension(final String i18n, final FieldInfo... field) {
        filterVisitor.visitFilterExtension(i18n, field)
    }
}
