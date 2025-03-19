package taack.ui.wysiwyg.canvasMono.command

import taack.ui.base.Helper.Companion.trace
import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.wysiwyg.canvasMono.ICanvasDrawable
import taack.ui.wysiwyg.canvasMono.MainCanvas
import web.uievents.MouseEvent

class DeleteCharMonoCommand(
    private val canvas: MainCanvas,
    private val posInTextarea: Int
) : ICanvasCommand {
    override fun doIt(): Boolean {
        trace("DeleteCharMonoCommand::doIt $posInTextarea")
        canvas.textarea.value = canvas.textarea.value.substring(0, posInTextarea - 1) + canvas.textarea.value.substring(posInTextarea)
        return true
    }
}