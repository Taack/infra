package taack.ui.canvas.text

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
        if (!styles.isNullOrEmpty()) {
            containedStyles = styles
            var posXStart = 0.0
            for (style in styles) {
                val w = style.draw(ctx, text, this, posXStart)
                posXStart += w
            }
        } else {
            ctx.fillText((if (posBegin == 0) text.txtPrefix else "") + text.txt.substring(posBegin, posEnd), if (posBegin > 0) leftMargin else 10.0, textY)
        }
    }

    fun caretNCoords(ctx: CanvasRenderingContext2D, text: CanvasText, x: Double): Int {
        ctx.save()
        text.initCtx(ctx)

        for (i in posBegin..posEnd) {
            val pos = text.measureText(ctx, posBegin, i) + leftMargin
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