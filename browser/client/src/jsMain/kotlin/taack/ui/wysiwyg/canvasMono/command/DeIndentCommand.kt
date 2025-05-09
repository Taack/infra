package taack.ui.wysiwyg.canvasMono.command

import taack.ui.wysiwyg.canvasMono.ICanvasDrawable

class DeIndentCommand(private val drawable: ICanvasDrawable) : ICanvasCommand{
    override fun doIt(): Boolean {
        if (drawable.citationNumber > 0) {
            drawable.citationNumber--
            return true
        }
        return false
    }
}