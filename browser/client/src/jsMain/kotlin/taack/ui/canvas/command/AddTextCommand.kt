package taack.ui.canvas.command

import taack.ui.canvas.ICanvasDrawable
import taack.ui.canvas.text.CanvasText

class AddTextCommand(val drawables: MutableList<ICanvasDrawable>, val i: Int, val text: CanvasText) : ICanvasCommand {
    override fun doIt() {
        drawables.add(i, text)
    }
}