package taack.ui.canvas.text

import taack.ui.base.Helper.Companion.trace
import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.canvas.ICanvasDrawable
import web.canvas.CanvasRenderingContext2D


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
    var lines: List<CanvasLine> = emptyList()
    private val internTextStyles: List<StringStyle>? = null
    val textStyles: List<StringStyle>
        get() {
            if (internTextStyles == null) {
                val internTextStyles = mutableListOf<StringStyle>()

                val inlineStyles = mutableListOf<StringStyle>()

                for (s in TextStyle.entries) {
                    var c = true
                    var p = 0
                    while (c) {
                        val ps = txt.substring(p).indexOf(s.sepBegin)
                        if (ps != -1) {
                            val pe = txt.substring(ps).indexOf(s.sepEnd)
                            if (pe != -1) {
                                inlineStyles.add(StringStyle(ps, pe).from(s))
                                p = pe
                            }
                        } else c = false
                    }
                }

                if (inlineStyles.isNotEmpty()) {
                    inlineStyles.sortWith(compareBy({it.start}, {it.end}))

                    var currentStyle = inlineStyles.first()
                    inlineStyles.forEach {
                        if (it != currentStyle) {
                            internTextStyles.addAll(currentStyle.merge(it))
                            currentStyle = it
                        }
                    }
                }
            }
            return internTextStyles!!
        }
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
    }

    fun delChar(p: Int, pEnd: Int? = null): Int {
        trace("CanvasText::delChar: $p, $pEnd")
        txtVar = txtVar.substring(0, p) + txtVar.substring(p + (pEnd ?: 1))

        return txtVar.length
    }

    fun rmChar(p: Int): Int {
        trace("CanvasText::rmChar: $p")
        if (txtVar.isEmpty()) return 0
        txtVar = txtVar.substring(0, p - 1) + txtVar.substring(p)
        return txtVar.length
    }

    fun addStyle(style: TextStyle, p: Int, pEnd: Int) {
        txtVar = style.applyStyle(txt, p, pEnd)
    }

    fun measureText(ctx: CanvasRenderingContext2D, from: Int, to: Int): Double {
//        trace("CanvasText::measureText: $from, $to")

        return ctx.measureText(txt.substring(from, to)).width
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

        ctx.font = font()
        ctx.fillStyle = fillStyle
        ctx.letterSpacing = letterSpacing.toString() + "px"
        ctx.wordSpacing = wordSpacing.toString() + "px"
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
            listTxt[i]
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
            l.drawLine(ctx, this)
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
        return txt
    }
}