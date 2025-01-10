package taack.ui.canvas.command

import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.canvas.ICanvasDrawable
import web.uievents.MouseEvent

class AddCharCommand(private val drawables: MutableList<ICanvasDrawable>, private val textIndex: Int, private val textSubIndex: Int, private val pos: Int? = null, private val ch: String, private val mouseEvent: MouseEvent?) : ICanvasCommand {
    override fun doIt(): Boolean {
        val text = embeddedText(drawables, textIndex, textSubIndex, mouseEvent)
        traceIndent("AddCharCommand +++ ${text?.txt}, $pos, $ch")
        text?.addChar(ch, pos)
        traceDeIndent("AddCharCommand --- ${text?.txt}")
        return true
    }
}