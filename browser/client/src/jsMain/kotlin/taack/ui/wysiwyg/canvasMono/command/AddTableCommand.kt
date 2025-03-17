package taack.ui.wysiwyg.canvasMono.command

import taack.ui.wysiwyg.canvasMono.ICanvasDrawable
import taack.ui.wysiwyg.canvasMono.table.CanvasTable

class AddTableCommand(private val drawables: MutableList<ICanvasDrawable>, val i: Int) : ICanvasCommand {
    override fun doIt(): Boolean {
        drawables.add(i, CanvasTable.createTable())
        return true
    }
}