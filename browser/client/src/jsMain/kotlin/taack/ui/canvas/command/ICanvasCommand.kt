package taack.ui.canvas.command

import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.MouseEvent
import taack.ui.canvas.text.CanvasText

interface ICanvasCommand {
    fun doIt(textContainer: CanvasText, keyboardEvent: KeyboardEvent)
    fun doIt(textContainer: CanvasText, mouseEvent: MouseEvent)
    fun undoIt()
}