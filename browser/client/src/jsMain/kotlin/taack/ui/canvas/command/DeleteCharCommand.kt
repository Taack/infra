package taack.ui.canvas.command

import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.canvas.ICanvasDrawable
import web.uievents.MouseEvent

class DeleteCharCommand(
    private val drawables: MutableList<ICanvasDrawable>,
    private val textIndex: Int,
    private val textSubIndex: Int,
    private val pos1: Int,
    private val pos2: Int?,
    private val mouseEvent: MouseEvent?
) : ICanvasCommand {
    override fun doIt(): Boolean {
        traceIndent("DeleteCharCommand +++")
        val text = embeddedText(drawables, textIndex, textSubIndex, mouseEvent)
        val ret = text?.delChar(pos1, pos2) == 0
        traceDeIndent("DeleteCharCommand --- ${text?.txt}, ret = $ret")
        return true
    }
}