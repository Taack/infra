package taack.ui.wysiwyg.canvasMono.command

import taack.ui.base.Helper.Companion.trace
import taack.ui.wysiwyg.canvasMono.MainCanvas

class AddCharMonoCommand(private val canvas: MainCanvas, private val posInTextArea: Int, private val ch: String) :
    ICanvasCommand {
    override fun doIt(): Boolean {
        trace("AddCharMonoCommand $ch posInTextarea: ${posInTextArea} caretPosInCurrentText: ${canvas.caretPosInCurrentText}")
        canvas.textarea.value =
            canvas.textarea.value.substring(0, posInTextArea) + ch + canvas.textarea.value.substring(posInTextArea)
        return true
    }
}