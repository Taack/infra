package taack.ui.canvas.command

import taack.ui.canvas.ICanvasDrawable

class DeIndentCommand(private val drawable: ICanvasDrawable) : ICanvasCommand{
    override fun doIt(): Boolean {
        if (drawable.citationNumber > 0) {
            drawable.citationNumber--
            return true
        }
        return false
    }
}