package taack.ui.canvas.command

import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.canvas.text.CanvasText

class AddCharCommand(val text: CanvasText, private val ch: String, private val pos: Int? = null) : ICanvasCommand {
    override fun doIt(): Boolean {
        traceIndent("AddCharCommand +++ ${text.txt}")
        text.addChar(ch, pos)
        traceDeIndent("AddCharCommand --- ${text.txt}")
        return true
    }
}