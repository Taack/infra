package taack.ui.canvas

import taack.ui.canvas.text.*
import web.canvas.CanvasRenderingContext2D

interface ICanvasDrawable : ICanvasSelectable {

    enum class AdocToken(val regex: Regex) {
        TITLE(Regex("^= ")),
        ATTR(Regex("^:([a-z-]+): ([^*`\n]*)")),
        H2(Regex("^== ")),
        H3(Regex("^=== ", RegexOption.MULTILINE)),
        H4(Regex("^==== ")),
        B1(Regex("^\\* ")),
        B2(Regex("^\\*\\* ")),
        TABLE_START(Regex("^\\|===\n")),
        TABLE_COL(Regex("\\|([^|*`\n]*)")),
        TABLE_CELL(Regex("^\\|([^|*`\n]*)")),
        MONO_BOLD(Regex("^`\\*\\*([^*`\n]*)\\*\\*`")),
        BOLD(Regex("^\\*\\*([^*`\n]*)\\*\\*")),
        MONO(Regex("^`([^`\n]*)`")),
        NEXT_DRAWABLE(Regex("^ *\n *\n *", RegexOption.MULTILINE)),
        NORMAL(Regex("(^[^*` \n][^*`\n]*)", RegexOption.MULTILINE)),
        NEXT_LINE(Regex("[\r\n]", RegexOption.MULTILINE)),
        OTHER(Regex("[ \t]*")),
        ERROR(Regex("ERRORRRORR"))
    }

    class TokenInfo(val sequence: String, val token: AdocToken, val start: Int, val end: Int) {
        override fun toString(): String {
            return "$token: $sequence"
        }
    }

    companion object {
        fun dumpAsciidoc(drawables: List<ICanvasDrawable>): String {
            val out = StringBuilder()
            out.append("= Title\n")
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

        fun readAsciidoc(content: String): List<ICanvasDrawable> {
            val canvasDrawables = mutableListOf<ICanvasDrawable>()
            val tokens = mutableListOf<TokenInfo>()
            var start = 0
            var end = 0

            var s = content.trim()

            while (s.isNotEmpty()) {
                var match = false
                for (t in AdocToken.entries) {
                    if (t.regex.containsMatchIn(s)) {
                        val m = t.regex.find(s)
                        if (m!!.value.isNotEmpty()) {
                            match = true
                            end += m.value.length
                            tokens.add(TokenInfo(m.value, t, start, end))
                            start += m.value.length
                            s = s.substring(m.value.length).trimStart(' ', '\t', '\r')
                        }
                        break
                    }
                }
                if (!match) {
                    tokens.add(TokenInfo(s, AdocToken.ERROR, start, start))
                    break
                }
            }

            val it = tokens.iterator()
            var currentText: CanvasText? = null
            var currentTextPosition = 0
            while (it.hasNext()) {
                val token = it.next()
                when (token.token) {
                    AdocToken.TITLE -> {}
                    AdocToken.ATTR -> {}
                    AdocToken.H2 -> {
                        currentText = H2Canvas()
                        currentTextPosition = token.start
                        canvasDrawables.add(currentText)
                    }

                    AdocToken.H3 -> {
                        currentText = H3Canvas()
                        currentTextPosition = token.start
                        canvasDrawables.add(currentText)
                    }

                    AdocToken.H4 -> {
                        currentText = H4Canvas()
                        currentTextPosition = token.start
                        canvasDrawables.add(currentText)
                    }

                    AdocToken.B1 -> {
                        currentText = LiCanvas()
                        currentTextPosition = token.start
                        canvasDrawables.add(currentText)
                    }

                    AdocToken.B2 -> {
                        currentText = Li2Canvas()
                        currentTextPosition = token.start
                        canvasDrawables.add(currentText)
                    }

                    AdocToken.TABLE_START -> TODO()
                    AdocToken.TABLE_COL -> TODO()
                    AdocToken.TABLE_CELL -> TODO()
                    AdocToken.NEXT_DRAWABLE -> {
                        currentText = PCanvas("")
                        currentTextPosition = token.start
                    }

                    AdocToken.NEXT_LINE -> {}
                    AdocToken.MONO_BOLD -> {
                        if (canvasDrawables.isNotEmpty() && currentText != canvasDrawables.last())
                            canvasDrawables.add(currentText!!)
                        currentText?.addToTxtInit(token.sequence)
                        currentText?.addStyle(
                            CanvasStyle.Type.BOLD_MONOSPACED,
                            token.start - currentTextPosition,
                            token.end - currentTextPosition
                        )
                    }

                    AdocToken.BOLD -> {
                        if (canvasDrawables.isNotEmpty() && currentText != canvasDrawables.last())
                            canvasDrawables.add(currentText!!)
                        currentText?.addToTxtInit(token.sequence)
                        currentText?.addStyle(
                            CanvasStyle.Type.BOLD,
                            token.start - currentTextPosition,
                            token.end - currentTextPosition
                        )
                    }

                    AdocToken.MONO -> {
                        if (canvasDrawables.isNotEmpty() && currentText != canvasDrawables.last())
                            canvasDrawables.add(currentText!!)
                        currentText?.addToTxtInit(token.sequence)
                        currentText?.addStyle(
                            CanvasStyle.Type.MONOSPACED,
                            token.start - currentTextPosition,
                            token.end - currentTextPosition
                        )
                    }

                    AdocToken.NORMAL -> {
                        if (canvasDrawables.isNotEmpty() && currentText != canvasDrawables.last())
                            canvasDrawables.add(currentText!!)
                        currentText?.addToTxtInit(token.sequence)
                        currentText?.addStyle(
                            CanvasStyle.Type.NORMAL,
                            token.start - currentTextPosition,
                            token.end - currentTextPosition
                        )
                    }

                    AdocToken.ERROR -> TODO()
                    AdocToken.OTHER -> {}
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