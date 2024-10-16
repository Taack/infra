package taack.ui.canvas.text

import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.events.KeyboardEvent

abstract class CanvasText {
    companion object {
        var globalPosY: Double = 0.0
        var num1: Int = 0
        var num2: Int = 0
    }

    val debugLines = true

    abstract val font: String
    abstract val fillStyle: String
    abstract val letterSpacing: Double
    abstract val lineHeight: Double
    abstract val wordSpacing: Double
    var totalHeight: Double = 0.0
    abstract val marginTop: Double
    abstract val marginBottom: Double

    var lines: List<CanvasLine> = emptyList()
    var txt = ""
    var totalWidth: Double = 0.0

    fun draw(ctx: CanvasRenderingContext2D, canvasWidth: Int) {
        ctx.save()
        ctx.font = font
        ctx.fillStyle = fillStyle

        println("draw $this")

        val numTxt = computeNum()
        val tmpTxt = numTxt + txt
        val txtMetrics = ctx.measureText(tmpTxt)
        totalWidth = txtMetrics.width
        val height = txtMetrics.actualBoundingBoxAscent + txtMetrics.actualBoundingBoxDescent//lineHeight

        val listTxt = tmpTxt.split(" ")
        var posX = 10.0
        var posY = marginTop + height
        totalHeight = posY
        var currentLetterPos = 0
        var posLetterLineBegin = 0
        var posLetterLineEnd = 0
        lines = emptyList()
        for (i in listTxt.indices) {
            val t = listTxt[i] + (if (i < listTxt.size - 1) " " else "")
            currentLetterPos += t.length

            if (posX + ctx.measureText(t).width >= canvasWidth) {
                posX = 10.0
                lines += CanvasLine(
                    posLetterLineBegin,
                    posLetterLineEnd,
                    globalPosY + totalHeight,
                    height,
                    10.0 + ctx.measureText(numTxt).width
                )
                posY += height
                totalHeight = posY
                posLetterLineBegin = posLetterLineEnd
            }
            posLetterLineEnd = currentLetterPos
            posX += ctx.measureText(t).width
        }
        if (posLetterLineBegin != currentLetterPos) {

            lines += CanvasLine(
                posLetterLineBegin,
                txt.length,
                globalPosY + totalHeight,
                height,
                10.0 + ctx.measureText(numTxt).width
            )
        }
        lines.forEach {
            it.drawLine(ctx, this)
        }

        if (debugLines)
            lines.forEach {
                println(it.toString())
                it.drawLine(ctx)
            }
        totalHeight += marginBottom
        globalPosY += totalHeight

        ctx.restore()
    }

    abstract fun computeNum(): String

    fun drawLine(line: CanvasLine,         ctx: CanvasRenderingContext2D,
        key: KeyboardEvent,
        charOffset: Int,
        lineOffset: Int,
        x: Double,
        y: Double) {
        val j = line.caretNCoords(ctx, this, x)

        val e = j
        if (key.code == "Backspace") {
            txt = txt.substring(0, line.posBegin + e + charOffset - 2) + txt.substring(line.posBegin + e + charOffset - 1)
        } else {
            txt = txt.substring(
                0,
                line.posBegin + e + charOffset - 1
            ) + key.key + txt.substring(line.posBegin + e + charOffset - 1)
        }

    }

    fun drawText(
        ctx: CanvasRenderingContext2D,
        key: KeyboardEvent,
        charOffset: Int,
        lineOffset: Int,
        x: Double,
        y: Double
    ) {
        val l = lines.find {
            it.textY >= y
        } ?: lines.last()
        drawLine(l, ctx, key, charOffset, lineOffset, x, y)
    }

    override fun toString(): String {
        return "CanvasText(font='$font', fillStyle='$fillStyle', letterSpacing=$letterSpacing, lineHeight=$lineHeight, wordSpacing=$wordSpacing, totalHeight=$totalHeight, marginTop=$marginTop, marginBottom=$marginBottom, txt='$txt', totalWidth=$totalWidth)"
    }
}