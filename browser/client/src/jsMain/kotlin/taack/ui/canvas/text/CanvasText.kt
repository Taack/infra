package taack.ui.canvas.text

import web.canvas.CanvasRenderingContext2D


abstract class CanvasText {
    companion object {
        var globalPosY: Double = 0.0
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

    var lines: List<CanvasLine> = emptyList()
    var styles: List<CanvasStyle> = emptyList()
    var txtPrefix = ""
    var txt = ""

    fun initCtx(ctx: CanvasRenderingContext2D) {
        ctx.font = font
        ctx.fillStyle = fillStyle
        ctx.letterSpacing = letterSpacing.toString() + "px"
        ctx.wordSpacing = wordSpacing.toString() + "px"
    }

    fun draw(ctx: CanvasRenderingContext2D, canvasWidth: Int) {
        ctx.save()
        initCtx(ctx)
        txtPrefix = computeNum()
        val tmpTxt = txtPrefix + txt
        val txtMetrics = ctx.measureText(if (tmpTxt.isEmpty()) "|" else tmpTxt)
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
                posX = 10.0 + ctx.measureText(txtPrefix).width
                lines += CanvasLine(
                    posLetterLineBegin,
                    posLetterLineEnd,
                    globalPosY + totalHeight,
                    height,
//                    10.0 + if (posLetterLineBegin == 0) ctx.measureText(txtPrefix).width else 0.0
                    10.0 + ctx.measureText(txtPrefix).width
                )
                posY += height
                totalHeight = posY
                posLetterLineBegin = posLetterLineEnd
            }
            posLetterLineEnd = currentLetterPos
            posX += ctx.measureText(t).width
        }

        if (posLetterLineBegin != currentLetterPos || currentLetterPos == 0) {

            lines += CanvasLine(
                posLetterLineBegin,
                txt.length,
                globalPosY + totalHeight,
                height,
//                10.0 + if (posLetterLineBegin == 0) ctx.measureText(txtPrefix).width else 0.0
                10.0 + ctx.measureText(txtPrefix).width
            )
        }
        lines.forEach { l ->
            val stylesInLine = styles.filter { s ->
                s.posNStart >= l.posBegin || s.posNEnd <= l.posEnd
            }
            console.log("line: $l, stylesInLine: $stylesInLine")
            l.drawLine(ctx, this, stylesInLine)
        }

        if (debugLines)
            lines.forEach {
                it.drawLine(ctx)
            }
        totalHeight += marginBottom
        globalPosY += totalHeight

        ctx.restore()
    }

    abstract fun computeNum(): String

    fun addChar(c: Char, p: Int) {
        txt = txt.substring(0, p) + c + txt.substring(p)
    }

    fun delChar(p: Int, pEnd: Int? = null): Int {
        txt = txt.substring(0, p) + txt.substring(p + (pEnd ?: 1))
        return txt.length
    }

    fun rmChar(p: Int): Int {
        txt = txt.substring(0, p - 1) + txt.substring(p)
        return txt.length
    }

    fun addStyle(style: CanvasStyle.Type, p: Int, pEnd: Int) {
        val newStyle = CanvasStyle(style, p, pEnd)
        if (styles.isEmpty())
            styles += CanvasStyle(CanvasStyle.Type.NORMAL, 0, txt.length)
        styles = styles.filterNot {
            it.posNStart >= p && it.posNStart <= pEnd && it.posNEnd >= p && it.posNEnd <= pEnd
        }
        val toSplit = styles.find {
            p > it.posNStart && it.posNEnd > pEnd
        }
        if (toSplit != null) {
            styles += CanvasStyle(toSplit.type, pEnd + 1, toSplit.posNEnd)
            toSplit.posNEnd = p - 1

        }
        val changePosNStart = styles.filter {
            it.posNStart >= p && it.posNStart <= pEnd
        }
        changePosNStart.forEach {
            it.posNStart = pEnd
        }
        val changePosNEnd = styles.filter {
            it.posNStart >= p && it.posNEnd >= p && it.posNEnd <= pEnd
        }
        changePosNEnd.forEach {
            it.posNEnd = p
        }
        styles += newStyle
        styles = styles.sortedBy { it.posNStart }
    }

    fun findLine(y: Double): Int {
        var i = 0
        lines.find {
            i++
            it.textY >= y
        }
        return i - 1
    }

    fun findLine(line: CanvasLine): Int {
        var i = 0
        lines.find {
            i++
            it.textY == line.textY
        }
        return i - 1
    }
//
//    fun drawLine(
//        line: CanvasLine, ctx: CanvasRenderingContext2D,
//        key: KeyboardEvent,
//        charOffset: Int,
//        lineOffset: Int,
//        x: Double,
//        y: Double
//    ) {
//        val j = line.caretNCoords(ctx, this, x)
//
//        val e = j
//        if (key.code == "Backspace") {
//            txt = txt.substring(0, e + charOffset - 2) + txt.substring(e + charOffset - 1)
//        } else {
//            txt = txt.substring(
//                0,
//                e + charOffset - 1
//            ) + key.key + txt.substring(e + charOffset - 1)
//        }
//
//    }
//
//    fun drawText(
//        ctx: CanvasRenderingContext2D,
//        key: KeyboardEvent,
//        charOffset: Int,
//        lineOffset: Int,
//        x: Double,
//        y: Double
//    ) {
//        val l = lines.find {
//            it.textY >= y
//        } ?: lines.last()
//        drawLine(l, ctx, key, charOffset, lineOffset, x, y)
//    }
//
//    override fun toString(): String {
//        return "CanvasText(globalPosY='$globalPosY', font='$font', fillStyle='$fillStyle', letterSpacing=$letterSpacing, lineHeight=$lineHeight, wordSpacing=$wordSpacing, totalHeight=$totalHeight, marginTop=$marginTop, marginBottom=$marginBottom, txt='$txt', totalWidth=$totalWidth)"
//    }
}