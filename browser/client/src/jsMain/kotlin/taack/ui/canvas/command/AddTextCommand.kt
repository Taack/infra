package taack.ui.canvas.command

import taack.ui.canvas.ICanvasDrawable
import taack.ui.canvas.text.CanvasText

class AddTextCommand(private val drawables: MutableList<ICanvasDrawable>, val i: Int, val text: CanvasText) : ICanvasCommand {
    override fun doIt() {
        if (i == -1) drawables.add(text)
        else drawables.add(i, text)
    }
}