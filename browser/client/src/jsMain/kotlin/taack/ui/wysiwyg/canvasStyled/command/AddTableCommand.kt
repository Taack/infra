package taack.ui.wysiwyg.command

import taack.ui.wysiwyg.canvasStyled.ICanvasDrawable
import taack.ui.wysiwyg.canvasStyled.table.CanvasTable

class AddTableCommand(private val drawables: MutableList<ICanvasDrawable>, val i: Int) : ICanvasCommand {
    override fun doIt(): Boolean {
        drawables.add(i, CanvasTable.createTable())
        return true
    }
}