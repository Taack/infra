package taack.ui.canvas

import web.canvas.CanvasRenderingContext2D

interface ICanvasDrawable {

    fun draw(canvas: CanvasRenderingContext2D, width: Int, posY: Double): Double
}