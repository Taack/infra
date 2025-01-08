package taack.ui.canvas.command

import taack.ui.canvas.ICanvasDrawable

class DeleteDrawableCommand(private val drawables: MutableList<ICanvasDrawable>, val text: ICanvasDrawable) : ICanvasCommand {
    override fun doIt(): Boolean {
        drawables.remove(text)
        return true
    }
}