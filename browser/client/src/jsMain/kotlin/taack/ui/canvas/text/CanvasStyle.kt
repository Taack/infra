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
        val from = max(posNStart, line.posBegin)
        val to = min(posNEnd, line.posEnd)
        if (from < to) {
            initCtx(ctx, text)
            val txt = text.txt.substring(from, to)
            ctx.fillText(txt, line.leftMargin + posXStart, line.textY)
            val width = text.measureText(ctx,  from, to)
            ctx.restore()
            traceDeIndent("CanvasStyle::draw: $this")
            return width
        }
        traceDeIndent("CanvasStyle::draw: $this")
        return 0.0
    }

    override fun toString(): String {
        return "CanvasStyle(type=$type, posNStart=$posNStart, posNEnd=$posNEnd)"
    }

    fun dumpAsciidoc(text: CanvasText): String {
        val str = text.txt.substring(posNStart, posNEnd)
        return when(type) {
            Type.NORMAL -> str
            Type.BOLD -> "*$str*"
            Type.MONOSPACED -> "`$str`"
            Type.BOLD_MONOSPACED -> "**$str**"
        }
    }
}