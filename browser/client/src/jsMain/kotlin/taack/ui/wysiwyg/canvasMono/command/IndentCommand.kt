package taack.ui.wysiwyg.canvasMono.command

import taack.ui.wysiwyg.canvasMono.ICanvasDrawable

class IndentCommand(private val drawable: ICanvasDrawable) : ICanvasCommand{
    override fun doIt(): Boolean {
        drawable.citationNumber++
        return true
    }
}