package taack.ui.canvas.command

import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.MouseEvent
import taack.ui.canvas.text.ICanvasText

interface ICanvasCommand {
    fun doIt(textContainer: ICanvasText, keyboardEvent: KeyboardEvent)
    fun doIt(textContainer: ICanvasText, mouseEvent: MouseEvent)
    fun undoIt()
}