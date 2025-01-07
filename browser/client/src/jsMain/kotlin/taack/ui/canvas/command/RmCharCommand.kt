package taack.ui.canvas.command

import taack.ui.canvas.ICanvasDrawable
import taack.ui.canvas.text.CanvasText

class RmCharCommand(private val drawables: MutableList<ICanvasDrawable>, val text: CanvasText, private val pos: Int) : ICanvasCommand {
    override fun doIt() {
        if (text.txt.isEmpty() || text.rmChar(pos) == 0) {
            val index = drawables.indexOf(text)
            if (index >= 0) drawables.removeAt(index)
        }

    }
}