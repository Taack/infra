package taack.ui.canvas.command

import taack.ui.canvas.ICanvasDrawable
import taack.ui.canvas.table.CanvasTable

class AddTableCommand(val drawables: MutableList<ICanvasDrawable>, val i: Int) : ICanvasCommand {
    override fun doIt() {
        drawables.add(i, CanvasTable.createTable())
    }
}