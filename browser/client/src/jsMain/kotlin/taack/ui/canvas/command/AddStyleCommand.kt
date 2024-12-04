package taack.ui.canvas.command

import taack.ui.canvas.text.CanvasStyle
import taack.ui.canvas.text.CanvasText

class AddStyleCommand(val text: CanvasText, val style: CanvasStyle, private val start: Int, private val end: Int) : ICanvasCommand {
    override fun doIt() {
        text.addStyle(style, start, end)
    }
}