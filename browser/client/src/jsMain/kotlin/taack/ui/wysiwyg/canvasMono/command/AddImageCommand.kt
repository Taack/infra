package taack.ui.wysiwyg.canvasMono.command

import taack.ui.wysiwyg.canvasMono.ICanvasDrawable
import taack.ui.wysiwyg.canvasMono.item.CanvasImg

class AddImageCommand(private val drawables: MutableList<ICanvasDrawable>, val i: Int, val image: CanvasImg) : ICanvasCommand {
    override fun doIt(): Boolean {
        drawables.add(i, image)
        return true
    }
}