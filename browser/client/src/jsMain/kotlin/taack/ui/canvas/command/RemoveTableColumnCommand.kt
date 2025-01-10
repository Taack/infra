package taack.ui.canvas.command

import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.canvas.ICanvasDrawable
import taack.ui.canvas.table.CanvasTable
import taack.ui.canvas.table.TxtHeaderCanvas
import taack.ui.canvas.table.TxtRowCanvas
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