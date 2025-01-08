package taack.ui.canvas.command

import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.canvas.ICanvasDrawable
import taack.ui.canvas.text.CanvasText

class RmCharCommand(private val drawables: MutableList<ICanvasDrawable>, val text: CanvasText, private val pos: Int) : ICanvasCommand {
    override fun doIt(): Boolean {
        traceIndent("RmCharCommand +++ ${text.txt}")
        var ret = true
        if (text.rmChar(pos) == 0) {
            ret = drawables.remove(text)
        }
        traceDeIndent("RmCharCommand --- ${text.txt}")
        return ret
    }
}