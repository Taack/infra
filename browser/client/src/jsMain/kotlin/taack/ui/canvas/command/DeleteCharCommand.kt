package taack.ui.canvas.command

import taack.ui.canvas.ICanvasDrawable
import taack.ui.canvas.text.CanvasText

class DeleteCharCommand(
    private val drawables: MutableList<ICanvasDrawable>,
    val text: CanvasText,
    private val pos1: Int,
    private val pos2: Int?
) : ICanvasCommand {
    override fun doIt() {
        if (text.delChar(pos1, pos2) == 0) {
            drawables.remove(text)
        }
    }
}