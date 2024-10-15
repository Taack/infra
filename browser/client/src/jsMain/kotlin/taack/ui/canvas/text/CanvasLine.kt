package taack.ui.canvas.text

import org.w3c.dom.CanvasRenderingContext2D

class CanvasLine(val posBegin: Int, val posEnd: Int, val posY1: Double, val posY2: Double, val leftMargin: Double = 0.0) {
    override fun toString(): String {
        return "CanvasLine(posBegin=$posBegin, posEnd=$posEnd, posY1=$posY1, posY2=$posY2, leftMargin=$leftMargin)"
    }

    fun drawLine(ctx: CanvasRenderingContext2D, globalHeight: Double) {
        ctx.save()
        ctx.beginPath()
        ctx.strokeStyle = "blue"
        ctx.moveTo(0.0, posY1 + globalHeight)
        ctx.lineTo(100.0, posY1 +globalHeight)
        ctx.fillStyle = "lightblue"
        ctx.fillText("$posBegin", 100.0, posY1 +globalHeight)
        ctx.stroke()
        ctx.restore()
        ctx.save()
        ctx.beginPath()
        ctx.strokeStyle = "red"
        ctx.moveTo(100.0, posY2 + globalHeight)
        ctx.lineTo(200.0, posY2 + globalHeight)
        ctx.fillStyle = "green"
        ctx.fillText("$posEnd", 100.0, posY2 +globalHeight)
        ctx.stroke()
        ctx.restore()
    }
}