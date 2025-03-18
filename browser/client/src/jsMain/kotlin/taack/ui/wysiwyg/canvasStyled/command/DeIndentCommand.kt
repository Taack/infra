package taack.ui.wysiwyg.command

import taack.ui.wysiwyg.canvasStyled.ICanvasDrawable

class DeIndentCommand(private val drawable: ICanvasDrawable) : ICanvasCommand{
    override fun doIt(): Boolean {
        if (drawable.citationNumber > 0) {
            drawable.citationNumber--
            return true
        }
        return false
    }
}