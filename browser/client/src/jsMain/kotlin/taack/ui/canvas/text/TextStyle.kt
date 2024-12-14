package taack.ui.canvas.text

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

    private fun mergeStyle(other: StringStyle, start: Int, end: Int): StringStyle {
        val result = StringStyle(start, end)
        result.italic = italic || other.italic
        result.bold = bold || other.bold
        result.monospace = monospace || other.monospace
        return result
    }

    fun isStyleDifferent(style: StringStyle): Boolean {
        return bold == style.bold && italic == style.italic && monospace == style.monospace
    }

    fun merge(other: StringStyle): List<StringStyle> {
        if (other.start < start && other.end > start) {
            if (other.end > end) {
                val r1 = other.mergeStyle(other, other.start, start)
                val r2 = mergeStyle(other, start, end)
                val r3 = other.mergeStyle(other, end, other.end)
                return arrayListOf(r1, r2, r3)
            } else if (other.end == end) {
                val r1 = other.mergeStyle(other, other.start, start)
                val r2 = mergeStyle(other, start, end)
                return arrayListOf(r1, r2)
            } else {
                val r1 = other.mergeStyle(other, other.start, start)
                val r2 = mergeStyle(other, start, other.end)
                val r3 = other.mergeStyle(other, other.end, end)
                return arrayListOf(r1, r2, r3)
            }
        } else if (other.start > start && other.start < end) {
            if (end > other.end) {
                val r1 = mergeStyle(this, start, other.start)
                val r2 = other.mergeStyle(this, other.start, end)
                val r3 = mergeStyle(this, other.end, end)
                return arrayListOf(r1, r2, r3)
            } else if (other.end == end) {
                val r1 = mergeStyle(this, start, other.start)
                val r2 = mergeStyle(other, other.start, end)
                return arrayListOf(r1, r2)
            } else {
                val r1 = mergeStyle(this, start, other.start)
                val r2 = mergeStyle(other, other.start, end)
                val r3 = other.mergeStyle(other, end, other.end)
                return arrayListOf(r1, r2, r3)
            }
        } else if (start == other.start) {
            if (end > other.end) {
                val r1 = mergeStyle(other, start, other.end)
                val r2 = this.mergeStyle(this, other.end, end)
                return arrayListOf(r1, r2)

            } else if (end < other.end) {
                val r1 = mergeStyle(other, start, end)
                val r2 = other.mergeStyle(other, end, other.end)
                return arrayListOf(r1, r2)
            } else {
                return arrayListOf(mergeStyle(other, start, end))
            }
        } else if (start > other.start) {
            return arrayListOf(this)
        } else if (start < other.start) {
            return arrayListOf(other)
        }
        return emptyList()
    }
}

enum class TextStyle(val sepBegin: String, val sepEnd: String, private val regex: Regex?) {
    NORMAL("", "", null),
    BOLD("*", "*", Regex("[^\\\\]\\*")),
    MONOSPACED("`", "`", Regex("[^\\\\]`")),
    BOLD_MONOSPACED("*`", "`*", Regex("[^\\\\][*`][*`]"));

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
        val t = clearFormating(txt, p, pEnd)
        return t.first.substring(0, t.second) + sepBegin + t.first.substring(
            t.second,
            t.third
        ) + sepEnd + t.first.substring(t.third)
    }
}