package taack.ui.wysiwyg.canvasMono

import taack.ui.base.Helper.Companion.trace
import taack.ui.wysiwyg.canvasMono.text.*
import web.canvas.CanvasRenderingContext2D
import web.html.HTMLInputElement

interface ICanvasDrawable : ICanvasSelectable {

    enum class AdocToken(val regex: Regex) {
        TITLE(Regex("^= ")),
        ATTR(Regex("^:([a-z-]+): ([^*`\n]*)")),
        INNER_BLOCK_DELIM(Regex("^____(__)+\n")),
        //        INDENT(Regex("^> ")),
        BLOCK_DELIM(Regex("^____\n")),
        H4(Regex("^==== ")),
        H3(Regex("^=== ")),
        H2(Regex("^== ")),
        B1(Regex("^\\* ")),
        B2(Regex("^\\*\\* ")),
        FIG(Regex("^\\.")),
        BOLD(Regex("^\\*\\*([^*`\n]*)\\*\\*")),
        MONO(Regex("^`([^`\n]*)`")),
        NEXT_DRAWABLE(Regex("^ *\n *\n *")),
        NEXT_LINE(Regex("^\n")),
        NORMAL(Regex("^[^\n]+")),
        OTHER(Regex("[ \t]*")),
        ERROR(Regex("ERRORRRORR"))
    }

    class TokenInfo(val sequence: String, val token: AdocToken, val start: Int, val end: Int) {
        override fun toString(): String {
            return "$token: $sequence"
        }
    }

    var globalPosYStart: Double
    var globalPosYEnd: Double
    var citationNumber: Int
    val citationXPos: Double
        get() = 16.0 * citationNumber

    fun isClicked(posX: Double, posY: Double): Boolean {
        return posY in globalPosYStart..globalPosYEnd
    }

    fun drawCitation(ctx: CanvasRenderingContext2D, textY: Double, height: Double): Double {
        ctx.save()
        ctx.fillStyle = "#dadde3"
        for (i in 0 until citationNumber) {
            val marginTop = getSelectedText()!!.marginTop
            val marginBottom = getSelectedText()!!.marginBottom
            ctx.fillRect(8.0 + 16.0 * i, textY - height - marginTop, 4.0, height + marginTop + marginBottom)        }
        ctx.restore()
        return citationXPos
    }

    fun getSelectedText(posX: Double? = null, posY: Double? = null): CanvasText?

    fun draw(ctx: CanvasRenderingContext2D, width: Double, posY: Double, posX: Double): Double

    fun reset()
}