package taack.ui.canvas.command

import taack.ui.base.Helper.Companion.trace
import taack.ui.canvas.ICanvasDrawable
import taack.ui.canvas.table.CanvasTable
import taack.ui.canvas.text.CanvasText
import web.uievents.MouseEvent

interface ICanvasCommand {
    fun doIt(): Boolean
    fun embeddedText(drawables: MutableList<ICanvasDrawable>, textIndex: Int, textSubIndex: Int, mouseEvent: MouseEvent?): CanvasText? {
        trace("embeddedText textIndex: $textIndex, textSubIndex: $textSubIndex, mouseEvent: $mouseEvent")
        val currentDrawable = drawables[textIndex]
        if (currentDrawable is CanvasTable) {
            currentDrawable.currentRowIndex = textSubIndex
        }
        return currentDrawable.getSelectedText(mouseEvent?.offsetX, mouseEvent?.offsetY)
    }
}