package taack.ui.canvas.text

import web.canvas.CanvasRenderingContext2D
import kotlin.math.max
import kotlin.math.min


abstract class CanvasText {
    companion object {
        var globalPosY: Double = 0.0
        var num1: Int = 0
        var num2: Int = 0
    }

    val debugLines = false

    abstract val fontWeight: String
    abstract val fontSize: String
    abstract val fontFace: String
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

    fun font(): String {
        return "$fontWeight ${fontSize} $fontFace"
    }

    fun initCtx(ctx: CanvasRenderingContext2D) {
        ctx.font = font()
        ctx.fillStyle = fillStyle
        ctx.letterSpacing = letterSpacing.toString() + "px"
        ctx.wordSpacing = wordSpacing.toString() + "px"
    }

    fun initCtx(ctx: CanvasRenderingContext2D, posN: Int) {
        if (styles.isNotEmpty()) {
            styles.find { it.posNStart <= posN && it.posNEnd >= posN }?.initCtx(ctx, this)
        } else {
            ctx.font = font()
            ctx.fillStyle = fillStyle
            ctx.letterSpacing = letterSpacing.toString() + "px"
            ctx.wordSpacing = wordSpacing.toString() + "px"
        }
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
            ctx.save()
            initCtx(ctx, currentLetterPos)
            if (posX + ctx.measureText(t).width >= canvasWidth) {
                posX = 10.0 + ctx.measureText(txtPrefix).width
                lines += CanvasLine(
                    posLetterLineBegin,
                    posLetterLineEnd,
                    globalPosY + totalHeight,
                    height,
                    10.0 + ctx.measureText(txtPrefix).width
                )
                posY += height
                totalHeight = posY
                posLetterLineBegin = posLetterLineEnd
            }
            posLetterLineEnd = currentLetterPos
            posX += ctx.measureText(t).width
            ctx.restore()
        }

        if (posLetterLineBegin != currentLetterPos || currentLetterPos == 0) {

            lines += CanvasLine(
                posLetterLineBegin,
                txt.length,
                globalPosY + totalHeight,
                height,
                10.0 + ctx.measureText(txtPrefix).width
            )
        }
        lines.forEach { l ->
            val stylesInLine = styles.filter { s ->
                s.posNStart >= l.posBegin && s.posNEnd <= l.posEnd || s.posNStart <= l.posBegin && s.posNEnd >= l.posBegin || s.posNStart >= l.posBegin && s.posNEnd >= l.posEnd
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
            styles += CanvasStyle(toSplit.type, pEnd, toSplit.posNEnd)
            toSplit.posNEnd = p

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

    fun measureText(ctx: CanvasRenderingContext2D, from: Int, to: Int): Double {
        if (styles.isEmpty()) {
            return ctx.measureText(txt.substring(from, to)).width
        } else {
            val stylesInRangeInclusive = styles.filter {
                it.posNStart >= from && it.posNEnd <= to                        // [...]
            }
            var remaining = styles - stylesInRangeInclusive
            val stylesInRangeIncluded = remaining.filter {
                it.posNStart <= from && it.posNEnd >= to                        // ...[]...
            }
            remaining = remaining - stylesInRangeIncluded
            val stylesInRangeLeft = remaining.filter {
                it.posNStart < from && it.posNEnd > from && it.posNEnd < to    // ..].]
            }
            remaining = remaining - stylesInRangeLeft
            val stylesInRangeRight = remaining.filter {
                it.posNStart > from && it.posNStart < to && it.posNEnd > to    // [.[..
            }

            var width = 0.0
            (stylesInRangeInclusive + stylesInRangeIncluded + stylesInRangeLeft + stylesInRangeRight).forEach {
                val begin = max(it.posNStart, from)
                val end = min(it.posNEnd, to)
                ctx.save()
                it.initCtx(ctx, this)
                width += ctx.measureText(txt.substring(begin, end)).width
                ctx.restore()
            }

            console.log("measureText: $width, from: $from, to: $to, stylesInRangeIncluded: $stylesInRangeIncluded, stylesInRangeInclusive: $stylesInRangeInclusive, stylesInRangeLeft: $stylesInRangeLeft, stylesInRangeRight: $stylesInRangeRight")
            return width
        }
    }

}