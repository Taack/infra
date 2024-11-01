package taack.ui.canvas

import taack.ui.canvas.text.CanvasLine
import web.canvas.CanvasRenderingContext2D

interface ICanvasSelectable {
    fun click(ctx: CanvasRenderingContext2D, posX: Double, posY: Double): Pair<CanvasLine, Int>?
    fun doubleClick(ctx: CanvasRenderingContext2D, posX: Double, posY: Double): Triple<CanvasLine, Int, Int>?
}