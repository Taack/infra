package taack.ui.canvas.text

import org.w3c.dom.CanvasRenderingContext2D

abstract class ICanvasCharSequence {
    companion object {
        var lastHeight: Double = 0.0
        var num1: Int = 0
        var num2: Int = 0
    }

    abstract val font: String
    abstract val fillStyle: String
    abstract val letterSpacing: Double
    abstract val lineHeight: Double
    abstract val wordSpacing: Double
    var totalHeight: Double = 0.0
    abstract val marginTop: Double
    abstract val marginBottom: Double

    var txt: String = ""
    var totalWidth: Double = 0.0

    fun draw(ctx: CanvasRenderingContext2D, canvasWidth: Int) {
        ctx.save()
        ctx.font = font
        ctx.fillStyle = fillStyle

        lastHeight += marginTop

        txt = "${computeNum()} $txt"
        val txtMetrics = ctx.measureText(txt)
        totalWidth = txtMetrics.width
        val height = lineHeight//txtMetrics.actualBoundingBoxAscent + txtMetrics.actualBoundingBoxDescent

        if (totalWidth!! > canvasWidth) {
            val listTxt = txt.split(" ")
            var posX = 10.0
            var posY = height
            for (i in 0 .. (listTxt.size - 1)) {
                val t = listTxt[i] + (if (i < listTxt.size - 1) " " else "")
                if (posX + ctx.measureText(t).width > canvasWidth) {
                    posX = 10.0
                    posY += height
                    totalHeight = posY
                }
                ctx.fillText(t, posX, posY + lastHeight)
                posX += ctx.measureText(t).width
            }
        } else {
            ctx.fillText(txt, 10.0, height + lastHeight)
            totalHeight = height
        }
        lastHeight += totalHeight + marginBottom
        ctx.restore()
    }

    abstract fun computeNum():String
}