package taack.ui.wysiwyg.canvasMono.command

import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.wysiwyg.canvasMono.ICanvasDrawable

class DeleteDrawableCommand(private val drawables: MutableList<ICanvasDrawable>, private val textIndex: Int) : ICanvasCommand {
    override fun doIt(): Boolean {
        traceIndent("DeleteDrawableCommand +++ ${textIndex}")
        if (textIndex == 0 && drawables.size <= 1) return false
        drawables.removeAt(textIndex)
        traceDeIndent("DeleteDrawableCommand --- ${drawables.size}")
        return true
    }
}