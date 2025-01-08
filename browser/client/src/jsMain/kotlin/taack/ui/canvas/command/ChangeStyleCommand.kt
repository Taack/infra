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
        if (currentDrawableIndex == 0 && drawables.size <= 1) return false
        drawables.removeAt(currentDrawableIndex)
        drawables.add(currentDrawableIndex, text)
        return true
    }
}