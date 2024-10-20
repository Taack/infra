package taack.ui.canvas.item

import org.w3c.dom.CanvasRenderingContext2D
import taack.ui.canvas.text.CanvasLine
import taack.ui.canvas.text.CanvasText

class CanvasCaret {

    companion object {
        var posX: Double = 0.0
        var posY: Double = 0.0
        private val height: Double = 20.0
        private val width: Double = 5.0

        fun draw(ctx: CanvasRenderingContext2D, text: CanvasText, line: CanvasLine, n: Int) {
            posY = line.textY
            ctx.save()
            ctx.font = text.font
            posX = ctx.measureText(text.txt.substring(line.posBegin, line.posBegin + n)).width + line.leftMargin
            ctx.restore()
            draw(ctx, posX, posY)
        }

        fun drawDblClick(ctx: CanvasRenderingContext2D, text: CanvasText, line: CanvasLine, n: Int, posNStart: Int, posNEnd: Int) {
            posY = line.textY
            ctx.save()
            ctx.font = text.font
            var i = text.findLine(line)
            var isFirstLine = true
            println("text.lines: ${text.lines}")
            var cLine: CanvasLine? = null
            posX = ctx.measureText(text.txt.substring(line.posBegin, line.posBegin + n)).width + line.leftMargin
            do {
                if (i >= text.lines.size) break
                cLine = text.lines[i]

                val posXStart = if (isFirstLine) ctx.measureText(text.txt.substring(cLine.posBegin, posNStart)).width + cLine.leftMargin else cLine.leftMargin
                val posXEnd = ctx.measureText(text.txt.substring(cLine.posBegin, posNEnd)).width + cLine.leftMargin
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