package taack.ui.wysiwyg.canvasMono.command

import taack.ui.base.Helper.Companion.trace
import taack.ui.wysiwyg.canvasMono.MainCanvas

class AddCharMonoCommand(private val canvas: MainCanvas, private val ch: String) :
    ICanvasCommand {
    override fun doIt(): Boolean {
        trace("AddCharMonoCommand $ch")
        canvas.textarea.value =
            canvas.textarea.value.substring(0, canvas.posInTextarea) + ch + canvas.textarea.value.substring(canvas.posInTextarea)
        canvas.posInTextarea += ch.length
        return true
    }
}