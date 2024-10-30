package taack.ui.canvas.command

import taack.ui.canvas.text.CanvasText

class AddCharCommand(val text: CanvasText, private val ch: Char, private val pos: Int) : ICanvasCommand {
    override fun doIt() {
        text.addChar(ch, pos)
    }
}