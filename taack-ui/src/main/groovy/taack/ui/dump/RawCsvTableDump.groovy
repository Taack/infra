package taack.ui.dump

import grails.util.Pair
import groovy.transform.CompileStatic
import taack.ast.type.FieldInfo
import taack.ast.type.GetMethodReturn
import taack.ui.dsl.common.ActionIcon
import taack.ui.dsl.common.Style
import taack.ui.dsl.table.IUiTableVisitor

@CompileStatic
final class RawCsvTableDump implements IUiTableVisitor {
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
    void visitTable() {

    }

    @Override
    void visitTableWithoutFilter() {

    }

    @Override
    void visitTableEnd() {

    }

    @Override
    void visitHeader() {

    }

    @Override
    void visitHeaderEnd() {
        out << "\n"
    }

    @Override
    void visitColumn(Integer colSpan, Integer rowSpan) {

    }

    @Override
    void visitColumnEnd() {

    }

    @Override
    void visitRow(Style style, boolean hasChildren) {

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
    void visitFieldHeader(FieldInfo[] fields) {

    }

    @Override
    void visitRowColumn(Integer colSpan, Integer rowSpan, Style style) {

    }

    @Override
    void visitRowColumnEnd() {

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

    @Override
    void visitRowFieldRaw(String value, Style style) {
        out << surroundCell(value)
    }

    @Override
    void visitRowAction(String i18n, ActionIcon actionIcon, Long id, String label, Map<String, ?> params, Boolean isAjax) {

    }

    @Override
    void visitRowAction(String i18n, ActionIcon actionIcon, String controller, String action, Long id, Map<String, ?> params, Boolean isAjax) {

    }

    @Override
    void visitRowAction(String i18n, String controller, String action, Long id, Map<String, ?> params, Boolean isAjax) {

    }

    @Override
    void visitRowIndent(Boolean isExpended) {

    }

    @Override
    void visitRowIndentEnd() {

    }

    @Override
    void visitPaginate(Number max, Number count) {

    }

    @Override
    void setSortingOrder(Pair<String, String> sortingOrder) {

    }

    @Override
    Pair<String, String> getSortingOrder() {
        return null
    }
}
