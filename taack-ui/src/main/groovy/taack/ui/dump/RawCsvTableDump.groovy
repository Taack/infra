package taack.ui.dump

import groovy.transform.CompileStatic
import taack.ast.type.FieldInfo
import taack.ast.type.GetMethodReturn
import taack.ui.dsl.common.Style
import taack.ui.dsl.table.UiTableVisitorImpl

@CompileStatic
final class RawCsvTableDump extends UiTableVisitorImpl {
    final private ByteArrayOutputStream out

    final static char sep = '|'

    @Override
    void visitSortableFieldHeader(String i18n, FieldInfo[] fields) {
        i18n ?= fields*.fieldName.join('>')
        out << "\"${i18n.replace(sep, (char) ':')}\"${sep}"
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
        out << "\"${i18n.replace(sep, (char) ':')}\"${sep}"
    }

    @Override
    void visitRowField(final FieldInfo fieldInfo, final String format, final Style style) {
        visitRowField(fieldInfo.value?.toString(), null)
    }

    @Override
    void visitRowField(final GetMethodReturn fieldInfo, final String format, final Style style) {
        visitRowField(fieldInfo.value?.toString(), null)
    }

    private static String surroundCell(final String cell) {
        "\"${cell ? cell.replace(sep, (char) ':').replace('"', '_') : ''}\"${sep}"
    }

    @Override
    void visitRowField(final String value, final Style style) {
        out << surroundCell(value)
    }


}
