package taack.ui.canvas

import taack.ui.base.Helper.Companion.trace
import taack.ui.canvas.item.CanvasImg
import taack.ui.canvas.item.CanvasLink
import taack.ui.canvas.table.CanvasTable
import taack.ui.canvas.table.TxtHeaderCanvas
import taack.ui.canvas.table.TxtRowCanvas
import taack.ui.canvas.text.*
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
        IMAGE(Regex("^image::[^:|*`]+\\[\\]")),
        LINK(Regex("^link:[^:|*`]+\\[.+,download\\]")),
//        IMAGE(Regex("^image::[^:|*`\n\\[]+")),
        IMAGE_INLINE(Regex("image:[^:|*`]+")),
        TABLE_START(Regex("^\\|===")),
//        TABLE_COL(Regex("^\\|[^*`=\n][^|*`\n]+\\|([^|*`\n])+")),
        TABLE_COL(Regex("^\\|[^*`=\n][^|*`\n]+(\\|([^|*`\n])+)+")),
        TABLE_CELL(Regex("^\\|")),
//        MONO_BOLD(Regex("^`\\*\\*([^*`\n]*)\\*\\*`")),
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

    companion object {
        fun dumpAsciidoc(mainCanvas: MainCanvas): String {
            val out = StringBuilder()
//            out.append("= Title\n")
//            out.append(":doctype: book\n")
//            out.append(":toc: left\n")
//            out.append(":toc-title: Table of Contents of {doctitle}\n")
//            out.append(":toclevels: 2\n")
//            out.append(":sectnums: 2\n")
//            out.append(":sectnumlevels: 2\n")
            out.append("\n")
            val drawables = mainCanvas.drawables
            var previousCitationNumber = 0
            drawables.forEach {
                out.append("\n")
                if (it.citationNumber > previousCitationNumber) {
                    if (previousCitationNumber == 0)
                        out.append("\n____")

                    for (m in 1..<it.citationNumber) {
                        out.append("\n____")
                        for (n in 1..m) {
                            out.append("__")
                        }
                        out.append("\n")
                    }
                } else if (it.citationNumber < previousCitationNumber) {
                    if (it.citationNumber == 0)
                        out.append("\n____")
                    for (m in 1..<previousCitationNumber) {
                        out.append("\n____")
                        for (n in 1..m) {
                            out.append("__")
                        }
                        out.append("\n")
                    }
                }
                out.append(it.dumpAsciidoc())
                previousCitationNumber = it.citationNumber
            }
            out.append("\n")
            return out.toString()
        }

        fun readAsciidoc(mainCanvas: MainCanvas): List<ICanvasDrawable> {
            val canvasDrawables = mutableListOf<ICanvasDrawable>()
            val tokens = mutableListOf<TokenInfo>()
            var start = 0
            var end = 0

            var s = mainCanvas.textarea.value.trim()

            var pt: AdocToken = AdocToken.TITLE
            while (s.isNotEmpty()) {
                var match = false
                for (t in AdocToken.entries) {
                    val m = t.regex.find(s)
                    if (m != null) {
                        trace("ICanvasDrawable::readAsciidoc ${m.groups} ${m.range} $t")
                        if (m.value.isNotEmpty() && m.range.first == 0) {
                            match = true
                            end += m.value.length
                            tokens.add(TokenInfo(m.value, t, start, end))
                            start += m.value.length
                            s = if (pt in listOf(AdocToken.MONO, /*AdocToken.MONO_BOLD,*/ AdocToken.NORMAL, AdocToken.BOLD)) {
                                s.substring(m.value.length)
                            } else {
                                s.substring(m.value.length).trimStart(' ', '\t', '\r')
                            }
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

            trace("ICanvasDrawable::readAsciidoc $tokens")

            val it = tokens.iterator()
            var currentText: CanvasText? = null
            var currentTextPosition = 0
            var tableStart = false
            val initCells: MutableList<TxtRowCanvas> = mutableListOf()
            val initHeaders: MutableList<TxtHeaderCanvas> = mutableListOf()
            var currentIndent = 0
            var wasIndent = false
            var wasInBlock = false
            while (it.hasNext()) {
//                if (!wasIndent) {
////                    currentIndent = 0
//                } else {
//                    wasIndent = false
//                }
                val token = it.next()
                trace("token: [$token]")
                val id = (mainCanvas.embeddingForm.f.elements.namedItem("id") as HTMLInputElement).value.toLong()
                val controller = (mainCanvas.embeddingForm.f.elements.namedItem("originController") as HTMLInputElement).value
                var varName = mainCanvas.textarea.name
                if (varName.contains('.'))
                    varName = varName.substring(0, varName.lastIndexOf('.'))
                val action = "downloadBin${varName.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }}Files"

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
                        val t = TxtRowCanvas("")
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

                    AdocToken.NEXT_LINE -> {
                        currentText = null
                    }
//                    AdocToken.MONO_BOLD -> {
//                        if (canvasDrawables.isNotEmpty() && currentText != canvasDrawables.last())
//                            canvasDrawables.add(currentText!!)
//                        currentText?.addToTxtInit(token.sequence.substring(3,token.sequence.length - 3))
//                        currentText?.addStyle(
//                            TextStyle.BOLD_MONOSPACED,
//                            token.start - currentTextPosition,
//                            token.end - currentTextPosition
//                        )
//                        currentTextPosition += 6
//                    }

                    AdocToken.BOLD -> {
                        if (canvasDrawables.isNotEmpty() && currentText != canvasDrawables.last())
                            canvasDrawables.add(currentText!!)
                        currentText?.addToTxtInit(token.sequence.substring(2,token.sequence.length - 2))
                        currentText?.addStyle(
                            TextStyle.BOLD,
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
                            TextStyle.MONOSPACED,
                            token.start - currentTextPosition,
                            token.end - currentTextPosition
                        )
                        currentTextPosition += 2
                    }

                    AdocToken.NORMAL -> {
                        if (currentText != null && (canvasDrawables.isNotEmpty() && currentText != canvasDrawables.last()) && !tableStart)
                            canvasDrawables.add(currentText)
                        else if (canvasDrawables.isEmpty() || currentText == null) {
                            currentText = PCanvas("", currentIndent)
                            canvasDrawables.add(currentText)
                        }

                        currentText.addToTxtInit(token.sequence)
                        currentText.addStyle(
                            TextStyle.NORMAL,
                            token.start - currentTextPosition,
                            token.end - currentTextPosition
                        )
                    }

                    AdocToken.ERROR -> {
                        currentText = PCanvas("ERROR: $token", currentIndent)
                        canvasDrawables.add(currentText)
}
                    AdocToken.OTHER -> {}
//                    AdocToken.INDENT -> {
//                        wasIndent = true
//                        currentIndent ++
//                    }

                    AdocToken.IMAGE -> {
                        val fileName = token.sequence.substring("image::".length, token.sequence.length - 2)
                        canvasDrawables.add(CanvasImg("/$controller/$action/$id?path=$fileName", fileName, 0))
                        currentTextPosition = token.end
                    }
                    AdocToken.IMAGE_INLINE -> {

                    }

                    AdocToken.BLOCK_DELIM -> {
                        if (!wasInBlock) {
                            wasInBlock = true
                            currentIndent = 1
                        } else {
                            wasInBlock = false
                            currentIndent = 0
                        }
                    }
                    AdocToken.INNER_BLOCK_DELIM -> {
                        val indent = 1 + (token.sequence.length - 4) / 2
                        if (currentIndent == indent) {
                            currentIndent--
                        } else currentIndent = indent
                    }

                    AdocToken.FIG -> {}
                    AdocToken.LINK -> {
                        val fileName = token.sequence.substring("link:".length, token.sequence.indexOf('['))
                        canvasDrawables.add(CanvasLink("/$controller/$action/$id?path=$fileName", fileName, 0))
                        currentTextPosition = token.end
                    }
                }
            }
            currentText = PCanvas("", currentIndent)
            canvasDrawables.add(currentText)
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