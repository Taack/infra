package taack.ui.canvas.command

import taack.ui.canvas.ICanvasDrawable

class IndentCommand(val drawable: ICanvasDrawable) : ICanvasCommand{
    override fun doIt() {
        drawable.citationNumber++
    }
}