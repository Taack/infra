package taack.ui.wysiwyg.contentEditableMono

import js.iterable.iterator
import taack.ui.base.element.Form
import web.cssom.ClassName
import web.dom.document
import web.dom.keyPressEvent
import web.events.EventHandler
import web.events.EventType
import web.events.addEventListener
import web.html.AutoCapitalize
import web.html.HTMLDivElement
import web.html.HTMLElement
import web.html.off
import web.window.window

class MainContentEditable(
    internal val embeddingForm: Form,
    private val divHolder: HTMLDivElement,
) {

    private val keyBuffer = StringBuilder()
    private var line = 0
    private var countLines = 0
    private val divLineNumber = document.createElement("div") as HTMLDivElement
    private val divLineNumberContainer = document.createElement("div") as HTMLDivElement

    private val divContent: HTMLDivElement = document.createElement("div") as HTMLDivElement

    class CmdLine {

        data class Span(val pattern: Regex, val replacement: String, val inlined: Boolean)

        enum class SpanStyle(val span: Span) {
            DOCUMENT(Span(Regex("^= .*"), "<span class='asciidoc-h1'>$0</span>", false)),
            TITLE1(Span(Regex("^== .*"), "<span class='asciidoc-h2'>$0</span>", false)),
            TITLE2(Span(Regex("^=== .*"), "<span class='asciidoc-h3'>$0</span>", false)),
            TITLE3(Span(Regex("^==== .*"), "<span class='asciidoc-h4'>$0</span>", false)),
            TITLE4(Span(Regex("^===== .*"), "<span class='asciidoc-h5'>$0</span>", false)),
//            LITERAL_PARAGRAPH(Span(Regex("^ .*"), "asciidoc-literal", false)),
            UNORDERED_LIST1(Span(Regex("^\\* (.*)"), "<span class='asciidoc-b1'>*</span> $1", false)),
            UNORDERED_LIST2(Span(Regex("^\\*\\* (.*)"), "<span class='asciidoc-b2'>**</span> $1", false)),
            UNORDERED_LIST3(Span(Regex("^\\*\\*\\* (.*)"), "<span class='asciidoc-b3'>***</span> $1", false)),
//            CONSTRAINED_BOLD(Span(Regex(".* \\*([^*]*)\\* "), "asciidoc-bold", true)),
//            CONSTRAINED_ITALIC(Span(Regex(" _([^_]*)_ "), "asciidoc-italic", true)),
//            CONSTRAINED_MONO(Span(Regex(" `([^`]*)` "), "asciidoc-mono", true)),
//            UNCONSTRAINED_BOLD(Span(Regex("[^\\*]\\*\\*([^*]*)\\*\\*[^\\*]"), "asciidoc-bold", true)),
//            UNCONSTRAINED_ITALIC(Span(Regex("[^_]__([^_]*)__[^_]"), "asciidoc-italic", true)),
//            UNCONSTRAINED_MONO(Span(Regex("[^`]``([^`]*)``[^`]"), "asciidoc-mono", true)),
//            HIGHLIGHT(Span(Regex("[^`]``([^`]*)``[^`]"), "asciidoc-highlight", true)),
//            UNDERLINE(Span(Regex(" \\[.underline]#([^#]*)# "), "asciidoc-underline", true)),
//            STRIKETHROUGH(Span(Regex(" \\[.line-through]#([^#]*)# "), "asciidoc-strikethrough", true)),
//            SMART_QUOTES(Span(Regex("\"`#([^\"`]*)`\""), "asciidoc-smart-quotes", true)),
//            APOSTROPHES(Span(Regex("'`#([^'`]*)`'"), "asciidoc-apostrophe", true)),
//            URL(Span(Regex("https://[^\\[]*\\[[^\\[]*]"), "asciidoc-url", true)),
        }
    }

    init {
        divLineNumberContainer.classList.add(ClassName("cm-gutter"), ClassName("cm-lineNumbers"))
        divLineNumber.classList.add(ClassName("cm-gutters"))
        divLineNumber.style.minHeight = "700px"
        divLineNumber.style.position = "sticky"
        divLineNumber.setAttribute("aria-hidden", "true")
        divLineNumber.appendChild(divLineNumberContainer)
        divContent.contentEditable = "true"
        divContent.autocorrect = false
        divContent.autocapitalize = AutoCapitalize.off
        divContent.contentEditable = "true"
        divContent.translate = false
        divContent.spellcheck = true
        divContent.style.tabSize = "4"
        divContent.setAttribute("aria-multiline", "true")
        divContent.setAttribute("role", "textbox")
        divContent.classList.add(ClassName("cm-content"))

        val divScroll = document.createElement("div") as HTMLDivElement
        divScroll.classList.add(ClassName("cm-scroller"))
        divScroll.setAttribute("tabindex", "-1")
        divScroll.appendChild(divLineNumber)
        divScroll.appendChild(divContent)

        divHolder.classList.add(ClassName("cm-editor"), ClassName("ͼ1"), ClassName("ͼ2"))
        divHolder.appendChild(divScroll)

        createCmdLine("<br>", 0)

        divContent.onkeyup = EventHandler { event ->
            if (event.key == "Enter") {
                createCmdLine(null, 0)

                val selection = window.getSelection()
                val range = selection?.getRangeAt(0)
                if (range != null) {
                    val element = range.commonAncestorContainer as HTMLElement
                    println("element.textContent: ${element.textContent}")
//                    element.innerHTML = asciidocToHtml(element.textContent)

//                    for (i in 0 until divContent.children.length) {
//                        // Ugly
//                        println("RDGSDFGSDFGsqdfsqd $i")
//                        if (divContent.children[i] == element) {
//                            println("RDGSDFGSDFG $i")
//                           val previousElement = divContent.children[i]
//                           previousElement.innerHTML = asciidocToHtml(previousElement.textContent) ?: "<br>"
//
//                        }
//                    }

                }
//                val element = createCmdLine("<br>")
//                val range = document.createRange()
//                val selection = window.getSelection()
//
//                range.setStart(element, 0)
//                range.setEnd(element, 0)
//                selection?.removeAllRanges()
//                selection?.addRange(range)
//                event.preventDefault()
//
//                countLines = 0
//
//                for (c in divContent.children.iterator()) {
//                    countLines ++
//                    c.innerHTML = asciidocToHtml(c.textContent)
//                    if (c.innerHTML.isEmpty()) c.innerHTML = "<br>"
//                }
            }
        }
    }


    fun asciidocToHtml(s: String?): String? {
        var matches = s
        if (matches != null)
            CmdLine.SpanStyle.entries.forEach { style ->
                println("match1: ${matches}")
                matches = matches!!.replace(style.span.pattern, style.span.replacement)
                println("match2: ${matches}")
            }
        return matches
    }

    fun createCmdLine(s: String?, index: Int) {
        divLineNumberContainer.innerHTML = ""
        if (line == 0) {
            val number: HTMLDivElement = document.createElement("div") as HTMLDivElement
            number.style.height = "0px"
            number.style.visibility = "hidden"
            number.style.pointerEvents = "none"
            number.textContent = "99"
            divLineNumberContainer.appendChild(number)
        }

        if (s != null) {
            val cmd: HTMLDivElement = document.createElement("div") as HTMLDivElement
            cmd.classList.add(ClassName("cm-line"))
            cmd.contentEditable = "true"

            cmd.innerHTML = s
            cmd.onchange = EventHandler { event ->
                println("onchange")
            }
            if (index == 0)
                divContent.appendChild(cmd)
            else
                divContent.insertBefore(divContent.children[index],cmd)
        }

        for (i in 0 until divContent.children.length) {
            line = i + 1
            val number: HTMLDivElement = document.createElement("div") as HTMLDivElement
            if (line == 1) number.style.marginTop = "4px"
            number.classList.add(ClassName("cm-gutterElement"))
            number.style.height = "22.4px"
            number.textContent = line.toString()
            divLineNumberContainer.appendChild(number)
        }
    }

}