package taack.ui.canvas.item

import taack.ui.canvas.text.CanvasLine
import taack.ui.canvas.text.CanvasText
import web.canvas.CanvasRenderingContext2D

class CanvasCaret {

    companion object {
        var posX: Double = 0.0
        var posY: Double = 0.0
        private val height: Double = 20.0
        private val width: Double = 5.0

        fun draw(ctx: CanvasRenderingContext2D, text: CanvasText, line: CanvasLine, n: Int) {
            posY = line.textY
            ctx.save()
            text.initCtx(ctx)
            posX = text.measureText(ctx, line.posBegin, line.posBegin + n) + line.leftMargin
            ctx.restore()
            draw(ctx, posX, posY)
        }

        fun drawDblClick(ctx: CanvasRenderingContext2D, text: CanvasText, line: CanvasLine, n: Int, posNStart: Int, posNEnd: Int) {
            posY = line.textY
            ctx.save()
            text.initCtx(ctx)
            var i = if (posNStart == 0) 0 else text.findLine(line)
            var isFirstLine = true
            println("text.lines: ${text.lines}")
            var cLine: CanvasLine? = null
            posX = text.measureText(ctx, line.posBegin, line.posBegin + n) + line.leftMargin
            do {
                if (i >= text.lines.size) break
                cLine = text.lines[i]

                val posXStart = if (isFirstLine) text.measureText(ctx, cLine.posBegin, posNStart) + cLine.leftMargin else cLine.leftMargin
                val posXEnd = text.measureText(ctx, cLine.posBegin, posNEnd) + cLine.leftMargin
                draw(ctx, posXStart, cLine.textY, posXEnd)
                console.log("i: $i posNStart: $posNStart posNEnd: $posNEnd posXEnd: $posXEnd cLine: $cLine, txt: ${text.txt.substring(cLine.posBegin, posNEnd)}")
                i += 1
                isFirstLine = false
            } while (posNEnd >= cLine!!.posEnd)
            ctx.restore()
        }

        fun draw(ctx: CanvasRenderingContext2D, x: Double, y: Double, posXEnd: Double? = null) {
            this.posX = x
            this.posY = y
            ctx.save()
            ctx.strokeStyle = if (posXEnd == null) "green" else "blue"
            ctx.beginPath()
            ctx.rect(posX - if (posXEnd != null) 0.0 else width, posY - height, if (posXEnd != null) posXEnd - posX else width, height)
            ctx.stroke()
            ctx.restore()
        }
    }

}