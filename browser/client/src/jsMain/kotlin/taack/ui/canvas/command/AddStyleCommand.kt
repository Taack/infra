package taack.ui.canvas.command

import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.canvas.text.TextStyle
import taack.ui.canvas.text.CanvasText

class AddStyleCommand(val text: CanvasText, val style: TextStyle, private val start: Int, private val end: Int) : ICanvasCommand {
    override fun doIt(): Boolean {
        traceIndent("AddStyleCommand +++ ${text.txt}, $start, $end")
        text.addStyle(style, start, end)
        traceDeIndent("AddStyleCommand --- ${text.txt}")
        return true
    }
}