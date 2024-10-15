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

    var letters: List<CanvasLetter> = emptyList()
    var lines: List<CanvasLine> = emptyList()
    var txt = ""
    var totalWidth: Double = 0.0

    fun draw(ctx: CanvasRenderingContext2D, canvasWidth: Int) {
        ctx.save()
        ctx.font = font
        ctx.fillStyle = fillStyle
        lastHeight += marginTop

        val numTxt = computeNum()
        val tmpTxt = numTxt + txt
        val txtMetrics = ctx.measureText(tmpTxt)
        totalWidth = txtMetrics.width
        val height = txtMetrics.actualBoundingBoxAscent + txtMetrics.actualBoundingBoxDescent//lineHeight

        val listTxt = tmpTxt.split(" ")
        var posX = 10.0
        var posY = height
        totalHeight = posY
        var txtLetterPos = 0
        var txtLetterPosBegin = 0
        var txtLetterPosEnd = 0
        for (i in listTxt.indices) {
            val t = listTxt[i] + (if (i < listTxt.size - 1) " " else "")
            if (posX + ctx.measureText(t).width > canvasWidth) {
                txtLetterPosEnd = txtLetterPos
                posX = 10.0
                posY += height
                totalHeight = posY
                lines += CanvasLine(txtLetterPosBegin, txtLetterPosEnd, posY - height - marginTop, posY + marginBottom)
                txtLetterPosBegin = txtLetterPosEnd
            }
//            val posXOld = posX
            ctx.fillText(t, posX, posY + lastHeight)
            posX += ctx.measureText(t).width
//            if (numTxt.isEmpty() || (numTxt.isNotEmpty() && i > 0)) {
//                var j = 0
//                var posXLetter = posXOld
//                for (letter in t) {
//                    val letterX = ctx.measureText(letter.toString()).width
//                    letters += CanvasLetter(txtLetterPos + j++, posXLetter, posXLetter + letterX)
//                    posXLetter += letterX
//                }
//            }
            txtLetterPos += t.length
        }
        lastHeight += totalHeight + marginBottom
        ctx.restore()
    }

    abstract fun computeNum(): String

    fun drawText(ctx: CanvasRenderingContext2D, inputText: String, n: Int, x: Double, y: Double) {
        for (l in lines) {
            if (l.posY1 <= y && l.posY2 >= y) {
                val t = txt.substring(l.posBegin, l.posEnd)
                for (e in t.indices) {
                    val eX = ctx.measureText(t.substring(e)).width
                    if (eX <= x) {
                        txt = txt.substring(0, l.posBegin + e + n) + inputText + txt.substring(l.posBegin + e + n)
                        return
                    }
                }
                break
            }
        }
    }

    override fun toString(): String {
        return "ICanvasText(font='$font', fillStyle='$fillStyle', letterSpacing=$letterSpacing, lineHeight=$lineHeight, wordSpacing=$wordSpacing, totalHeight=$totalHeight, marginTop=$marginTop, marginBottom=$marginBottom, txt='$txt', totalWidth=$totalWidth)"
    }
}