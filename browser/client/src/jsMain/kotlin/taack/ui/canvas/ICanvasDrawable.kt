package taack.ui.canvas

import taack.ui.canvas.text.CanvasText
import web.canvas.CanvasRenderingContext2D

interface ICanvasDrawable: ICanvasSelectable {

    var globalPosYStart: Double
    var globalPosYEnd: Double
    var citationNumber: Int

    fun isClicked(posX: Double, posY: Double): Boolean {
        return posY in globalPosYStart..globalPosYEnd
    }

    fun drawCitation(ctx: CanvasRenderingContext2D, textY: Double, height: Double): Double {
        ctx.save()
        ctx.fillStyle = "#dadde3"
        for (i in 0 until citationNumber) {
            val marginTop = getSelectedText()!!.marginTop
            val marginBottom = getSelectedText()!!.marginBottom
            ctx.fillRect(8.0 + 16.0 * i, textY - height * 1.2, 4.0, height + marginTop + marginBottom)
        }
        ctx.restore()
        return 16.0 * citationNumber
    }


    fun getSelectedText(posX: Double? = null, posY: Double? = null): CanvasText?

    fun draw(ctx: CanvasRenderingContext2D, width: Double, posY: Double, posX: Double): Double

}