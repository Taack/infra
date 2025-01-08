package taack.ui.canvas.command

import taack.ui.canvas.ICanvasDrawable

class AddDrawableCommand(private val drawables: MutableList<ICanvasDrawable>, val i: Int, val text: ICanvasDrawable) : ICanvasCommand {
    override fun doIt(): Boolean {
        if (i == -1) drawables.add(text)
        else drawables.add(i, text)
        return true
    }
}