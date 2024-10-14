package taack.ui.canvas.text

import org.w3c.dom.CanvasRenderingContext2D

abstract class ICanvasText {
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

    var words: List<CanvasWord> = emptyList()
    var txt = ""
    var totalWidth: Double = 0.0

    fun draw(ctx: CanvasRenderingContext2D, canvasWidth: Int) {
        ctx.save()
        ctx.font = font
        ctx.fillStyle = fillStyle

        lastHeight += marginTop

        val numTxt = computeNum()
        val tmpTxt = numTxt + txt
        val txtMetrics = ctx.measureText(numTxt + tmpTxt)
        totalWidth = txtMetrics.width
        val height = lineHeight//txtMetrics.actualBoundingBoxAscent + txtMetrics.actualBoundingBoxDescent

        if (totalWidth > canvasWidth) {
            val listTxt = tmpTxt.split(" ")
            var posX = 10.0
            var posY = height
            for (i in listTxt.indices) {
                val t = listTxt[i] + (if (i < listTxt.size - 1) " " else "")
                if (posX + ctx.measureText(t).width > canvasWidth) {
                    posX = 10.0
                    posY += height
                    totalHeight = posY
                }
                val posXOld = posX
                ctx.fillText(t, posX, posY + lastHeight)
                posX += ctx.measureText(t).width
                words += CanvasWord(t, posXOld, posX, posY, posY + lastHeight)
            }
        } else {
            ctx.fillText(tmpTxt, 10.0, height + lastHeight)
            totalHeight = height
        }
        lastHeight += totalHeight + marginBottom
        ctx.restore()
    }

    abstract fun computeNum():String

    fun drawText(inputText: String, n: Int,  x: Double, y: Double) {
        for (w in words) {
            if (x > w.posX1 && x < w.posX2 && w.posY1 < y && w.posY2 > y) {
                val letterPos = ((x - w.posX1 + w.posX2 - x) / 2 * txt.length).toInt()
                txt = txt.substring(0, letterPos) + inputText + txt.substring(letterPos)
            }
        }
    }
}