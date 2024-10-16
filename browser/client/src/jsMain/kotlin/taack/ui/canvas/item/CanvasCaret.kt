package taack.ui.canvas.item

import org.w3c.dom.CanvasRenderingContext2D

class CanvasCaret {

    companion object {
        var posX: Double = 0.0
        var posY: Double = 0.0
        private val height: Double = 20.0
        private val width: Double = 5.0

        fun draw(ctx: CanvasRenderingContext2D, moveX: Double) {
            draw(ctx, posX + moveX, posY)
        }

        fun draw(ctx: CanvasRenderingContext2D, x: Double, y: Double) {
            this.posX = x
            this.posY = y
            ctx.save()
            ctx.fillStyle = "green"
            ctx.beginPath()
            ctx.rect(posX, posY - height, width, height)
            ctx.stroke()
            ctx.restore()
        }
    }

}