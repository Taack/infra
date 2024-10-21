package taack.ui.canvas.text

import web.canvas.CanvasRenderingContext2D
import kotlin.math.max
import kotlin.math.min


class CanvasStyle(val type: Type, var posNStart: Int, var posNEnd: Int) {
    enum class Type {
        NORMAL,
        BOLD,
        MONOSPACED,
        BOLD_MONOSPACED,
    }

    var posXStart: Double = 0.0

    fun initCtx(ctx: CanvasRenderingContext2D, text: CanvasText) {
        text.initCtx(ctx)
        ctx.font = when(this.type) {
            Type.NORMAL -> {
                text.font()
            }
            Type.BOLD -> {
                "bold ${text.fontSize} ${text.fontFace}"
            }
            Type.MONOSPACED -> {
                "${text.fontWeight} ${text.fontSize} monospace"
            }
            Type.BOLD_MONOSPACED -> {
                "bold ${text.fontSize} monospace"
            }
        }
    }

    fun draw(ctx: CanvasRenderingContext2D, text: CanvasText, line: CanvasLine, posXStart: Double): Double {
        ctx.save()
        initCtx(ctx, text)
        val txt = text.txt.substring(max(posNStart, line.posBegin), min(posNEnd, line.posEnd))
        ctx.fillText((if (posNStart == 0) text.txtPrefix else "") + txt, (if (posNStart > 0) line.leftMargin else 10.0) + posXStart, line.textY)
        this.posXStart = posXStart
        val width = text.measureText(ctx,  max(posNStart, line.posBegin), min(posNEnd, line.posEnd))
        console.log("CanvasStyle::draw width: $width from: ${max(posNStart, line.posBegin)}, to: ${min(posNEnd, line.posEnd)}")
        ctx.restore()
        return width
    }

    override fun toString(): String {
        return "CanvasStyle(type=$type, posNStart=$posNStart, posNEnd=$posNEnd, posXStart=$posXStart)"
    }
}