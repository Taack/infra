package taack.ui.canvas

import taack.ui.canvas.text.CanvasText
import taack.ui.canvas.text.H2Canvas
import taack.ui.canvas.text.H3Canvas
import taack.ui.canvas.text.H4Canvas
import web.canvas.CanvasRenderingContext2D

interface ICanvasDrawable: ICanvasSelectable {

    enum class AsciidocToken(exp: Regex) {
        H2(Regex("^== ")),
        H3(Regex("^=== ")),
        H4(Regex("^==== ")),
        B1(Regex("^\\*")),
        B2(Regex("^\\*\\* ")),
        TABLE_START(Regex("^\\|===")),
        TABLE_COL(Regex("\\|([^|]*)")),
        TABLE_CELL(Regex("^\\|([^|]*)")),
        BOLD(Regex("\\*\\*")),
        MONO(Regex("`")),
        NEXT_DRAWABLE(Regex("\n\n")),
        NEXT_LINE(Regex("\n")),
        P(Regex(".*")),
    }

    companion object {
        fun dumpAsciidoc(drawables: List<ICanvasDrawable>): String {
            val out = StringBuilder()
            out.append("= Titre\n")
            out.append(":doctype: book\n")
            out.append(":toc: left\n")
            out.append(":toc-title: Table of Contents of {doctitle}\n")
            out.append(":toclevels: 2\n")
            out.append(":sectnums: 2\n")
            out.append(":sectnumlevels: 2\n")
            out.append("\n")

            drawables.forEach {
                out.append("\n")
                out.append(it.dumpAsciidoc())
            }
            out.append("\n")
            return out.toString()
        }

        fun readAsciidoc(file: String): List<ICanvasDrawable> {
            val drawables = file.substring(file.indexOf("\n\n")).split("\n")
            val canvasDrawables = mutableListOf<ICanvasDrawable>()
            for (drawable in drawables) {
                if (drawable.startsWith("== ")) {
                    canvasDrawables.add(H2Canvas(drawable.substring(3)))
                } else if (drawable.startsWith("=== ")) {
                    canvasDrawables.add(H3Canvas(drawable.substring(4)))
                } else if (drawable.startsWith("==== ")) {
                    canvasDrawables.add(H4Canvas(drawable.substring(5)))
                }
            }
            return canvasDrawables
        }

    }

    var globalPosYStart: Double
    var globalPosYEnd: Double
    var citationNumber: Int

    fun isClicked(posX: Double, posY: Double): Boolean {
        return posY in globalPosYStart..globalPosYEnd
    }

    fun drawCitation(ctx: CanvasRenderingContext2D, textY: Double, height: Double): Double {
        ctx.save()
        ctx.fillStyle = "#dadde3"
        for (i in 0 until citationNumber) {
            val marginTop = getSelectedText()!!.marginTop
            val marginBottom = getSelectedText()!!.marginBottom
            ctx.fillRect(8.0 + 16.0 * i, textY - height * 1.2, 4.0, height + marginTop + marginBottom)
        }
        ctx.restore()
        return 16.0 * citationNumber
    }

    fun getSelectedText(posX: Double? = null, posY: Double? = null): CanvasText?

    fun draw(ctx: CanvasRenderingContext2D, width: Double, posY: Double, posX: Double): Double

    fun dumpAsciidoc(): String

    fun reset()
}