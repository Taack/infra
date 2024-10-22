package taack.ui.canvas

import taack.ui.canvas.item.MenuEntry
import taack.ui.canvas.text.CanvasText
import web.canvas.CanvasRenderingContext2D

interface ICanvasDrawable: ICanvasSelectable {

    var globalPosYStart: Double
    var globalPosYEnd: Double

    fun isClicked(posX: Double, posY: Double): Boolean {
        return posY in globalPosYStart..globalPosYEnd
    }

    fun getSelectedText(posX: Double, posY: Double): CanvasText?

    fun draw(ctx: CanvasRenderingContext2D, width: Int, posY: Double): Double

}