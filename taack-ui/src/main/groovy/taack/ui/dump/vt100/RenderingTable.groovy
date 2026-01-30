package taack.ui.dump.vt100

import grails.util.Pair
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.ast.type.FieldInfo
import taack.ast.type.GetMethodReturn
import taack.ui.IEnumOptions
import taack.ui.dsl.common.ActionIcon
import taack.ui.dsl.common.Style
import taack.ui.dsl.table.IUiTableVisitor
import taack.ui.dsl.table.TableOption
import taack.ui.dump.vt100.DisplayManager as DM

import java.text.DateFormat
import java.text.NumberFormat

@CompileStatic
final class RenderingTable implements IUiTableVisitor {
    private final DM dm

    final char colSep = '│'
    final char lineSep = '─'
    final char crossSep = '┼'

    int colWidth = 9

    private final List<String> headers = []
    private final Map<String, FieldInfo> sortableHeaders = [:]
    private final Map<String, FieldInfo[]> sortableHeaders2 = [:]

    private int currentLineSize = 0

    RenderingTable(InputStream inputStream, OutputStream outputStream) {
        dm = new DM(inputStream, outputStream)
    }

    @Override
    void visitHeader() {
    }

    @Override
    void visitHeaderEnd() {
        // Draw complete Header
        dm.newRegion(DisplayManager.AreaScrollMode.V_LOCKED)
        int numberOfCols = headers.size()
        colWidth = Math.max(dm.curDim.col / numberOfCols as int, 10)
        headers.eachWithIndex { it, inc ->
            dm.addFocusableCell(it, colWidth)
            if (inc < numberOfCols) {
                dm.addDecoration(colSep.toString())
            }
        }
        dm.gotoNextLine()
        headers.eachWithIndex { it, inc ->
            dm.addDecoration("$lineSep" * colWidth)
            if (inc < numberOfCols) {
                dm.addDecoration(crossSep.toString())
            }
        }
        dm.newRegion(DisplayManager.AreaScrollMode.NORMAL)
    }

    @Override
    void visitColumn(Integer colSpan, Integer rowSpan) {

    }

    @Override
    void visitColumnEnd() {

    }

    @Override
    void visitFieldHeader(String i18n) {
        headers.add i18n
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
    void visitRowField(FieldInfo fieldInfo, Long id = null, String format, Style style) {

    }

    @Override
    void visitRowField(GetMethodReturn fieldInfo, String format, Style style) {

    }

    @Override
    void visitSortableFieldHeader(String i18n, FieldInfo[] fields) {
        headers.add i18n
        sortableHeaders2.put i18n, fields
    }

    @Override
    void visitRow(Style style, boolean hasChildren) {
        dm.gotoNextLine()

        currentLineSize = 0
    }

    @Override
    void visitRowEnd() {

    }

    private void drawCell(String value, boolean padLeft = true) {
        dm.addFocusableCell(value, colWidth, padLeft)
        currentLineSize++
        if (currentLineSize < headers.size()) {
            dm.addDecoration(colSep.toString())
        }
    }

    @Override
    void visitRowField(String value, Style style) {
        drawCell(value.toString())
    }

    @Override
    void visitRowField(Number value, NumberFormat locale, Style style) {

    }

    @Override
    void visitRowField(Date value, DateFormat locale, Style style) {

    }

    @Override
    void visitRowFieldRaw(String value, Style style) {
        drawCell(value.toString())
    }

    @Override
    void visitRowAction(String i18n, ActionIcon actionIcon, String key, String label) {

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

    @Override
    void setLastReadingDate(Pair<Date, String> lastReadingDate) {

    }

    @Override
    String getLastReadingDateString() {
        return null
    }

    @Override
    Date getLastReadingDate() {
        return null
    }

    @Override
    String getReadingDateFieldString() {
        return null
    }

    @Override
    void visitColumnSelect(String paramsKey) {

    }

    @Override
    void visitColumnSelectButton(String buttonText, String controller, String action, Map<String, ?> params, Boolean isAjax) {

    }

    @Override
    void visitColumnSelectEnd() {

    }

    @Override
    void visitRowSelect(String value, boolean isSelectable) {

    }

    @Override
    void visitTableOption(TableOption tableOption) {

    }

    @Override
    void visitRowDropAction(MethodClosure dropAction, Map<String, ? extends Serializable> parameters) {

    }

    @Override
    void visitCellDropAction(MethodClosure dropAction, Map<String, ? extends Serializable> parameters) {

    }

    @Override
    String getSelectColumnParamsKey() {
        return null
    }

    @Override
    void visitRowFieldEdit(FieldInfo fieldInfo, String format, Style style, IEnumOptions eos) {

    }

    @Override
    void visitTable() {

    }

    @Override
    void visitTableWithoutFilter() {

    }

    @Override
    void visitTableEnd() {
        dm.draw()
//        dm.goTo(tablePos)
        dm.nav()
    }
}
