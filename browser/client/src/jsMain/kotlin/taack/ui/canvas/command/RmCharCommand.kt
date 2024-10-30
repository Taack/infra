package taack.ui.canvas.command

import taack.ui.canvas.ICanvasDrawable
import taack.ui.canvas.text.CanvasText

class RmCharCommand(private val drawables: MutableList<ICanvasDrawable>, val text: CanvasText, private val pos: Int) : ICanvasCommand {
    override fun doIt() {
        if (text.rmChar(pos) == 0) {
            drawables.remove(text)
        }

    }
}