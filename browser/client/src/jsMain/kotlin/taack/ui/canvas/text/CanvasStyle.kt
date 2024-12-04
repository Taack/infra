package taack.ui.canvas.text

import web.canvas.CanvasRenderingContext2D


enum class CanvasStyle(val sepBegin: String, val sepEnd: String) {
    NORMAL("", ""),
    BOLD("*", "*"),
    MONOSPACED("`", "`"),
    BOLD_MONOSPACED("*`","`*");

    fun initCtx(ctx: CanvasRenderingContext2D, text: CanvasText) {
        text.initCtx(ctx)
        ctx.font = when(this) {
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

    fun applyStyle(txt: String, p: Int, pEnd: Int): String {
        return txt.substring(0, p) + sepBegin + txt.substring(p, pEnd) + sepEnd + txt.substring(pEnd)
    }
}