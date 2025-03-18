package taack.ui.wysiwyg.parser

import taack.ui.base.Helper.Companion.trace
import taack.ui.wysiwyg.canvasMono.text.*

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
        var txt = ""
        while (it.hasNext()) {
            val token = it.next()
            trace("AsciidocParser::parse $token")
            when (token.token) {
                AdocToken.TITLE -> {
                    textOutline = token
                }

                AdocToken.ATTR -> TODO()
                AdocToken.INNER_BLOCK_DELIM -> TODO()
                AdocToken.BLOCK_DELIM -> TODO()
                AdocToken.H4 -> {
                    textOutline = token
                }

                AdocToken.H3 -> {
                    textOutline = token
                }

                AdocToken.H2 -> {
                    textOutline = token
                }

                AdocToken.B1 -> {
                    textOutline = token
                }

                AdocToken.B2 -> {
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
                    txt += token.sequence
                }

                AdocToken.MONO -> {
                    txt += token.sequence
                }

                AdocToken.NEXT_DRAWABLE -> {
                    textOutlines.add(createOutline(textOutline, txt))
                    textOutline = null
                    txt = ""
                }

                AdocToken.NEXT_LINE -> {
                    textOutlines.add(createOutline(textOutline, txt))
                    textOutline = null
                    txt = ""
                }

                AdocToken.NORMAL -> {
                    txt += token.sequence
                }

                AdocToken.OTHER -> TODO()
                AdocToken.ERROR -> TODO()
            }
        }
        textOutlines.add(createOutline(textOutline, txt))
        return textOutlines
    }

    private fun createOutline(
        textOutline: TokenInfo?,
        initText: String
    ): CanvasText {
        val text = if (textOutline == null) initText else textOutline.sequence + ' ' + initText
        trace("AsciidocParser::createOutline $text")
        when (textOutline?.token) {
            AdocToken.TITLE -> {
                return TitleCanvas(text)
            }

            AdocToken.H4 -> {
                return H4Canvas(text)
            }

            AdocToken.H3 -> {
                return H3Canvas(text)
            }

            AdocToken.H2 -> {
                return H2Canvas(text)
            }

            AdocToken.B1 -> {
                return LiCanvas(text)
            }

            AdocToken.B2 -> {
                return Li2Canvas(text)
            }

            else -> {
                return PCanvas(text)
            }
        }
        return PCanvas(text)
    }
}