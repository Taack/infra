package taack.ui.wysiwyg.canvasMono.command

import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.wysiwyg.canvasMono.ICanvasDrawable
import taack.ui.wysiwyg.canvasMono.table.CanvasTable
import web.uievents.MouseEvent

class AddTableRowCommand(private val drawables: MutableList<ICanvasDrawable>, private val textIndex: Int, private val textSubIndex: Int, private val mouseEvent: MouseEvent?) : ICanvasCommand {
    override fun doIt(): Boolean {
        traceIndent("AddTableRowCommand +++")
        val table = drawables[textIndex] as CanvasTable
        table.addLine(textSubIndex)
        traceDeIndent("AddTableRowCommand ---")

        return true
    }
}