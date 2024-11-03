package taack.ui.canvas.command

import taack.ui.canvas.ICanvasDrawable
import taack.ui.canvas.item.CanvasImg

class AddImageCommand(private val drawables: MutableList<ICanvasDrawable>, val i: Int, val image: CanvasImg) : ICanvasCommand {
    override fun doIt() {
        drawables.add(i, image)
    }
}