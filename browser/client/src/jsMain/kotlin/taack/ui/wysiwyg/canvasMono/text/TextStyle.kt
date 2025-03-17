package taack.ui.wysiwyg.canvasMono.text

import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import web.canvas.CanvasRenderingContext2D


data class StringStyle(
    var start: Int,
    var end: Int,
    var italic: Boolean = false,
    var bold: Boolean = false,
    var monospace: Boolean = false
) {
    fun from(ts: TextStyle): StringStyle {
        return when (ts) {
            TextStyle.BOLD -> StringStyle(this.start, this.end, bold = true)
            TextStyle.NORMAL -> StringStyle(this.start, this.end)
            TextStyle.MONOSPACED -> StringStyle(this.start, this.end, monospace = true)
            TextStyle.BOLD_MONOSPACED -> StringStyle(this.start, this.end, monospace = true, bold = true)
        }
    }

    fun getTextStyle(): TextStyle {
        if (monospace && bold) {
            return TextStyle.BOLD_MONOSPACED
        } else if (monospace) {
            return TextStyle.MONOSPACED
        } else if (bold) {
            return TextStyle.BOLD
        } else return TextStyle.NORMAL
    }

    fun mergeStyle(other: StringStyle) {
        this.italic = italic || other.italic
        this.bold = bold || other.bold
        this.monospace = monospace || other.monospace
    }
}

enum class TextStyle(val sepBegin: String, val sepEnd: String, private val regex: Regex?) {
    NORMAL("", "", null),
    BOLD("*", "*", Regex("[^\\\\]\\*")),
    MONOSPACED("`", "`", Regex("[^\\\\]`")),
    BOLD_MONOSPACED("*`", "`*", Regex("[^\\\\][*`][*`]"))
    ;

    fun initCtx(ctx: CanvasRenderingContext2D, text: CanvasText) {
        text.initCtx(ctx)
        ctx.font = when (this) {
            NORMAL -> {
                text.font()
            }

            BOLD -> {
                "bold ${text.fontSize} ${text.fontFace}"
            }

            MONOSPACED -> {
                "${text.fontWeight} ${text.fontSize} monospace"
            }

            BOLD_MONOSPACED -> {
                "bold ${text.fontSize} monospace"
            }
        }
    }

    private fun clearFormating(txt: String, p: Int, pEnd: Int): Triple<String, Int, Int> {
        var text = txt
        var ptEnd = pEnd
        entries.forEach {
            if (it.regex != null) {
                val txtNormal = text.substring(p, ptEnd).replace(it.regex, "")
                ptEnd -= (ptEnd - p - txtNormal.length)
                text = text.substring(0, p) + txtNormal + text.substring(ptEnd)
            }
        }
        return Triple(text, p, ptEnd)
    }

//    fun chargeWordCtx(ctx: CanvasRenderingContext2D, canvasText: CanvasText, text: String, currentWordStyle: StringStyle): StringStyle {
//        entries.forEach { entry ->
//            if (entry.regex != null && text.startsWith(entry.sepBegin)) {
//                when (entry) {
//                    NORMAL -> {
//                        // Path-through
//                    }
//                    BOLD -> {
//                        val end = text.endsWith(BOLD.sepEnd)
//                        val ws = StringStyle(text, !currentWordStyle.bold && !end, currentWordStyle.italic, currentWordStyle.monospace)
//                        BOLD.initCtx(ctx, canvasText)
//                        return ws
//                    }
//                    MONOSPACED -> {
//                        val end = text.endsWith(MONOSPACED.sepEnd)
//                        val ws = StringStyle(text, !currentWordStyle.bold, currentWordStyle.italic, !currentWordStyle.monospace && !end)
//                        BOLD.initCtx(ctx, canvasText)
//                        return ws
//
//                    }
//                    BOLD_MONOSPACED -> {
//                        val end = text.endsWith(BOLD_MONOSPACED.sepEnd)
//                        val ws = StringStyle(text, !currentWordStyle.bold && !end, currentWordStyle.italic, currentWordStyle.monospace && !end)
//                        BOLD.initCtx(ctx, canvasText)
//                        return ws
//                    }
//                }
//            }
//        }
//        NORMAL.initCtx(ctx, canvasText)
//        return currentWordStyle
//    }

    fun applyStyle(txt: String, p: Int, pEnd: Int): String {
        traceIndent("TextStyle::applyStyle +++ $txt, $p, $pEnd")
        val t = clearFormating(txt, p, pEnd)
        traceDeIndent("TextStyle::applyStyle --- $t")
        return t.first.substring(0, t.second) + sepBegin + t.first.substring(
            t.second,
            t.third
        ) + sepEnd + t.first.substring(t.third)
    }
}