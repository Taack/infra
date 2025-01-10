package taack.ui.canvas.command

import taack.ui.base.Helper.Companion.trace
import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.canvas.ICanvasDrawable
import taack.ui.canvas.table.CanvasTable
import web.uievents.MouseEvent

class RmCharCommand(private val drawables: MutableList<ICanvasDrawable>, private val textIndex: Int, private val textSubIndex: Int, private val pos: Int, private val mouseEvent: MouseEvent?) : ICanvasCommand {
    override fun doIt(): Boolean {
        traceIndent("RmCharCommand +++")
        val text = embeddedText(drawables, textIndex, textSubIndex, mouseEvent)
        trace("RmCharCommand ${text?.txt}, ${mouseEvent?.offsetX} ${mouseEvent?.offsetY}")
        val ret = text?.rmChar(pos) == 0
        traceDeIndent("RmCharCommand --- ${text?.txt}, ret = $ret")
        return true
    }
}