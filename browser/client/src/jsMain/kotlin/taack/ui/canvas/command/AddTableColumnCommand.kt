package taack.ui.canvas.command

import taack.ui.canvas.table.CanvasTable
import taack.ui.canvas.table.TxtHeaderCanvas

class AddTableColumnCommand(val table: CanvasTable, val text: TxtHeaderCanvas) : ICanvasCommand {
    override fun doIt() {
        table.addColumn(text)
    }
}