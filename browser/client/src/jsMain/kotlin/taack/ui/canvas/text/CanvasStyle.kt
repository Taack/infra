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

    fun draw(ctx: CanvasRenderingContext2D, text: CanvasText, line: CanvasLine) {
        val weight: String = when(this.type) {
            Type.NORMAL -> {
                ""
            }
            Type.BOLD -> {
                "bold "
            }
            Type.MONOSPACED -> {
                "monospace "
            }
            Type.BOLD_MONOSPACED -> {
                "bold monospace "
            }
        }
        ctx.save()
        val xStart = ctx.measureText(text.txt.substring(line.posBegin, posNStart)).width
        ctx.font = weight + text.font
        console.log("xStart: $xStart")
        ctx.fillText((if (posNStart == 0) text.txtPrefix else "") + text.txt.substring(max(posNStart, line.posBegin), min(posNEnd, line.posEnd)), (if (posNStart > 0) line.leftMargin else 10.0) + xStart, line.textY)
        ctx.restore()
    }

    override fun toString(): String {
        return "CanvasStyle(type=$type, posNStart=$posNStart, posNEnd=$posNEnd)"
    }
}