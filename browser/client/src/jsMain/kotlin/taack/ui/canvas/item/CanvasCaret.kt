package taack.ui.canvas.item

import taack.ui.base.Helper.Companion.trace
import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.canvas.text.CanvasLine
import taack.ui.canvas.text.CanvasText
import web.canvas.CanvasRenderingContext2D

class CanvasCaret {

    companion object {
        private var posX: Double = 0.0
        private var posY: Double = 0.0
        private const val HEIGHT: Double = 20.0
        private const val WIDTH: Double = 5.0

        fun draw(ctx: CanvasRenderingContext2D, text: CanvasText, line: CanvasLine, n: Int) {
            traceIndent("CanvasCaret::draw: $n line.posEnd: ${line.posEnd}")
            posY = line.textY
            ctx.save()
            text.initCtx(ctx)
//            posX = text.measureText(ctx, line.posBegin, min(line.posBegin + n, line.posEnd)) + line.leftMargin
            posX = text.measureText(ctx, line.posBegin,line.posBegin + n) + line.leftMargin + text.posXStart
            ctx.restore()
            draw(ctx, posX, posY)
            traceDeIndent("CanvasCaret::draw: $n")
        }

        fun drawDblClick(ctx: CanvasRenderingContext2D, text: CanvasText, line: CanvasLine, n: Int, posNStart: Int, posNEnd: Int) {
            traceIndent("CanvasCaret::drawDblClick: $n, $posNStart, $posNEnd")
            posY = line.textY
            ctx.save()
            text.initCtx(ctx)
            var i = if (posNStart == 0) 0 else text.findLine(line)
            var isFirstLine = true
            var cLine: CanvasLine?
            posX = text.measureText(ctx, line.posBegin, line.posBegin + n) + line.leftMargin + text.posXStart
            do {
                if (i >= text.lines.size) break
                cLine = text.lines[i]

                val posXStart = if (isFirstLine) text.measureText(ctx, cLine.posBegin, posNStart) + cLine.leftMargin else cLine.leftMargin
                val posXEnd = text.measureText(ctx, cLine.posBegin, posNEnd) + cLine.leftMargin
                draw(ctx, posXStart, cLine.textY, posXEnd)
                i += 1
                isFirstLine = false
            } while (posNEnd >= cLine!!.posEnd)
            ctx.restore()
            traceDeIndent("CanvasCaret::drawDblClick: $n, $posNStart, $posNEnd")
        }

        private fun draw(ctx: CanvasRenderingContext2D, x: Double, y: Double, posXEnd: Double? = null) {
            trace("CanvasCaret::draw: $x, $y, $posXEnd")
            this.posX = x
            this.posY = y
            ctx.save()
            ctx.strokeStyle = if (posXEnd == null) "green" else "blue"
            ctx.beginPath()
            ctx.rect(posX - if (posXEnd != null) 0.0 else WIDTH, posY - HEIGHT, if (posXEnd != null) posXEnd - posX else WIDTH, HEIGHT)
            ctx.stroke()
            ctx.restore()
        }
    }

}