package taack.ui.canvas.command

import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.canvas.table.CanvasTable
import taack.ui.canvas.table.TxtHeaderCanvas

class AddTableColumnCommand(val table: CanvasTable, val text: TxtHeaderCanvas) : ICanvasCommand {
    override fun doIt(): Boolean {
        traceIndent("AddTableColumnCommand +++")
        table.addColumn(text)
        traceDeIndent("AddTableColumnCommand ---")
        return true
    }
}