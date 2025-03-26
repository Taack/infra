package taack.ui.wysiwyg.canvasMono.text

//import taack.ui.wysiwyg.canvasMono.text.TextStyle.*
import web.canvas.CanvasRenderingContext2D


data class StringStyle(
    var start: Int,
    var end: Int,
    var italic: Boolean = false,
    var bold: Boolean = false,
    var monospace: Boolean = false,
    var lineThrough: Boolean = false,
    var underline: Boolean = false,
) {
    fun initCtx(ctx: CanvasRenderingContext2D, text: CanvasText) {
        text.initCtx(ctx)
        var font = ""
        if (bold) {
            font += " bold "
        }
        if (italic || text.fontStyle == "italic") {
            font += " italic "
        }
        if (underline) {
            font += " underline "
        }
        if (lineThrough) {
            font += " line-through "
        }
        ctx.font = "$font ${text.fontSize} monospace"
    }

//    fun from(ts: TextStyle): StringStyle {
//        return when (ts) {
//            BOLD -> StringStyle(this.start, this.end, bold = true)
//            NORMAL -> StringStyle(this.start, this.end)
//            MONOSPACED -> StringStyle(this.start, this.end, monospace = true)
//            BOLD_MONOSPACED -> StringStyle(this.start, this.end, monospace = true, bold = true)
//        }
//    }

    fun mergeStyle(other: StringStyle) {
        this.italic = italic || other.italic
        this.bold = bold || other.bold
        this.monospace = monospace || other.monospace
    }
}

//enum class TextStyle(
//    val sepBegin: String,
//    val sepEnd: String,
//    private val regex: Regex?,
//) {
//    NORMAL("", "", null),
//    BOLD("*", "*", Regex("[^\\\\]\\*")),
//    MONOSPACED("`", "`", Regex("[^\\\\]`")),
//    BOLD_MONOSPACED("*`", "`*", Regex("[^\\\\][*`][*`]"))
//    ;
//
//}