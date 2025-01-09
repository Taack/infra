package taack.ui.canvas.command

import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.canvas.ICanvasDrawable

class AddDrawableCommand(private val drawables: MutableList<ICanvasDrawable>, val i: Int, val text: ICanvasDrawable) : ICanvasCommand {
    override fun doIt(): Boolean {
        traceIndent("AddDrawableCommand +++ $text $i")
        if (i == -1) drawables.add(text)
        else drawables.add(i, text)
        traceDeIndent("AddDrawableCommand --- ${drawables.size}")
        return true
    }
}