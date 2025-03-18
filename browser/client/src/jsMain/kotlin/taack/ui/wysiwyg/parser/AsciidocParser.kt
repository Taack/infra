package taack.ui.wysiwyg.parser

import taack.ui.wysiwyg.canvasMono.text.CanvasText
import taack.ui.wysiwyg.canvasMono.text.H2Canvas
import taack.ui.wysiwyg.canvasMono.text.H3Canvas
import taack.ui.wysiwyg.canvasMono.text.H4Canvas
import taack.ui.wysiwyg.canvasMono.text.Li2Canvas
import taack.ui.wysiwyg.canvasMono.text.LiCanvas
import taack.ui.wysiwyg.canvasMono.text.PCanvas

class AsciidocParser {
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

    fun parse(html: String): List<CanvasText> {
        val textOutlines = mutableListOf<CanvasText>()
        val tokens = mutableListOf<TokenInfo>()
        var start = 0
        var end = 0

        var s = html.trim()

        var pt: AdocToken = AdocToken.TITLE
        while (s.isNotEmpty()) {
            var match = false
            for (t in AdocToken.entries) {
                val m = t.regex.find(s)
                if (m != null) {
                    if (m.value.isNotEmpty() && m.range.first == 0) {
                        match = true
                        end += m.value.length
                        tokens.add(TokenInfo(m.value, t, start, end))
                        start += m.value.length
                        s = if (pt in listOf(
                                AdocToken.MONO, /*AdocToken.MONO_BOLD,*/
                                AdocToken.NORMAL,
                                AdocToken.BOLD
                            )
                        ) {
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

        val it = tokens.iterator()
        var textOutline: TokenInfo? = null
        var textOutlineContent: String? = null

        while (it.hasNext()) {
            val token = it.next()
            when (token.token) {
                AdocToken.TITLE -> TODO()
                AdocToken.ATTR -> TODO()
                AdocToken.INNER_BLOCK_DELIM -> TODO()
                AdocToken.BLOCK_DELIM -> TODO()
                AdocToken.H4 -> {
                    textOutline = token
                }
                AdocToken.H3 ->  {
                    textOutline = token
                }
                AdocToken.H2 ->  {
                    textOutline = token
                }
                AdocToken.B1 ->  {
                    textOutline = token
                }
                AdocToken.B2 ->  {
                    textOutline = token
                }
                AdocToken.FIG -> TODO()
                AdocToken.IMAGE -> TODO()
                AdocToken.LINK -> TODO()
                AdocToken.IMAGE_INLINE -> TODO()
                AdocToken.TABLE_START -> TODO()
                AdocToken.TABLE_COL -> TODO()
                AdocToken.TABLE_CELL -> TODO()
                AdocToken.BOLD -> {
                    textOutlineContent += token.sequence
                }
                AdocToken.MONO -> {
                    textOutlineContent += token.sequence
                }
                AdocToken.NEXT_DRAWABLE -> TODO()
                AdocToken.NEXT_LINE -> {
                    if (!textOutlineContent.isNullOrBlank()) {
                        if (textOutline != null) {
                            textOutlines.add(createOutline(textOutline, textOutlineContent))
                        }
                        textOutlineContent = null
                        textOutline = null
                    }
                }
                AdocToken.NORMAL -> {
                    textOutlineContent += token.sequence
                }
                AdocToken.OTHER -> TODO()
                AdocToken.ERROR -> TODO()
            }
        }
        return textOutlines
    }

    fun createOutline(
        textOutline: TokenInfo,
        initText: String
    ): CanvasText {
        val text = textOutline.sequence + ' ' + initText
        when (textOutline.token) {
            AdocToken.H4 -> {
                return H4Canvas(text)
            }
            AdocToken.H3 ->  {
                return H3Canvas(text)
            }
            AdocToken.H2 ->  {
                return H2Canvas(text)
            }
            AdocToken.B1 ->  {
                return LiCanvas(text)
            }
            AdocToken.B2 ->  {
                return Li2Canvas(text)
            }

            else -> {}
        }
        return PCanvas(text)
    }
}