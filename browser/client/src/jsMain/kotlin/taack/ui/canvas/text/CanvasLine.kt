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

    var containedStyles: List<CanvasStyle>? = null

    fun drawCitation(ctx: CanvasRenderingContext2D, text: CanvasText, height: Double): Double {
        ctx.save()
        ctx.fillStyle = "#dadde3"
        for (i in 0 until text.citationNumber) {
            ctx.fillRect(4.0 + 8.0 * i, textY - height, 2.0, height * 1.5 + text.marginTop + text.marginBottom)
        }
        ctx.restore()
        return 8.0 * text.citationNumber
    }

    fun drawLine(ctx: CanvasRenderingContext2D, text: CanvasText, styles: List<CanvasStyle>?) {
        traceIndent("CanvasLine::drawLine: $this")
        if (!styles.isNullOrEmpty()) {
            containedStyles = styles
            var posXStart = drawCitation(ctx, text, height)
            for (style in styles) {
                val w = style.draw(ctx, text, this, posXStart)
                posXStart += w
            }
        } else {
            val posXStart = drawCitation(ctx, text, height)
            ctx.fillText((if (posBegin == 0) text.txtPrefix else "") + text.txt.substring(posBegin, posEnd), (if (text.txtPrefix.isEmpty() || posBegin > 0) leftMargin else 10.0) + posXStart, textY)
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

    override fun toString(): String {
        return "CanvasLine(posBegin=$posBegin, posEnd=$posEnd, textY=$textY, height=$height, leftMargin=$leftMargin)"
    }

}