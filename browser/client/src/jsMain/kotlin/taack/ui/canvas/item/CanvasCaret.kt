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

        fun draw(ctx: CanvasRenderingContext2D, x: Double, y: Double) {
            this.posX = x
            this.posY = y
            ctx.save()
            ctx.fillStyle = "green"
            ctx.beginPath()
            ctx.rect(posX - width, posY - height, width, height)
            ctx.stroke()
            ctx.restore()
        }
    }

}