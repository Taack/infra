package taack.ui.canvas.text

import taack.ui.canvas.ICanvasDrawable
import taack.ui.canvas.item.MenuEntry
import web.canvas.CanvasRenderingContext2D
import kotlin.math.max
import kotlin.math.min


abstract class CanvasText(var txt: String = "") : ICanvasDrawable {
    companion object {
        var num1: Int = 0
        var num2: Int = 0
    }

    abstract val fontWeight: String
    abstract val fontSize: String
    abstract val fontFace: String
    abstract val fillStyle: String
    abstract val letterSpacing: Double
    abstract val lineHeight: Double
    abstract val wordSpacing: Double
    private var totalHeight: Double = 0.0
    abstract val marginTop: Double
    abstract val marginBottom: Double

    var txtPrefix = ""
    private var styles: List<CanvasStyle> = emptyList()
    var lines: List<CanvasLine> = emptyList()
    var posXEnd: Double = 0.0
    var posXStart: Double = 0.0

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

    private fun addStyle(style: CanvasStyle.Type, p: Int, pEnd: Int) {
        val newStyle = CanvasStyle(style, p, pEnd)
        if (styles.isEmpty())
            styles += CanvasStyle(CanvasStyle.Type.NORMAL, 0, txt.length)
        styles = styles.filterNot {
            it.posNStart in p..pEnd && it.posNEnd >= p && it.posNEnd <= pEnd
        }
        val toSplit = styles.find {
            p > it.posNStart && it.posNEnd > pEnd
        }
        if (toSplit != null) {
            styles += CanvasStyle(toSplit.type, pEnd, toSplit.posNEnd)
            toSplit.posNEnd = p

        }
        val changePosNStart = styles.filter {
            it.posNStart in p..pEnd
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

    fun measureText(ctx: CanvasRenderingContext2D, from: Int, to: Int): Double {
        if (styles.isEmpty()) {
            return ctx.measureText(txt.substring(from, to)).width
        } else {
            val stylesInRangeInclusive = styles.filter {
                it.posNStart >= from && it.posNEnd <= to                        // [...]
            }
            var remaining = styles - stylesInRangeInclusive.toSet()
            val stylesInRangeIncluded = remaining.filter {
                it.posNStart <= from && it.posNEnd >= to                        // ...[]...
            }
            remaining = remaining - stylesInRangeIncluded.toSet()
            val stylesInRangeLeft = remaining.filter {
                it.posNStart < from && it.posNEnd > from && it.posNEnd < to    // ..].]
            }
            remaining = remaining - stylesInRangeLeft.toSet()
            val stylesInRangeRight = remaining.filter {
                it.posNStart in (from + 1)..<to && it.posNEnd > to    // [.[..
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

    fun font(): String {
        return "$fontWeight $fontSize $fontFace"
    }

    fun initCtx(ctx: CanvasRenderingContext2D) {
        ctx.font = font()
        ctx.fillStyle = fillStyle
        ctx.letterSpacing = letterSpacing.toString() + "px"
        ctx.wordSpacing = wordSpacing.toString() + "px"
    }

    private fun initCtx(ctx: CanvasRenderingContext2D, posN: Int) {
        if (styles.isNotEmpty()) {
            styles.find { it.posNStart <= posN && it.posNEnd >= posN }?.initCtx(ctx, this)
        } else {
            ctx.font = font()
            ctx.fillStyle = fillStyle
            ctx.letterSpacing = letterSpacing.toString() + "px"
            ctx.wordSpacing = wordSpacing.toString() + "px"
        }
    }

    override var globalPosYStart: Double = 0.0
    override var globalPosYEnd: Double = 0.0

     override fun getSelectedText(posX: Double?, posY: Double?): CanvasText? {
         if (posX == null || posY == null || isClicked(posX, posY)) {
             return this
         }
         return null
     }

     override fun draw(ctx: CanvasRenderingContext2D, width: Int, posY: Double, posX: Double): Double {
         this.posXStart = posX
         this.posXEnd = width.toDouble()
        ctx.save()
        initCtx(ctx)
        txtPrefix = computeNum()
        val tmpTxt = txtPrefix + txt
        val txtMetrics = ctx.measureText(tmpTxt.ifEmpty { "|" })
        val height = txtMetrics.actualBoundingBoxAscent + txtMetrics.actualBoundingBoxDescent//lineHeight
        globalPosYStart = posY
        val listTxt = tmpTxt.split(" ")
        var pX = posX
        var pY = marginTop + height
        totalHeight = pY
        var currentLetterPos = 0
        var posLetterLineBegin = 0
        var posLetterLineEnd = 0
        lines = emptyList()
        for (i in listTxt.indices) {
            val t = listTxt[i] + (if (i < listTxt.size - 1) " " else "")
            currentLetterPos += t.length
            ctx.save()
            initCtx(ctx, currentLetterPos)
            if (pX + ctx.measureText(t).width >= width) {
                pX = posX + ctx.measureText(txtPrefix).width
                lines += CanvasLine(
                    posLetterLineBegin,
                    posLetterLineEnd,
                    posY + totalHeight,
                    height,
                    posX + ctx.measureText(txtPrefix).width
                )
                pY += height
                totalHeight = pY
                posLetterLineBegin = posLetterLineEnd
            }
            posLetterLineEnd = currentLetterPos
            pX += ctx.measureText(t).width
            ctx.restore()
        }

        if (posLetterLineBegin != currentLetterPos || currentLetterPos == 0) {

            lines += CanvasLine(
                posLetterLineBegin,
                txt.length,
                posY + totalHeight,
                height,
                posX + ctx.measureText(txtPrefix).width
            )
        }
        lines.forEach { l ->
            val stylesInLine = styles.filter { s ->
                s.posNStart >= l.posBegin && s.posNEnd <= l.posEnd || s.posNStart <= l.posBegin && s.posNEnd >= l.posBegin || s.posNStart >= l.posBegin && s.posNEnd >= l.posEnd
            }
            l.drawLine(ctx, this, stylesInLine)
        }

        totalHeight += marginBottom
        val ret = posY + totalHeight
        globalPosYEnd = ret
        ctx.restore()
        return ret
    }


    abstract fun computeNum(): String

    fun findLine(line: CanvasLine): Int {
        var i = 0
        lines.find {
            i++
            it.textY == line.textY
        }
        return i - 1
    }

    override fun click(ctx: CanvasRenderingContext2D, posX: Double, posY: Double): Pair<CanvasLine, Int>? {
        for (line in lines) {
            console.log("CanvaText::click line: $line")
            if (posY in line.textY - line.height..line.textY) {
                val caretPosInCurrentText = line.caretNCoords(ctx, this, posX)
                console.log(
                    "CanvaText::click find text line ... at (${posY}, ${posY})(${line.textY + line.height}) = ${
                        txt.substring(
                            line.posBegin, caretPosInCurrentText

                        )
                    }"
                )
                return Pair(line, caretPosInCurrentText)
            }
        }
        return null
    }

    override fun doubleClick(
        ctx: CanvasRenderingContext2D,
        posX: Double,
        posY: Double
    ): Triple<CanvasLine, Int, Int>? {
        for (line in lines) {
            if (posY in line.textY - line.height..line.textY) {
                val caretPosInCurrentText = line.caretNCoords(ctx, this, posX)
                var charSelectStartNInText =
                    txt.substring(line.posBegin, caretPosInCurrentText)
                        .indexOfLast { it == ' ' } + 1
                charSelectStartNInText += line.posBegin
                var charSelectEndNInText =
                    txt.substring(caretPosInCurrentText + 1).indexOfFirst { it == ' ' }
                charSelectEndNInText += caretPosInCurrentText + 1
                console.log(
                    "click find text line ... at (${posX}, ${posY})(${line.textY + line.height}) = ${
                        txt.substring(
                            line.posBegin, caretPosInCurrentText
                        )
                    }"
                )
                console.log(
                    "double click find text line ... at (${charSelectStartNInText}, ${charSelectEndNInText}) = ${
                        txt.substring(
                            charSelectStartNInText, charSelectEndNInText
                        )
                    }"
                )
                return Triple(
                    line,
                    charSelectStartNInText,
                    charSelectEndNInText
                )
            }
        }
        return null
    }

    override fun getContextualMenuEntries(dblClick: Triple<CanvasLine, Int, Int>): List<MenuEntry> {
        val charSelectStartNInText = dblClick.second
        val charSelectEndNInText = dblClick.third
        return listOf(
            MenuEntry("NORMAL") {
                this.addStyle(
                    CanvasStyle.Type.NORMAL,
                    charSelectStartNInText,
                    charSelectEndNInText
                )
            },
            MenuEntry("BOLD") {
                this.addStyle(
                    CanvasStyle.Type.BOLD,
                    charSelectStartNInText,
                    charSelectEndNInText
                )
            },
            MenuEntry("MONOSPACED") {
                this.addStyle(
                    CanvasStyle.Type.MONOSPACED,
                    charSelectStartNInText,
                    charSelectEndNInText
                )
            },
            MenuEntry("BOLD + MONOSPACED") {
                this.addStyle(
                    CanvasStyle.Type.BOLD_MONOSPACED,
                    charSelectStartNInText,
                    charSelectEndNInText
                )
            }
        )
    }

    override fun toString(): String {
        return "CanvasText(posX=$posXStart, width=$posXEnd, globalPosYStart=$globalPosYStart, globalPosYEnd=$globalPosYEnd)"
    }

}