package taack.ui.vt100.render

import groovy.transform.CompileStatic
import taack.ast.type.FieldInfo
import taack.ui.base.common.Style
import taack.ui.base.table.ColumnHeaderFieldSpec
import taack.ui.base.table.UiTableVisitorImpl
import taack.ui.vt100.render.DisplayManager as DM

import java.text.NumberFormat

@CompileStatic
final class RenderingTable extends UiTableVisitorImpl {
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
    void visitFieldHeader(String i18n) {
        headers.add i18n
    }

    @Override
    void visitSortableFieldHeader(String i18n, FieldInfo[] fields) {
        headers.add i18n
        sortableHeaders2.put i18n, fields
    }

    @Override
    void visitRow(Object current, Style style, boolean hasChildren) {
        dm.gotoNextLine()

        currentLineSize = 0
    }

    private void drawCell(String value, boolean padLeft = true) {
        dm.addFocusableCell(value, colWidth, padLeft)
        currentLineSize++
        if (currentLineSize < headers.size()) {
            dm.addDecoration(colSep.toString())
        }
    }

    @Override
    void visitRowField(String value, Style style, String controller, String action, Long id) {
        drawCell(value.toString())
    }

    @Override
    void visitRowField(Long value, Style style, String controller, String action, Long id) {
        drawCell(value.toString(), false)
    }

    @Override
    void visitRowField(BigDecimal value, String format, Style style, String controller, String action, Long id) {
        drawCell(value.toString(), false)
    }

    @Override
    void visitRowField(Date value, String format, Style style, String controller, String action, Long id) {
        drawCell(value.toString())
    }

    @Override
    void visitTableEnd() {
        dm.draw()
//        dm.goTo(tablePos)
        dm.nav()
    }

    @Override
    void visitRowField(BigDecimal value, NumberFormat numberFormat, Style style, String controller, String action, Long id) {
        drawCell(value.toString(), false)
    }
}
