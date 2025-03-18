package taack.ui.wysiwyg.command

import taack.ui.wysiwyg.canvasStyled.ICanvasDrawable

class IndentCommand(private val drawable: ICanvasDrawable) : ICanvasCommand{
    override fun doIt(): Boolean {
        drawable.citationNumber++
        return true
    }
}