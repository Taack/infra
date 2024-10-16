package taack.ui.canvas.text

import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.events.KeyboardEvent

abstract class CanvasText {
    companion object {
        var lastHeight: Double = 0.0
        var num1: Int = 0
        var num2: Int = 0
    }

    val debugLines = false

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
        lines = emptyList()
        for (i in listTxt.indices) {
            val t = listTxt[i] + (if (i < listTxt.size - 1) " " else "")
            if (posX + ctx.measureText(t).width > canvasWidth) {
                txtLetterPosEnd = txtLetterPos
                posX = 10.0
                posY += height
                totalHeight = posY
                lines += CanvasLine(
                    txtLetterPosBegin,
                    txtLetterPosEnd,
                    posY - 2 * height - marginTop,
                    posY - height + marginTop,
                    10.0 + ctx.measureText(numTxt).width
                )
                txtLetterPosBegin = txtLetterPosEnd
            }
            ctx.fillText(t, posX, posY + lastHeight)
            posX += ctx.measureText(t).width
            txtLetterPos += t.length
        }
        if (lines.isEmpty()) lines += CanvasLine(
            0,
            txt.length,
            0.0,
            height + marginTop + marginBottom,
            10.0 + ctx.measureText(numTxt).width
        )
        if (debugLines)
            lines.forEach { it.drawLine(ctx, lastHeight) }
        lastHeight += totalHeight + marginBottom
        ctx.restore()

    }

    abstract fun computeNum(): String

    fun drawText(ctx: CanvasRenderingContext2D, key: KeyboardEvent, charOffset: Int, lineOffset: Int, x: Double, y: Double) {
        ctx.save()
        ctx.font = font
        ctx.fillStyle = fillStyle

        for (l in lines) {
            if (l.posY1 <= y && l.posY2 >= y) {
                val t = txt.substring(l.posBegin, l.posEnd)
                println("Find line: $l => $t")
                for (e in t.indices) {
                    val eX = ctx.measureText(t.substring(0, e)).width
                    if (eX + l.leftMargin >= x) {
                        println("Find letter: ${t.substring(0, e)}, $eX <= $x, e: $e")
                        if (key.code == "Backspace") {
                            txt = txt.substring(0, l.posBegin + e + charOffset - 2) + txt.substring(l.posBegin + e + charOffset - 1)
                        } else {
                            txt = txt.substring(0, l.posBegin + e + charOffset - 1) + key.key + txt.substring(l.posBegin + e + charOffset - 1)
                        }
                        return
                    }
                }
                break
            }
        }
        ctx.restore()
    }

    override fun toString(): String {
        return "ICanvasText(font='$font', fillStyle='$fillStyle', letterSpacing=$letterSpacing, lineHeight=$lineHeight, wordSpacing=$wordSpacing, totalHeight=$totalHeight, marginTop=$marginTop, marginBottom=$marginBottom, txt='$txt', totalWidth=$totalWidth)"
    }
}