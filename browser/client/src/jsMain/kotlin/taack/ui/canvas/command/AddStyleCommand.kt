package taack.ui.canvas.command

import taack.ui.canvas.text.TextStyle
import taack.ui.canvas.text.CanvasText

class AddStyleCommand(val text: CanvasText, val style: TextStyle, private val start: Int, private val end: Int) : ICanvasCommand {
    override fun doIt(): Boolean {
        text.addStyle(style, start, end)
        return true
    }
}