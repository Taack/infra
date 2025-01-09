package taack.ui.canvas.command

import taack.ui.base.Helper.Companion.trace
import taack.ui.canvas.ICanvasDrawable
import taack.ui.canvas.MainCanvas
import taack.ui.canvas.text.CanvasText

class ChangeStyleCommand(
    private val drawables: MainCanvas.MyMutableList,
    private val currentDrawableIndex: Int,
    val text: CanvasText
) : ICanvasCommand {
    override fun doIt(): Boolean {
        trace("ChangeStyleCommand")
        drawables.add(currentDrawableIndex, text)
        drawables.removeAt(currentDrawableIndex + 1)
        return true
    }
}