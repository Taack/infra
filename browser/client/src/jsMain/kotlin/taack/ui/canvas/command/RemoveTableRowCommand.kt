package taack.ui.canvas.command

import taack.ui.canvas.table.CanvasTable
import taack.ui.canvas.table.TxtRowCanvas

class RemoveTableRowCommand(val table: CanvasTable, val text: TxtRowCanvas) : ICanvasCommand {
    override fun doIt() {
        table.removeLine(text)
    }
}