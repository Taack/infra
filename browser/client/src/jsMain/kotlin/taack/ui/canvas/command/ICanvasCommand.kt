package taack.ui.canvas.command

import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.MouseEvent
import taack.ui.canvas.text.ICanvasCharSequence

interface ICanvasCommand {
    fun doIt(textContainer: ICanvasCharSequence, keyboardEvent: KeyboardEvent)
    fun doIt(textContainer: ICanvasCharSequence, mouseEvent: MouseEvent)
    fun undoIt()
}