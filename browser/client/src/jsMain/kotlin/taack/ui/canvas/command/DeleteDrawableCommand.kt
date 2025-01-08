package taack.ui.canvas.command

import taack.ui.canvas.ICanvasDrawable

class DeleteDrawableCommand(private val drawables: MutableList<ICanvasDrawable>, private val textIndex: Int) : ICanvasCommand {
    override fun doIt(): Boolean {
        if (textIndex == 0 && drawables.size <= 1) return false
        drawables.removeAt(textIndex)
        return true
    }
}