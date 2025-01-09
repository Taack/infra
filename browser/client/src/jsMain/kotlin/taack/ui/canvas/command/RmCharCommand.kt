package taack.ui.canvas.command

import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.canvas.ICanvasDrawable

class RmCharCommand(private val drawables: MutableList<ICanvasDrawable>, private val textIndex: Int, private val pos: Int) : ICanvasCommand {
    override fun doIt(): Boolean {
        val text = drawables[textIndex].getSelectedText()!!
        traceIndent("RmCharCommand +++ ${text.txt}")
        val ret = text.rmChar(pos) == 0
        traceDeIndent("RmCharCommand --- ${text.txt}, ret = $ret")
        return true
    }
}