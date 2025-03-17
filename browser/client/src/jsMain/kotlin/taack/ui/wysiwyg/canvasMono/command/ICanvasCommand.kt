package taack.ui.wysiwyg.canvasMono.command

import taack.ui.base.Helper.Companion.trace
import taack.ui.wysiwyg.canvasMono.ICanvasDrawable
import taack.ui.wysiwyg.canvasMono.text.CanvasText
import web.uievents.MouseEvent

interface ICanvasCommand {
    fun doIt(): Boolean
    fun embeddedText(drawables: MutableList<ICanvasDrawable>, textIndex: Int, textSubIndex: Int, mouseEvent: MouseEvent?): CanvasText? {
        trace("embeddedText textIndex: $textIndex, textSubIndex: $textSubIndex, mouseEvent: $mouseEvent")
        val currentDrawable = drawables[textIndex]
        return currentDrawable.getSelectedText(mouseEvent?.offsetX, mouseEvent?.offsetY)
    }
}