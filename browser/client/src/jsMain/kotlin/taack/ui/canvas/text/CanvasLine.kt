package taack.ui.canvas.text

import org.w3c.dom.CanvasRenderingContext2D
import taack.ui.canvas.text.CanvasText.Companion.globalPosY

class CanvasLine(
    val posBegin: Int,
    val posEnd: Int,
    val textY: Double,
    val height: Double,
    val leftMargin: Double = 0.0
) {
    override fun toString(): String {
        return "CanvasLine(posBegin=$posBegin, posEnd=$posEnd, textY=$textY, height=$height, leftMargin=$leftMargin)"
    }

    fun drawLine(ctx: CanvasRenderingContext2D) {
        ctx.save()
        ctx.beginPath()
        ctx.strokeStyle = "blue"
        ctx.moveTo(0.0, textY - height)
        ctx.lineTo(100.0, textY - height)
        ctx.fillStyle = "violet"
        ctx.fillText("+++ $posBegin to $posEnd", 100.0, textY)
        ctx.stroke()
        ctx.restore()
        ctx.save()
        ctx.beginPath()
        ctx.strokeStyle = "red"
        ctx.moveTo(100.0, textY)
        ctx.lineTo(200.0, textY)
        ctx.fillStyle = "green"
        ctx.fillText("--- $posBegin to $posEnd", 500.0, textY)
        ctx.stroke()
        ctx.restore()
    }

    fun drawLine(ctx: CanvasRenderingContext2D, text: CanvasText) {
//        ctx.fillText((if (posBegin == 0) text.txtPrefix else "") + text.txt.substring(posBegin, posEnd), 10.0, textY)
        ctx.fillText((if (posBegin == 0) text.txtPrefix else "") + text.txt.substring(posBegin, posEnd), if (posBegin > 0) leftMargin else 10.0, textY)
    }

//    fun caretXCoords(ctx: CanvasRenderingContext2D, text: CanvasText, x: Double): Double {
//        val cp = caretNCoords(ctx, text, x) + if (posBegin == 0) text.txtPrefix.length else 0
//        val txt = (if (posBegin == 0) text.txtPrefix else "") + text.txt.substring(posBegin, cp) + if (cp > text.txt.length) " " else ""
//        ctx.save()
//        ctx.font = text.font
//        ctx.fillStyle = text.fillStyle
//        val txtWidth = ctx.measureText(txt).width
//        ctx.restore()
//        println("txt: <$txt> width: $txtWidth")
//        return txtWidth
//    }
//
    fun caretNCoords(ctx: CanvasRenderingContext2D, text: CanvasText, x: Double): Int {
        ctx.save()
        ctx.font = text.font
        ctx.fillStyle = text.fillStyle

        for (i in posBegin..posEnd) {
//            val pos = ctx.measureText((if (posBegin == 0) text.txtPrefix else "") + text.txt.substring(posBegin, i)).width
            val pos = ctx.measureText(text.txt.substring(posBegin, i)).width + leftMargin
            if (pos >= x - 10.0) {
                println("i: $i pos: $pos; x: $x")
                ctx.restore()
                return i
            }
        }
        ctx.restore()

        return text.txt.length + 1
    }


}