package taack.ui.wysiwyg.command

import taack.ui.base.Helper.Companion.trace
import taack.ui.wysiwyg.canvasStyled.MainCanvas
import taack.ui.wysiwyg.canvasStyled.text.CanvasText

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