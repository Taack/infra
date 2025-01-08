package taack.ui.canvas.command

import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.canvas.ICanvasDrawable
import taack.ui.canvas.text.CanvasText

class DeleteCharCommand(
    private val drawables: MutableList<ICanvasDrawable>,
    val text: CanvasText,
    private val pos1: Int,
    private val pos2: Int?
) : ICanvasCommand {
    override fun doIt(): Boolean {
        traceIndent("DeleteCharCommand +++ ${text.txt}")
        var ret = true
        if (text.delChar(pos1, pos2) == 0) {
            ret = drawables.remove(text)
        }
        traceDeIndent("DeleteCharCommand --- ${text.txt}")
        return ret
    }
}