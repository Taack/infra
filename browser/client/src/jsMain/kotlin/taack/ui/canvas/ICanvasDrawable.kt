package taack.ui.canvas

import taack.ui.canvas.text.CanvasText
import web.canvas.CanvasRenderingContext2D

interface ICanvasDrawable: ICanvasSelectable {

    var globalPosYStart: Double
    var globalPosYEnd: Double

    fun isClicked(posX: Double, posY: Double): Boolean {
        return posY in globalPosYStart..globalPosYEnd
    }

    fun getSelectedText(posX: Double? = null, posY: Double? = null): CanvasText?

    fun draw(ctx: CanvasRenderingContext2D, width: Double, posY: Double, posX: Double): Double

}