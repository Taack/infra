package taack.ui.wysiwyg.canvasMono.command

import taack.ui.base.Helper.Companion.trace
import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.wysiwyg.canvasMono.ICanvasDrawable
import taack.ui.wysiwyg.canvasMono.MainCanvas
import web.uievents.MouseEvent

class DeleteCharMonoCommand(
    val canvas: MainCanvas,
) : ICanvasCommand {
    override fun doIt(): Boolean {
        trace("DeleteCharMonoCommand::doIt")
        canvas.textarea.value = canvas.textarea.value.substring(0, canvas.posInTextarea) + canvas.textarea.value.substring(0, canvas.posInTextarea + 1)
        canvas.posInTextarea--
        return true
    }
}