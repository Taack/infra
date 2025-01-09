package taack.ui.canvas.command

import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.canvas.ICanvasDrawable

class RmCharCommand(private val drawables: MutableList<ICanvasDrawable>, private val textIndex: Int, private val pos: Int) : ICanvasCommand {
    override fun doIt(): Boolean {
        val text = drawables[textIndex].getSelectedText()!!
        traceIndent("RmCharCommand +++ ${text.txt}")
        var ret = true
        if (text.rmChar(pos) == 0) {
            ret = drawables.remove(text)
        }
        traceDeIndent("RmCharCommand --- ${text.txt}")
        return ret
    }
}