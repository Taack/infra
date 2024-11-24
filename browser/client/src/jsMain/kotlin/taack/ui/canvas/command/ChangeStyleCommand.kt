package taack.ui.canvas.command

import taack.ui.base.Helper.Companion.trace
import taack.ui.canvas.ICanvasDrawable
import taack.ui.canvas.MainCanvas
import taack.ui.canvas.text.CanvasText

class ChangeStyleCommand(
    private val drawables: MainCanvas.MyMutableList,
    private val initialDrawables: MutableList<ICanvasDrawable>,
    private val currentDrawable: ICanvasDrawable?,
    val text: CanvasText
) : ICanvasCommand {
    override fun doIt() {
        trace("ChangeStyleCommand")
        if (currentDrawable == null) return
        val i = drawables.indexOf(currentDrawable)
        if (i == -1) return
        drawables.removeAt(i)
        drawables.add(i, text)
    }
}