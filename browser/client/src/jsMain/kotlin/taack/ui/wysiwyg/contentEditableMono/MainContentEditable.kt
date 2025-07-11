package taack.ui.wysiwyg.contentEditableMono

import js.iterable.iterator
import web.cssom.ClassName
import web.dom.ElementId
import web.dom.Node
import web.dom.document
import web.events.EventHandler
import web.html.AutoCapitalize
import web.html.HTMLDivElement
import web.html.HTMLSpanElement
import web.html.off
import web.selection.Selection
import web.window.window
import web.html.HTMLTextAreaElement

class MainContentEditable(
    internal val text: HTMLTextAreaElement,
    private val divHolder: HTMLDivElement,
) {

    private var line = 0
    private val divAutocomplete = document.createElement("div") as HTMLDivElement
    private val divLineNumber = document.createElement("div") as HTMLDivElement
    private val divLineNumberContainer = document.createElement("div") as HTMLDivElement

    private val divContent: HTMLDivElement = document.createElement("div") as HTMLDivElement

    class CmdLine {

        data class Span(
            val pattern: String,
            val className: String,
            val inlined: Boolean,
            val delimiter: Boolean = false
        )


        enum class SpanStyle(val span: Span) {
            DOCUMENT(Span("= ", "asciidoc-h1", false)),
            TITLE1(Span("== ", "asciidoc-h2", false)),
            TITLE2(Span("=== ", "asciidoc-h3", false)),
            TITLE3(Span("==== ", "asciidoc-h4", false)),
            TITLE4(Span("===== ", "asciidoc-h5", false)),
            UNORDERED_LIST1(Span("* ", "asciidoc-b1", false)),
            UNORDERED_LIST2(Span("** ", "asciidoc-b2", false)),
            UNORDERED_LIST3(Span("*** ", "asciidoc-b3", false)),

            UNCONSTRAINED_BOLD(Span("([^*]?)(\\*\\*[^*]*\\*\\*)([^*]?)", "asciidoc-bold", true, true)),
            UNCONSTRAINED_ITALIC(Span("([^_]?)(__[^_]*__)([^_]?)", "asciidoc-italic", true)),
            UNCONSTRAINED_MONO(Span("[^`]``([^`]*)``[^`]", "asciidoc-mono", true)),
            CONSTRAINED_BOLD(Span("([^\\w\\d*])(\\*[^*]+\\*)([^\\w\\d*]?)", "asciidoc-bold", true, true)),
//            LITERAL_PARAGRAPH(Span("^ .*", "asciidoc-literal", true)),
            CONSTRAINED_ITALIC(Span("([^\\w\\d_])(_[^_]+_)([^\\w\\d_]?)", "asciidoc-italic", true, true)),
            CONSTRAINED_MONO(Span(" `([^`]*)` ", "asciidoc-mono", true)),
            HIGHLIGHT(Span("[^`]``([^`]*)``[^`]", "asciidoc-highlight", true)),
            UNDERLINE(Span("([^\\w\\d]?)(\\[.underline\\]#[^#]*#)([^\\w\\d]?)", "asciidoc-underline", true, true)),
            STRIKETHROUGH(Span("([^\\w\\d]?)(\\[.line-through\\]#[^#]*#)([^\\w\\d]?)", "asciidoc-line-through", true)),
            SMART_QUOTES(Span("\"`#([^\"`]*)`\"", "asciidoc-smart-quotes", true)),
            APOSTROPHES(Span("'`#([^'`]*)`'", "asciidoc-apostrophe", true)),
            URL(Span("([^\\w\\d]?)(http[s]?://[^[]*\\[[^\\]]*\\])([^\\w\\d]?)", "asciidoc-url", true)),
        }
    }

    var focus: Int? = 0
    var currentLine: HTMLDivElement? = currentLineComputed
    val currentLineComputed: HTMLDivElement?
        get() {
            if (selectedElement?.parentElement is HTMLDivElement) {
                return selectedElement?.parentElement as HTMLDivElement
            } else if(selectedElement?.parentElement?.parentElement is HTMLDivElement) {
                return selectedElement?.parentElement ?.parentElement as HTMLDivElement
            } else if (selectedElement?.parentElement?.parentElement?.parentElement is HTMLDivElement) {
                return selectedElement?.parentElement?.parentElement?.parentElement as HTMLDivElement
            }
            return null
        }
    var selection: Selection? = null
    var selectedElement: Node? = null
    var position = 0


    fun initSelection() {
        selection = window.getSelection()
        focus = selection?.focusOffset
        selectedElement = selection?.focusNode

        println("selection: $selection")
        println("selectedElement: $selectedElement, parent: ${selectedElement?.parentElement}")
        println("focus: $focus")

        // position in chars in parent container
        currentLine = currentLineComputed

        println("currentLine: $currentLine (${currentLine?.innerHTML})")
        if (currentLine != null) {
            position = 0
            for(child in currentLine!!.childNodes.iterator()) {
                println("child: $child (${child.textContent})")

                if (child == selectedElement || child == selectedElement?.parentElement || child == selectedElement?.parentElement?.parentElement) {
                    position += focus ?: 0
                    println("child == selectedElement $focus $position")
                    break
                } else {
                    position += child.textContent?.length ?: 0
                }
            }
        } else position = focus ?: 0
        println("position: $position")
    }

    fun autocomplete(e: HTMLDivElement?) {

        val top = e?.getBoundingClientRect()?.top
        if (top != null) {
            val visibleElement = divContent.parentElement?.parentElement?.parentElement?.parentElement
            if (visibleElement != null) {

                val bottomVisible = visibleElement.getBoundingClientRect().bottom
                val topVisible = visibleElement.getBoundingClientRect().top
                println("topVisible: $topVisible, bottomVisible: $bottomVisible")
                if (e.checkVisibility() && top > topVisible && top < bottomVisible) {
                    divAutocomplete.style.left = "${e.getBoundingClientRect().left}px"
                    divAutocomplete.style.top = "${e.getBoundingClientRect().top}px"
                    divAutocomplete.innerHTML = """ <span id='PopUpText'>TEXT</span> """
                    divAutocomplete.style.display = "block"
                } else {
                    divAutocomplete.style.display = "none"
                }
            }
        }



    }

    fun repairSelection() {
        println("repairSelection currentLine: $currentLine (${currentLine?.textContent}), ${currentLine?.childElementCount}, ${currentLine?.childNodes?.length}")
        if (currentLine != null) {
            var currentPosition = 0
            for (child in currentLine!!.childNodes.iterator()) {
                println("child: $child (${child.textContent})")

                val childLength = child.textContent?.length

                childLength?.plus(currentPosition)?.let {
                    if (it >= position) {
                        selection?.setPosition(child, 1)
                        return
                    }
                }
                currentPosition += childLength ?: 0
            }
        }
    }

    init {
        divLineNumberContainer.classList.add(ClassName("cm-gutter"), ClassName("cm-lineNumbers"))
        divLineNumber.classList.add(ClassName("cm-gutters"))
        divLineNumber.style.minHeight = "240px"
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

        divAutocomplete.id = ElementId("${text.name}-autocomplete")
        divAutocomplete.style.display = "none"
        divAutocomplete.style.backgroundColor = "rgb(200,100,100)"
        divAutocomplete.style.position = "absolute"
//        divAutocomplete.style.left = "100px"
//        divAutocomplete.style.top = "50px"
        divAutocomplete.style.textAlign = "justify"
        divAutocomplete.style.fontSize = "12px"
        divAutocomplete.style.width = "135px"
        divAutocomplete.style.border = "black 1px solid"
        divAutocomplete.style.padding = "10px"

        document.body.appendChild(divAutocomplete)
        divScroll.appendChild(divLineNumber)
        divScroll.appendChild(divContent)

        divHolder.classList.add(ClassName("cm-editor"), ClassName("ͼ1"), ClassName("ͼ2"))
        divHolder.appendChild(divScroll)

        text.textContent?.split("\n")?.forEach {
            if (it.isNotEmpty())
                asciidocToHtml(createCmdLine(it, 0)!!)
            else createCmdLine("<br>", 0)
        }

        divContent.onclick = EventHandler { e ->
            initSelection()
        }

        divContent.onkeydown = EventHandler { event ->
            println("event.key: ${event.key}, event.ctrlKey: ${event.ctrlKey}")
            if (event.key == " " && event.ctrlKey) {
                println("autocomplete ...")
                autocomplete(currentLine)
                event.preventDefault()
                event.stopPropagation()
                return@EventHandler
            }
        }

        divContent.onkeyup = EventHandler { event ->
            initSelection()
            if (event.key.startsWith("Arrow")) {
                return@EventHandler
            }

            if (event.key == "Enter") {
                createCmdLine(null, 0)
                val range = selection?.rangeCount?.let { if (it > 0) selection?.getRangeAt(0) else null }
                var e = range?.commonAncestorContainer

                if (e != null) {
                    if (e is HTMLSpanElement) {
                        println("Press enter, range = $range, reset style ${range?.commonAncestorContainer}")
                        if (range?.commonAncestorContainer?.parentElement is HTMLDivElement)
                            range.commonAncestorContainer.parentElement?.innerHTML = "<br>"
                        else if (range?.commonAncestorContainer?.parentElement?.parentElement is HTMLDivElement)
                            range.commonAncestorContainer.parentElement?.parentElement?.innerHTML = "<br>"
                    }
                }

                return@EventHandler
            }
            text.textContent = ""
            for (c in divContent.children.iterator()) {
                asciidocToHtml(c as HTMLDivElement)
                text.textContent += c.textContent + "\n"
            }
        }
    }

    fun asciidocToHtml(e: HTMLDivElement) {
        val txt = e.textContent
        println("txt: $txt")
        if (txt?.isEmpty() == true) {
            e.innerHTML = "<br>"
            return
        }
        if (txt != null) {
            var hasStart: CmdLine.SpanStyle? = null
            var inlineMatchSequence: List<Pair<CmdLine.SpanStyle, MatchResult>> = mutableListOf<Pair<CmdLine.SpanStyle, MatchResult>>()
            for (entry in CmdLine.SpanStyle.entries) {
                if (!entry.span.inlined && hasStart == null) {
                    if (txt.startsWith(entry.span.pattern)) {
                        println("Has Prefix")
                        hasStart = entry
                    }
                } else if (entry.span.inlined) {
                    val pattern = entry.span.pattern
                    println("Pattern: $pattern")
                    val regex = Regex(pattern)
                    if (regex.containsMatchIn(txt)) {
                        for (s in regex.findAll(txt)) {
                            inlineMatchSequence = inlineMatchSequence.plusElement(Pair(entry, s))
                        }
                    }
                }
            }
            var result = ""
            val sorted = inlineMatchSequence.sortedBy { it.second.range.first }

            var i = 0
            for (c in sorted) {
                val spanStyle: CmdLine.SpanStyle = c.first
                val match: MatchResult = c.second
                val start = match.range.first
                val ends = match.range.endInclusive
                val replace = """${match.groupValues[1]}<span class="${spanStyle.span.className}">${match.groupValues[2]}</span>${match.groupValues[3]}"""
                result += txt.substring(i..start - 1) + replace
                i = ends + 1
            }
            println("result: $result, i: $i, txt.length: ${txt.length}")
            result += txt.substring(i)

            println("result: $result")

            if (hasStart != null) {
                val cn = hasStart.span.className
                result = """<span class="${cn}">$result</span>"""
            }

            if (e.innerHTML != result) {
                println("selection?.focusOffset: ${selection?.focusOffset}")
                println("selection?.focusNode: ${selection?.focusNode}")


                e.innerHTML = result
                repairSelection()
            }
        }
    }

    fun createCmdLine(s: String?, index: Int): HTMLDivElement? {
        divLineNumberContainer.innerHTML = ""
        if (line == 0) {
            val number: HTMLDivElement = document.createElement("div") as HTMLDivElement
            number.style.height = "0px"
            number.style.visibility = "hidden"
            number.style.pointerEvents = "none"
            number.textContent = "99"
            divLineNumberContainer.appendChild(number)
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
            return cmd
        }
        return null
    }

}