package taack.ui.canvas.command

import taack.ui.canvas.ICanvasDrawable
import taack.ui.canvas.text.CanvasText

class DeleteTextCommand(private val drawables: MutableList<ICanvasDrawable>, val text: CanvasText) : ICanvasCommand {
    override fun doIt() {
        drawables.remove(text)
    }
}