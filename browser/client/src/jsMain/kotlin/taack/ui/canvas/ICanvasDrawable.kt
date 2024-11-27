package taack.ui.canvas

import taack.ui.base.Helper.Companion.trace
import taack.ui.canvas.table.CanvasTable
import taack.ui.canvas.table.TxtHeaderCanvas
import taack.ui.canvas.table.TxtRowCanvas
import taack.ui.canvas.text.*
import web.canvas.CanvasRenderingContext2D

interface ICanvasDrawable : ICanvasSelectable {

    enum class AdocToken(val regex: Regex) {
        TITLE(Regex("^= ")),
        ATTR(Regex("^:([a-z-]+): ([^*`\n]*)")),
        INDENT(Regex("^> ")),
        H2(Regex("^== ")),
        H3(Regex("^=== ")),
        H4(Regex("^==== ")),
        B1(Regex("^\\* ")),
        B2(Regex("^\\*\\* ")),
        TABLE_START(Regex("^\\|===")),
        TABLE_COL(Regex("^\\|[^*`=\n][^|*`\n]+\\|([^|*`\n])+")),
        TABLE_CELL(Regex("^\\|")),
        MONO_BOLD(Regex("^`\\*\\*([^*`\n]*)\\*\\*`")),
        BOLD(Regex("^\\*\\*([^*`\n]*)\\*\\*")),
        MONO(Regex("^`([^`\n]*)`")),
        NEXT_DRAWABLE(Regex("^ *\n *\n *")),
        NORMAL(Regex("(^[^*`\n]+)")),
        NEXT_LINE(Regex("\n")),
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

            var pt: AdocToken = AdocToken.TITLE
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
                            s = //if (pt in listOf(AdocToken.MONO, AdocToken.MONO_BOLD, AdocToken.NORMAL, AdocToken.BOLD)) {
                                s.substring(m.value.length)
//                            } else {
//                                s.substring(m.value.length).trimStart(' ', '\t', '\r')
//                            }
                        }
                        pt = t
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
            var tableStart = false
            val initCells: MutableList<TxtRowCanvas> = mutableListOf()
            val initHeaders: MutableList<TxtHeaderCanvas> = mutableListOf()
            var currentIndent = 0
            var wasIndent = false
            while (it.hasNext()) {
                if (!wasIndent) {
                    currentIndent = 0
                } else {
                    wasIndent = false
                }
                val token = it.next()
                trace("token: [$token]")
                when (token.token) {
                    AdocToken.TITLE -> {}
                    AdocToken.ATTR -> {}
                    AdocToken.H2 -> {
                        currentText = H2Canvas("", currentIndent)
                        currentTextPosition = token.end
                        canvasDrawables.add(currentText)
                    }

                    AdocToken.H3 -> {
                        currentText = H3Canvas("", currentIndent)
                        currentTextPosition = token.end
                        canvasDrawables.add(currentText)
                    }

                    AdocToken.H4 -> {
                        currentText = H4Canvas("", currentIndent)
                        currentTextPosition = token.end
                        canvasDrawables.add(currentText)
                    }

                    AdocToken.B1 -> {
                        currentText = LiCanvas("", currentIndent)
                        currentTextPosition = token.end
                        canvasDrawables.add(currentText)
                    }

                    AdocToken.B2 -> {
                        currentText = Li2Canvas("", currentIndent)
                        currentTextPosition = token.end
                        canvasDrawables.add(currentText)
                    }

                    AdocToken.TABLE_START -> {
                        if (tableStart) {
                            canvasDrawables.add(CanvasTable(initHeaders, initCells))
                            tableStart = false
                        } else tableStart = true
//                        currentTable = if (currentTable == null) {
//                            CanvasTable()
//                        } else null
//                        if (currentTable != null) {
//                            canvasDrawables.add(currentTable)
//                        }
                    }
                    AdocToken.TABLE_COL -> {
                        for (txt in token.sequence.split('|')) {
                            if (txt.isNotEmpty()) {
                                val h = TxtHeaderCanvas(txt)
                                currentTextPosition = token.end
                                initHeaders.add(h)
                            }
                        }
                    }
                    AdocToken.TABLE_CELL -> {
                        val t = TxtRowCanvas()
                        currentText = t
                        currentTextPosition = token.end
                        initCells.add(t)
                    }
                    AdocToken.NEXT_DRAWABLE -> {
                        if (!tableStart) {
                            currentText = PCanvas("", currentIndent)
                        }
                        currentTextPosition = token.end
                    }

                    AdocToken.NEXT_LINE -> {}
                    AdocToken.MONO_BOLD -> {
                        if (canvasDrawables.isNotEmpty() && currentText != canvasDrawables.last())
                            canvasDrawables.add(currentText!!)
                        currentText?.addToTxtInit(token.sequence.substring(3,token.sequence.length - 3))
                        currentText?.addStyle(
                            CanvasStyle.Type.BOLD_MONOSPACED,
                            token.start - currentTextPosition,
                            token.end - currentTextPosition
                        )
                        currentTextPosition += 6
                    }

                    AdocToken.BOLD -> {
                        if (canvasDrawables.isNotEmpty() && currentText != canvasDrawables.last())
                            canvasDrawables.add(currentText!!)
                        currentText?.addToTxtInit(token.sequence.substring(2,token.sequence.length - 2))
                        currentText?.addStyle(
                            CanvasStyle.Type.BOLD,
                            token.start - currentTextPosition,
                            token.end - currentTextPosition
                        )
                        currentTextPosition += 4
                    }

                    AdocToken.MONO -> {
                        if (canvasDrawables.isNotEmpty() && currentText != canvasDrawables.last())
                            canvasDrawables.add(currentText!!)
                        currentText?.addToTxtInit(token.sequence.substring(1,token.sequence.length - 1))
                        currentText?.addStyle(
                            CanvasStyle.Type.MONOSPACED,
                            token.start - currentTextPosition,
                            token.end - currentTextPosition
                        )
                        currentTextPosition += 2
                    }

                    AdocToken.NORMAL -> {
                        if (canvasDrawables.isNotEmpty() && currentText != canvasDrawables.last() && !tableStart)
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
                    AdocToken.INDENT -> {
                        wasIndent = true
                        currentIndent ++
                    }
                }
            }

            return canvasDrawables
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

    fun dumpAsciidoc(): String

    fun reset()
}