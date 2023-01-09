package taack.ui.dump

import groovy.transform.CompileStatic
import taack.ast.type.FieldInfo
import taack.ast.type.GetMethodReturn
import taack.ui.base.common.Style
import taack.ui.base.table.ColumnHeaderFieldSpec
import taack.ui.base.table.UiTableVisitorImpl
import taack.ui.style.EnumStyle

import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat

@CompileStatic
final class RawCsvTableDump extends UiTableVisitorImpl {
    final private ByteArrayOutputStream out

    final static char sep = '|'

    @Override
    void visitSortableFieldHeader(String i18n, String controller, String action, Map<String, ?> params, Map<String, ?> additionalParams) {
        out << "\"${i18n?.replace(sep, (char)':')}\"${sep}"
    }

    @Override
    void visitRowField(EnumStyle value, Style style) {
        out << (value?.name?:"") + sep
    }

    @Override
    void visitRowField(BigDecimal value, NumberFormat numberFormat, Style style) {
        out << (value?.toString()?:"") + sep
    }

    @Override
    void visitSortableFieldHeader(String i18n, FieldInfo fieldInfo, ColumnHeaderFieldSpec.DefaultSortingDirection defaultDirection) {
        i18n ?= fieldInfo.fieldName
        out << "\"${i18n.replace(sep, (char)':')}\"${sep}"
    }

    @Override
    void visitSortableFieldHeader(String i18n, FieldInfo[] fields, ColumnHeaderFieldSpec.DefaultSortingDirection defaultDirection) {
        i18n ?= fields*.fieldName.join('>')
        out << "\"${i18n.replace(sep, (char)':')}\"${sep}"
    }

    RawCsvTableDump(final ByteArrayOutputStream out) {
        this.out = out
    }

    @Override
    void visitHeaderEnd() {
        out << "\n"
    }

    @Override
    void visitRowEnd() {
        out << "\n"
    }

    @Override
    void visitFieldHeader(final String i18n) {
        out << "\"${i18n.replace(sep, (char)':')}\"${sep}"
    }

    @Override
    void visitRowField(final FieldInfo fieldInfo, final String format = null, final Style style) {
        switch (fieldInfo.fieldConstraint.field.type) {
            case Long:
            case Integer:
                visitRowField((Long) fieldInfo.value, style)
                break
            case Double:
            case Float:
            case BigDecimal:
                visitRowField((BigDecimal) fieldInfo.value, style)
                break
            case Date:
                visitRowField((Date) fieldInfo.value, style)
                break
            default:
                visitRowField((String) fieldInfo.value, style)
        }
    }

    @Override
    void visitRowField(final GetMethodReturn fieldInfo, final Style style) {
        switch (fieldInfo.getMethod().returnType) {
            case Long:
            case Integer:
                visitRowField((Long) fieldInfo.value, style)
                break
            case Double:
            case Float:
            case BigDecimal:
                visitRowField((BigDecimal) fieldInfo.value, style)
                break
            case Date:
                visitRowField((Date) fieldInfo.value, style)
                break
            default:
                visitRowField((String) fieldInfo.value, style)
        }
    }

    private static String surroundCell(final String cell) {
        "\"${cell?cell.replace(sep, (char)':').replace('"', '_'):''}\"${sep}"
    }

    @Override
    void visitRowField(final String value, final Style style) {
        out << surroundCell(value)
    }

    @Override
    void visitRowField(final Long value, final Style style) {
        out << surroundCell(value?.toString())
    }

    @Override
    void visitRowField(final BigDecimal value, final String format = null, final Style style) {
        DecimalFormat df = new DecimalFormat(format ?: "#,###.00")
        out << surroundCell(df.format(value))
    }

    @Override
    void visitRowField(final Date value, final String format = null, final Style style) {
        SimpleDateFormat sdf = new SimpleDateFormat(format ?: "yyyy-MM-dd")
        out << surroundCell(sdf.format(value))
    }
}
