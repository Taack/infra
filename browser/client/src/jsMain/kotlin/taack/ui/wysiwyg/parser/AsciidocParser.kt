package taack.ui.wysiwyg.parser

import taack.ui.wysiwyg.structure.*

class AsciidocParser : IParser {
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

    override fun parse(html: String, factory: IFactory): List<TextOutline> {
        val textOutlines = mutableListOf<TextOutline>()
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
        var textOutline: TextOutline? = null
        var inlineFaces = mutableListOf<InlineFace>()
        var textOutlineContent: String? = null

        while (it.hasNext()) {
            val token = it.next()
            when (token.token) {
                AdocToken.TITLE -> TODO()
                AdocToken.ATTR -> TODO()
                AdocToken.INNER_BLOCK_DELIM -> TODO()
                AdocToken.BLOCK_DELIM -> TODO()
                AdocToken.H4 -> {
                    textOutline = H4Text()
                }
                AdocToken.H3 ->  {
                    textOutline = H3Text()
                }
                AdocToken.H2 ->  {
                    textOutline = H2Text()
                }
                AdocToken.B1 ->  {
                    textOutline = B1Text()
                }
                AdocToken.B2 ->  {
                    textOutline = B2Text()
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
                    inlineFaces.add(factory.createInline(BoldFace(token.start, token.end)))
                }
                AdocToken.MONO -> {
                    textOutlineContent += token.sequence
                    inlineFaces.add(factory.createInline(MonoFace(token.start, token.end)))
                }
                AdocToken.NEXT_DRAWABLE -> TODO()
                AdocToken.NEXT_LINE -> {
                    if (!textOutlineContent.isNullOrBlank()) {
                        if (textOutline != null) {
                            textOutlines.add(factory.createOutline(textOutline, textOutlineContent, inlineFaces))
                        } else {
                            textOutlines.add(factory.createOutline(PText(), textOutlineContent, inlineFaces))
                        }
                        inlineFaces = mutableListOf()
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
}