package taack.ui.canvas.text

import taack.ui.base.Helper.Companion.trace
import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
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

    fun initCtx(ctx: CanvasRenderingContext2D, text: CanvasText) {
        trace("CanvasStyle::initCtx: $this")
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
        traceIndent("CanvasStyle::draw: $this")
        if (posNStart == 0) ctx.fillText(text.txtPrefix, text.posXStart, line.textY)
        ctx.save()
        initCtx(ctx, text)
        val txt = text.txt.substring(max(posNStart, line.posBegin), min(posNEnd, line.posEnd))
        ctx.fillText(txt, line.leftMargin + posXStart, line.textY)
        val width = text.measureText(ctx,  max(posNStart, line.posBegin), min(posNEnd, line.posEnd))
        console.log("CanvasStyle::draw width: $width from: ${max(posNStart, line.posBegin)}, to: ${min(posNEnd, line.posEnd)}")
        ctx.restore()
        traceDeIndent("CanvasStyle::draw: $this")
        return width
    }

    override fun toString(): String {
        return "CanvasStyle(type=$type, posNStart=$posNStart, posNEnd=$posNEnd)"
    }
}