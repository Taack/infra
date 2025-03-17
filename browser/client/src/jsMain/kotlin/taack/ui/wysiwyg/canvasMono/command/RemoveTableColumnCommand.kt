package taack.ui.wysiwyg.canvasMono.command

import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.wysiwyg.canvasMono.ICanvasDrawable
import taack.ui.wysiwyg.canvasMono.table.CanvasTable
import web.uievents.MouseEvent

class RemoveTableColumnCommand(private val drawables: MutableList<ICanvasDrawable>, private val textIndex: Int, private val textSubIndex: Int, private val mouseEvent: MouseEvent?) : ICanvasCommand {
    override fun doIt(): Boolean {
        traceIndent("RemoveTableColumnCommand +++")
        val table = drawables[textIndex] as CanvasTable
//        val text = embeddedText(drawables, textIndex, textSubIndex, mouseEvent) as TxtHeaderCanvas
        table.removeColumn(textSubIndex)
        traceDeIndent("RemoveTableColumnCommand ---")

        return true
    }
}