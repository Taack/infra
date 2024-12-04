package taack.ui.canvas.text

import taack.ui.base.Helper.Companion.trace
import web.canvas.CanvasRenderingContext2D


class CanvasLine(
    val posBegin: Int,
    val posEnd: Int,
    val textY: Double,
    val height: Double,
    val leftMargin: Double = 0.0
) {

    val length: Int
        get() = posEnd - posBegin

    fun drawLine(ctx: CanvasRenderingContext2D, text: CanvasText) {
//        trace("CanvasLine::drawLine: $this")
        var posXStart = text.posXStart
        text.drawCitation(ctx, textY, height)

// TODO: Styles
            ctx.fillText(
                (if (posBegin == 0) text.txtPrefix else "") + text.txt.substring(posBegin, posEnd),
                (if (text.txtPrefix.isEmpty() || posBegin > 0) leftMargin else 0.0) + posXStart,
                textY
            )
    }

    fun caretNCoords(ctx: CanvasRenderingContext2D, text: CanvasText, x: Double): Int {
        ctx.save()
        text.initCtx(ctx)

        for (i in posBegin..posEnd) {
            val pos = text.measureText(ctx, posBegin, i) + leftMargin + text.posXStart
            if (pos >= x) {
                ctx.restore()
                trace("CanvasLine::caretNCoords: $x, ret ${i - 1}")
                return i - 1
            }
        }
        ctx.restore()

        trace("CanvasLine::caretNCoords: $x, ret txt.length = ${text.txt.length}")
        return text.txt.length
    }

    override fun toString(): String {
        return "CanvasLine(posBegin=$posBegin, posEnd=$posEnd, textY=$textY, height=$height, leftMargin=$leftMargin)"
    }

}