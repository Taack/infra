package taack.ui.canvas.command

import taack.ui.canvas.ICanvasDrawable
import taack.ui.canvas.table.CanvasTable

class AddTableCommand(private val drawables: MutableList<ICanvasDrawable>, val i: Int) : ICanvasCommand {
    override fun doIt(): Boolean {
        drawables.add(i, CanvasTable.createTable())
        return true
    }
}