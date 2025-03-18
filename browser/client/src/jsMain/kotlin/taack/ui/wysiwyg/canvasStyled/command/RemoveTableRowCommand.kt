package taack.ui.wysiwyg.command

import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.wysiwyg.canvasStyled.ICanvasDrawable
import taack.ui.wysiwyg.canvasStyled.table.CanvasTable
import web.uievents.MouseEvent

class RemoveTableRowCommand(private val drawables: MutableList<ICanvasDrawable>, private val textIndex: Int, private val textSubIndex: Int, private val mouseEvent: MouseEvent?) : ICanvasCommand {
    override fun doIt(): Boolean {
        traceIndent("RemoveTableRowCommand +++")
        val table = drawables[textIndex] as CanvasTable
        table.removeLine(textSubIndex)
        traceDeIndent("RemoveTableRowCommand ---")

        return true
    }
}