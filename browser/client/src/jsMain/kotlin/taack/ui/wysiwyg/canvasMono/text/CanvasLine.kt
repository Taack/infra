package taack.ui.wysiwyg.canvasMono.text

import taack.ui.base.Helper.Companion.trace
import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import web.canvas.CanvasRenderingContext2D


class CanvasLine(
    val posBegin: Int,
    val posEnd: Int,
    val textY: Double,
    val height: Double,
    val leftMargin: Double = 0.0
) {

    val length: Int
        get() = posEnd - posBegin

    fun drawLine(ctx: CanvasRenderingContext2D, text: CanvasText) {
        traceIndent("CanvasLine::drawLine: $this")
        var posXStart = text.posXStart
        text.drawCitation(ctx, textY, height)
        val lineStyles = text.textStyles.filter {
            posBegin <= it.end && posEnd >= it.start
        }
        trace("CanvasLine::drawLine:lineStyles: $lineStyles between $posBegin and $posEnd")
        if (lineStyles.isNotEmpty()) {
            var pe = posBegin

            val contextList = mutableListOf<StringStyle>()
            var currentBackgroundStyle: StringStyle? = null
            var currentEndPosition = 0
            // merge at most 2 styles
            lineStyles.sortedBy { it.start }.forEach {

                if (currentBackgroundStyle == null) {
                    currentBackgroundStyle = it
                    currentEndPosition = it.end
                    contextList.add(it)
                } else {
                    if (currentEndPosition < it.start) {
                        currentBackgroundStyle = it
                        currentEndPosition = it.end
                        contextList.add(it)
                    } else if (currentEndPosition > it.start && currentEndPosition > it.end) {
                        it.mergeStyle(currentBackgroundStyle!!)
                        contextList.add(it)
                        val currentPosition = it.end
                        currentBackgroundStyle!!.end = it.start
                        val ctc = currentBackgroundStyle!!.copy()
                        ctc.start = currentPosition
                        ctc.end = currentEndPosition
                        contextList.add(ctc)
                        currentBackgroundStyle = ctc//currentBackgroundStyle!!.copy()
                    }
                }
            }
//            if (currentStyle != null) {
//                contextList.add(currentStyle!!)
//            }
            trace("CanvasLine::drawLine:contextList $contextList")
            contextList.sortedBy { it.start }.forEach {
                val s = if (it.start < posBegin) posBegin else it.start
                val e = if (it.end > posEnd) posEnd else it.end
                if (s > pe) {
                    trace("CanvasLine::drawLine:s>pe: s: $s pe: $pe")
                    ctx.fillText(
                        text.txt.substring(pe, s),
//                        (if (text.txtPrefix.isEmpty() || pe > 0) leftMargin else 0.0) + posXStart,
                        posXStart,
                        textY
                    )
                    posXStart += ctx.measureText(text.txt.substring(pe, s)).width
                }
                ctx.save()
                it.getTextStyle().initCtx(ctx, text)
                trace("CanvasLine::drawLine $s $e ${text.txt.substring(s, e)}")
                ctx.fillText(text.txt.substring(s, e),
//                    (if (text.txtPrefix.isEmpty() || s > 0) leftMargin else 0.0) + posXStart,
                    posXStart,
                    textY
                )
                posXStart += ctx.measureText(text.txt.substring(s, e)
                ).width

                ctx.restore()
                pe = e
            }
            if (pe < posEnd) {
                trace("CanvasLine::drawLine:pe < posEnd: posEnd: $posEnd pe: $pe")
                ctx.fillText(
                    text.txt.substring(pe, posEnd),
//                    (if (text.txtPrefix.isEmpty() || pe > 0) leftMargin else 0.0) + posXStart,
                    posXStart,
                    textY
                )
                posXStart += ctx.measureText(
                    text.txt.substring(pe, posEnd)
                ).width
            }
        } else {
            trace("CanvasLine::drawLine:else (empty...) ...")
            ctx.fillText(
                text.txt.substring(posBegin, posEnd),
                (if (posBegin > 0) leftMargin else 0.0) + posXStart,
                textY
            )
        }
        traceDeIndent("CanvasLine::drawLine: ---")
    }

    fun caretNCoords(ctx: CanvasRenderingContext2D, text: CanvasText, x: Double): Int {
        ctx.save()
        text.initCtx(ctx)

        for (i in posBegin..posEnd) {
            val pos = text.measureText(ctx, posBegin, i) + leftMargin + text.posXStart
            if (pos >= x) {
                ctx.restore()
                trace("CanvasLine::caretNCoords: $x, ret ${i - 1}")
                return i - 1
            }
        }
        ctx.restore()

        trace("CanvasLine::caretNCoords: $x, ret txt.length = ${text.txt.length}")
        return text.txt.length
    }

    override fun toString(): String {
        return "CanvasLine(posBegin=$posBegin, posEnd=$posEnd, textY=$textY, height=$height, leftMargin=$leftMargin)"
    }

}