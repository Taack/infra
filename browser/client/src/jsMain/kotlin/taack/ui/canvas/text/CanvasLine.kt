package taack.ui.canvas.text

import taack.ui.base.Helper.Companion.trace
import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import web.canvas.CanvasRenderingContext2D


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

    var containedStyles: List<CanvasStyle>? = null

    fun drawLine(ctx: CanvasRenderingContext2D, text: CanvasText, styles: List<CanvasStyle>?) {
        traceIndent("CanvasLine::drawLine: $this")
        if (!styles.isNullOrEmpty()) {
            containedStyles = styles
            var posXStart = 0.0
            for (style in styles) {
                val w = style.draw(ctx, text, this, posXStart)
                posXStart += w
            }
        } else {
            ctx.fillText((if (posBegin == 0) text.txtPrefix else "") + text.txt.substring(posBegin, posEnd), if (text.txtPrefix.isEmpty() || posBegin > 0) leftMargin else 10.0, textY)
        }
        traceDeIndent("CanvasLine::drawLine: $this")
    }

    fun caretNCoords(ctx: CanvasRenderingContext2D, text: CanvasText, x: Double): Int {
        trace("CanvasLine::caretNCoords: $x")
        ctx.save()
        text.initCtx(ctx)

        for (i in posBegin..posEnd) {
            val pos = text.measureText(ctx, posBegin, i) + leftMargin
            if (pos >= x - 10.0) {
                ctx.restore()
                return i
            }
        }
        ctx.restore()

        return text.txt.length + 1
    }

}