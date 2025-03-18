package taack.ui.wysiwyg.command

import taack.ui.wysiwyg.canvasStyled.ICanvasDrawable
import taack.ui.wysiwyg.canvasStyled.item.CanvasImg

class AddImageCommand(private val drawables: MutableList<ICanvasDrawable>, val i: Int, val image: CanvasImg) : ICanvasCommand {
    override fun doIt(): Boolean {
        drawables.add(i, image)
        return true
    }
}