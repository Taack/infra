package taack.ui.canvas.text

import taack.ui.base.Helper.Companion.trace
import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.canvas.ICanvasDrawable
import web.canvas.CanvasRenderingContext2D
import kotlin.math.max
import kotlin.math.min


abstract class CanvasText(_txtInit: String = "", private var initCitationNumber: Int = 0) : ICanvasDrawable {
    companion object {
        var num1: Int = 0
        var num2: Int = 0
        var figNum: Int = 0
    }

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

    override var globalPosYStart: Double = 0.0
    override var globalPosYEnd: Double = 0.0
    override var citationNumber: Int = initCitationNumber

    private var __txtInit: String = _txtInit
    var txtInit: String
        get() {
            return __txtInit
        }
        set(value) {
            txtVar = value
            __txtInit = value
        }

    var txtPrefix = ""
    var styles: List<CanvasStyle> = emptyList()
    var lines: List<CanvasLine> = emptyList()
    var posXEnd: Double = 0.0
    var posXStart: Double = 0.0
    private var txtVar: String = _txtInit
    val txt: String
        get() {
            return txtVar
        }


    fun addToTxtInit(txt: String) {
        txtInit += txt
    }

    fun addChar(c: String, pos: Int? = null) {
        val p = pos ?: txtVar.length
        trace("CanvasText::addChar: $c, $p")
        txtVar = if (txtVar.isEmpty())
            c
        else
            txtVar.substring(0, p) + c + txtVar.substring(p)

        if (styles.isNotEmpty()) {
            val stylesAfter = styles.filter {
                it.posNStart > p
            }
            stylesAfter.forEach {
                it.posNStart += c.length
                it.posNEnd += c.length
            }
            val currentStyle = styles.find {
                it.posNStart <= p && it.posNEnd >= p
            }
            if (currentStyle != null)
                currentStyle.posNEnd += c.length
        }
    }

    fun delChar(p: Int, pEnd: Int? = null): Int {
        trace("CanvasText::delChar: $p, $pEnd")
        txtVar = txtVar.substring(0, p) + txtVar.substring(p + (pEnd ?: 1))
        if (styles.isNotEmpty()) {
            val stylesAfter = styles.filter {
                it.posNStart > p
            }
            stylesAfter.forEach {
                it.posNStart -= 1
                it.posNEnd -= 1
            }
            val currentStyle = styles.find {
                it.posNStart <= p && it.posNEnd >= p
            }
            if (currentStyle != null)
                currentStyle.posNEnd -= 1
            styles = styles.filterNot {
                it.posNStart >= it.posNEnd
            }
        }

        return txtVar.length
    }

    fun rmChar(p: Int): Int {
        trace("CanvasText::rmChar: $p")
        if (txtVar.isEmpty()) return 0
        txtVar = txtVar.substring(0, p - 1) + txtVar.substring(p)
        if (styles.isNotEmpty()) {
            val stylesAfter = styles.filter {
                it.posNStart > p - 1
            }
            stylesAfter.forEach {
                it.posNStart -= 1
                it.posNEnd -= 1
            }
            val currentStyle = styles.find {
                it.posNStart <= p - 1 && it.posNEnd >= p - 1
            }
            if (currentStyle != null)
                currentStyle.posNEnd -= 1
            styles = styles.filterNot {
                it.posNStart >= it.posNEnd
            }
        }

        return txtVar.length
    }

    fun addStyle(style: CanvasStyle.Type, p: Int, pEnd: Int) {
        traceIndent("CanvasText::addStyle: $style, $p, $pEnd, ${txt.substring(p, pEnd)}")
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
            it.posNEnd in p..pEnd
        }
        changePosNEnd.forEach {
            it.posNEnd = p
        }

        styles += newStyle
        styles = styles.sortedBy { it.posNStart }
        traceDeIndent("CanvasText::addStyle: $styles on $this: $txt")
    }

    fun measureText(ctx: CanvasRenderingContext2D, from: Int, to: Int): Double {
//        trace("CanvasText::measureText: $from, $to")
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

            return width
        }
    }

    fun font(): String {
        return "$fontWeight $fontSize $fontFace"
    }

    fun initCtx(ctx: CanvasRenderingContext2D) {
        //trace("CanvasText::initCtx")
        ctx.font = font()
        ctx.fillStyle = fillStyle
        ctx.letterSpacing = letterSpacing.toString() + "px"
        ctx.wordSpacing = wordSpacing.toString() + "px"
    }

    fun initCtx(ctx: CanvasRenderingContext2D, posN: Int) {
        //trace("CanvasText::initCtx: $posN")
        if (styles.isNotEmpty()) {
            styles.find { it.posNStart <= posN && it.posNEnd >= posN }?.initCtx(ctx, this)
        } else {
            ctx.font = font()
            ctx.fillStyle = fillStyle
            ctx.letterSpacing = letterSpacing.toString() + "px"
            ctx.wordSpacing = wordSpacing.toString() + "px"
        }
    }

    override fun getSelectedText(posX: Double?, posY: Double?): CanvasText? {
        //trace("CanvasText::getSelectedText $this, $posX, $posY")
        return this
    }

    override fun draw(ctx: CanvasRenderingContext2D, width: Double, posY: Double, posX: Double): Double {
        traceIndent("CanvasText::draw: $posX, $posY, $width")
        this.posXStart = posX + citationXPos
        this.posXEnd = width
        ctx.save()
        initCtx(ctx)
        txtPrefix = computeNum()
        val tmpTxt = txtPrefix + txt
        //val txtMetrics = ctx.measureText(tmpTxt.ifEmpty { "|" })
        val height = lineHeight//txtMetrics.actualBoundingBoxAscent// + txtMetrics.actualBoundingBoxDescent//lineHeight
        globalPosYStart = posY
        var pX = posX
        var pY = marginTop + height
        totalHeight = pY
        var currentLetterPos = 0
        var posLetterLineBegin = 0
        var posLetterLineEnd = 0
        lines = emptyList()
        val listTxt = tmpTxt.split(" ")
        for (i in listTxt.indices) {
            val t = listTxt[i] + (if (i < listTxt.size - 1) " " else "")
            currentLetterPos += t.length
            val tWidth = measureText(ctx, posLetterLineEnd, currentLetterPos)
            ctx.save()
            initCtx(ctx, currentLetterPos)
            if (pX + ctx.measureText(txtPrefix).width + tWidth >= width - 30.0) {
                pX = posX + ctx.measureText(txtPrefix).width
                lines += CanvasLine(
                    posLetterLineBegin,
                    posLetterLineEnd,
                    posY + totalHeight,
                    height,
                    ctx.measureText(txtPrefix).width
                )
                pY += height
                totalHeight = pY
                posLetterLineBegin = posLetterLineEnd
            }
            posLetterLineEnd = currentLetterPos
            pX += tWidth
            ctx.restore()
        }
        if (posLetterLineBegin != currentLetterPos || currentLetterPos == 0) {

            lines += CanvasLine(
                posLetterLineBegin,
                txt.length,
                posY + totalHeight,
                height,
                ctx.measureText(txtPrefix).width
            )
        }


        trace("draw: $this: $txt")
        lines.forEach { l ->
            val stylesInLine = styles.filter { s ->
                s.posNStart >= l.posBegin && s.posNEnd <= l.posEnd || s.posNStart <= l.posBegin && s.posNEnd >= l.posBegin || s.posNStart >= l.posBegin && s.posNEnd >= l.posEnd
            }
            trace("line: $l")
            trace(styles.toString())
            trace(stylesInLine.toString())
            l.drawLine(ctx, this, stylesInLine)
        }

        totalHeight += marginBottom
        val ret = posY + totalHeight
        globalPosYEnd = ret
        ctx.restore()
        traceDeIndent("CanvasText::draw: $globalPosYEnd")
        return ret
    }


    abstract fun computeNum(): String

    fun indexOfLine(line: CanvasLine): Int {
        var i = 0
        lines.find {
            i++
            it.textY == line.textY
        }
        return i - 1
    }

    fun indexOfLine(pos: Int): Int {
        var i = 0
        lines.find {
            i++
            it.posBegin <= pos && it.posEnd > pos
        }
        return i - 1
    }

    override fun click(ctx: CanvasRenderingContext2D, posX: Double, posY: Double): Pair<CanvasLine, Int>? {
        traceIndent("CanvasText::click: $posX, $posY")
        for (line in lines) {
            if (posY in line.textY - line.height..line.textY) {
                val caretPosInCurrentText = line.caretNCoords(ctx, this, posX)
                traceDeIndent("CanvasText::click: $line, $caretPosInCurrentText")
                return Pair(line, caretPosInCurrentText)
            }
        }
        if (posY < lines.last().textY) {
            traceDeIndent("CanvasText::click: first ${lines.first()}")
            return Pair(lines.first(), 0)
        }
        traceDeIndent("CanvasText::click: last ${lines.last()}")
        return Pair(lines.last(), txt.length)
    }

    override fun doubleClick(
        ctx: CanvasRenderingContext2D,
        posX: Double,
        posY: Double
    ): Triple<CanvasLine, Int, Int>? {
        traceIndent("CanvasText::doubleClick: $posX, $posY")
        for (line in lines) {
            if (posY in line.textY - line.height..line.textY) {
                val caretPosInCurrentText = line.caretNCoords(ctx, this, posX)
                var charSelectStartNInText =
                    txt.substring(line.posBegin, caretPosInCurrentText)
                        .indexOfLast { !it.isLetter() } + 1
                charSelectStartNInText += line.posBegin
                var charSelectEndNInText =
                    txt.substring(caretPosInCurrentText + 1).indexOfFirst { !it.isLetter() }
                if (charSelectEndNInText == -1) {
                    charSelectEndNInText = line.posEnd
                } else
                    charSelectEndNInText += caretPosInCurrentText + 1
                traceDeIndent("CanvasText::doubleClick1: $line, $charSelectStartNInText, $charSelectEndNInText")
                return Triple(
                    line,
                    charSelectStartNInText,
                    charSelectEndNInText
                )
            }
        }
        traceDeIndent("CanvasText::doubleClick: null")
        return null
    }

    override fun toString(): String {
        return "CanvasText(posXStart=$posXStart, posXEnd=$posXEnd, globalPosYStart=$globalPosYStart, globalPosYEnd=$globalPosYEnd, lines.size=${lines.size})"
    }

    override fun reset() {
        citationNumber = initCitationNumber
        txtVar = txtInit
//        styles = emptyList()
    }

    override fun dumpAsciidoc(): String {
        if (styles.isNotEmpty()) {
            val ret = StringBuilder()
            for (s in styles) {
                ret.append(s.dumpAsciidoc(this))
            }
            return ret.toString()
        } else {
            return txt
        }
    }

}