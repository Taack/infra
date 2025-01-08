package taack.ui.canvas.command

import taack.ui.canvas.table.CanvasTable
import taack.ui.canvas.table.TxtHeaderCanvas

class RemoveTableColumnCommand(val table: CanvasTable, val text: TxtHeaderCanvas) : ICanvasCommand {
    override fun doIt(): Boolean {
        table.removeColumn(text)
        return true
    }
}