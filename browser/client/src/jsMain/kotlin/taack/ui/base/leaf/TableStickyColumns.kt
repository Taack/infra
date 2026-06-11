package taack.ui.base.leaf

import js.array.asList
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.element.Table
import web.cssom.ClassName
import web.html.HTMLTableCellElement
import web.html.HTMLTableRowElement

/**
 * Make the N leftmost columns of a table sticky
 *
 * The server tags the table with `taackStickyColumns="N"` (see ThemableTable) and puts it in a `.taack-sticky-scroll` horizontal scroller.
 * The only thing CSS cannot do is sum the widths of the preceding frozen columns to position each one,
 * so that is done here from the rendered header
 *
 * Runs once per table from [Table]'s init (which re-runs on every AJAX refresh) so the offsets are
 * always calculated against fresh DOM
 */
class TableStickyColumns {
    companion object {
        fun freeze(p: Table) {
            val count = p.t.attributes.getNamedItem("taackStickyColumns")?.value?.toIntOrNull() ?: return
            if (count <= 0) return

            // Header row gives the column widths shared by every body row
            val headRow = p.t.querySelector("thead tr") as? HTMLTableRowElement ?: return
            val headCells = headRow.children
            val frozen = minOf(count, headCells.length)
            if (frozen <= 0) return

            trace("TableStickyColumns::freeze ${p.tableId} columns: $frozen")

            // Left offset of each frozen column = sum of widths of the columns before it
            val offsets = DoubleArray(frozen)
            var accumulatedWidth = 0.0
            for (i in 0 until frozen) {
                offsets[i] = accumulatedWidth
                accumulatedWidth += (headCells[i] as HTMLTableCellElement).getBoundingClientRect().width
            }

            val rows = p.t.querySelectorAll("thead tr, tbody tr").asList()
            for (row in rows) {
                val cells = (row as HTMLTableRowElement).children
                val frozenInRow = minOf(frozen, cells.length)
                for (i in 0 until frozenInRow) {
                    val cell = cells[i] as HTMLTableCellElement
                    cell.classList.add(ClassName("taack-sticky-cell"))
                    cell.style.left = "${offsets[i]}px"
                    if (i == frozen - 1) cell.classList.add(ClassName("taack-sticky-cell-last"))
                }
            }
        }
    }
}
