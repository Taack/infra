package taack.ui.wysiwyg.contentEditableMono

import js.iterable.iterator
import taack.ui.base.element.Form
import web.cssom.ClassName
import web.dom.Element
import web.dom.Node
import web.dom.Text
import web.dom.document
import web.events.EventHandler
import web.html.AutoCapitalize
import web.html.HTMLDivElement
import web.html.HTMLSpanElement
import web.html.off
import web.selection.Selection
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

        data class Span(val pattern: String, val replacement: String, val inlined: Boolean)

        enum class SpanStyle(val span: Span) {
            DOCUMENT(Span("= ", "asciidoc-h1", false)),
            TITLE1(Span("== ", "asciidoc-h2", false)),
            TITLE2(Span("=== ", "asciidoc-h3", false)),
            TITLE3(Span("==== ", "asciidoc-h4", false)),
            TITLE4(Span("===== ", "asciidoc-h5", false)),
            UNORDERED_LIST1(Span("* ", "asciidoc-b1", false)),
            UNORDERED_LIST2(Span("** ", "asciidoc-b2", false)),
            UNORDERED_LIST3(Span("*** ", "asciidoc-b3", false)),

            CONSTRAINED_BOLD(Span("(.* )(\\*[^*]*\\*)( .*)", "asciidoc-bold", true)),
//            LITERAL_PARAGRAPH(Span("^ .*", "asciidoc-literal", true)),
//            CONSTRAINED_ITALIC(Span(" _([^_]*)_ ", "asciidoc-italic", true)),
//            CONSTRAINED_MONO(Span(" `([^`]*)` ", "asciidoc-mono", true)),
//            UNCONSTRAINED_BOLD(Span("[^\\*]\\*\\*([^*]*)\\*\\*[^\\*]", "asciidoc-bold", true)),
//            UNCONSTRAINED_ITALIC(Span("[^_]__([^_]*)__[^_]", "asciidoc-italic", true)),
//            UNCONSTRAINED_MONO(Span("[^`]``([^`]*)``[^`]", "asciidoc-mono", true)),
//            HIGHLIGHT(Span("[^`]``([^`]*)``[^`]", "asciidoc-highlight", true)),
//            UNDERLINE(Span(" \\[.underline]#([^#]*)# ", "asciidoc-underline", true)),
//            STRIKETHROUGH(Span(" \\[.line-through]#([^#]*)# ", "asciidoc-strikethrough", true)),
//            SMART_QUOTES(Span("\"`#([^\"`]*)`\"", "asciidoc-smart-quotes", true)),
//            APOSTROPHES(Span("'`#([^'`]*)`'", "asciidoc-apostrophe", true)),
//            URL(Span("https://[^\\[]*\\[[^\\[]*]", "asciidoc-url", true)),
        }
    }

    var focus: Int? = 0
    var selection: Selection? = null
    var selectedElement: Node? = null

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

            if (event.key.startsWith("Arrow")) return@EventHandler

            if (event.key == "Enter") {
                createCmdLine(null, 0)
                val range = selection?.rangeCount?.let { if (it > 0) selection?.getRangeAt(0) else null }
                var e = range?.commonAncestorContainer
                if (e != null) {
                    if (e is HTMLSpanElement) {
                        println("Press enter, range = $range, reset style ${range?.commonAncestorContainer}")
                        (range?.commonAncestorContainer?.parentElement as HTMLDivElement?)?.innerHTML = "<br>"
                    }
                }

                return@EventHandler
            }
            selection = window.getSelection()
            focus = selection?.focusOffset
            for (c in divContent.children.iterator()) {
                asciidocToHtml(c as HTMLDivElement)
            }
            val range = selection?.rangeCount?.let { if (it > 0) selection?.getRangeAt(0) else null }
            selectedElement = range?.commonAncestorContainer

            if (selectedElement is Text) selectedElement = selectedElement?.parentElement

            if (focus != null && selectedElement != null && selection != null) {
                var out = "focus: $focus"
                if (selectedElement?.firstChild is HTMLSpanElement || selectedElement is HTMLDivElement) {
                    out += " selectedElement.innerHtml: ${(selectedElement!! as Element).innerHTML}"
                } else {
                    out += " selectedElement: ${selectedElement} selectedElement.textContent: ${selectedElement?.textContent}"
                }
                println(out)
            }

        }
    }

    fun asciidocToHtml(e: HTMLDivElement) {
        println("asciidocToHtml ${e.textContent}")
        var matches = e.textContent
        if (matches != null) {
            val isSelected = selectedElement == e
            for (entry in CmdLine.SpanStyle.entries) {
                if (!entry.span.inlined) {
                    if (matches!!.startsWith(entry.span.pattern)) {

                        var out = "Matches:  ${entry}, matches: $matches, e.innerHtml: ${e.innerHTML} [${isSelected}]"
                        val innerHTML = """<span class="${entry.span.replacement}">${matches}</span>"""

                        if (innerHTML != e.innerHTML) {
                            e.innerHTML = innerHTML
                            out += " innerHTML != e.innerHTML "

                            if (isSelected && focus != null) {
                                out += ", Set carret positoin => focus: $focus, e.children: ${e.children}"
                                selection?.setPosition(e.firstChild?.firstChild, focus!!)
                            }
                        }
                        println(out)
                        return
                    } else {

                    }
                } else {
                    val r = Regex(entry.span.pattern)
                    if (r.matches(matches as CharSequence)) {
                        var out = "matches in : $matches ($entry)\n"
                        matches = matches!!.replace(Regex(entry.span.pattern), """$1<span class="${entry.span.replacement}">$2</span>$3""")
                        out += "matches out: $matches ($entry)\n"
                        e.innerHTML = matches
                        if (isSelected && focus != null) {
                            out += "Set caret position => focus: $focus, e: $e, e.children.length: ${e.children.length}, selection?.focusOffset: ${selection?.focusOffset}\n"
                            out += "Iterate over children: ${e.childElementCount}\n"
                            var cursorPos = 0
                            for (c in e.children.iterator()) {
                                val txt = c.textContent!!
//                                out += "txt.length >= (focus!! - cursorPos - 1): ${txt.length} >= (${focus} - $cursorPos - 1)\n)"
//                                if (txt.length >= (focus!! - cursorPos - 1)) {
//                                    out += txt + " setting to  ${focus!! - cursorPos}\n"
//                                    selection?.setPosition(c, focus!! - cursorPos)
//                                    break
//                                }
                                cursorPos += txt.length
                                out += "$txt cursorPos: $cursorPos focus: $focus\n"
                            }
                            println(out)
                            val offset = if (focus!! > cursorPos) focus!! - cursorPos else focus!!
                            println ("offset: $offset")
                            println("selection?.getRangeAt(0): ${selection?.getRangeAt(0).toString()}")
                            println("selection?.getRangeAt(0)?.endOffset: ${selection?.getRangeAt(0)?.endOffset}")
                            println("selection?.getRangeAt(0)?.getClientRects: ${selection?.getRangeAt(0)?.getClientRects()}")
                            println("selection?.getRangeAt(0)?.getBoundingClientRect: ${selection?.getRangeAt(0)?.getBoundingClientRect()}")
                            println("selection?.getRangeAt(0)?.startOffset: ${selection?.getRangeAt(0)?.startOffset}")
                            selection?.setPosition(selection?.focusNode, selection?.focusOffset!!)
                            return
                        }
                        println(out)
                    } else {
                        val html = matches!!.replace(Regex(entry.span.pattern), """$1<span class="${entry.span.replacement}">$2</span>$3""")
                        if (html != e.textContent) {
                            e.innerHTML = html
                            selection?.setPosition(selection?.focusNode, selection?.focusOffset!!)
                        }
                    }
                }
            }
//            println("No matches found ${e}")
//            if (e.innerHTML != e.textContent && e.innerHTML != "<br>") {
//                println("HTML (${e.innerHTML}) != Txt (${e.textContent})")
//                e.innerHTML = e.textContent ?: "<br>"
//            }
        } else {
        }
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
                divContent.insertBefore(divContent.children[index], cmd)
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