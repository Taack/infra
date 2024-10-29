package taack.ui.canvas.command

import taack.ui.canvas.ICanvasDrawable

class DeIndentCommand(val drawable: ICanvasDrawable) : ICanvasCommand{
    override fun doIt() {
        if (drawable.citationNumber > 0)
            drawable.citationNumber--
    }
}