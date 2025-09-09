package taack.ui.wysiwyg.contentEditableMono

import js.buffer.AllowSharedBufferSource
import js.date.Date
import js.iterable.iterator
import js.typedarrays.toUint8Array
import org.w3c.dom.HTMLBRElement
import org.w3c.fetch.Response
import taack.ui.base.Helper
import web.compression.CompressionFormat
import web.compression.DecompressionStream
import web.compression.deflate
import web.cssom.ClassName
import web.dom.ElementId
import web.dom.Node
import web.dom.document
import web.encoding.TextDecoder
import web.events.EventHandler
import web.html.*
import web.keyboard.Enter
import web.keyboard.KeyCode
import web.keyboard.KeyV
import web.keyboard.KeyW
import web.keyboard.Space
import web.selection.Selection
import web.window.window
import web.xhr.XMLHttpRequest
import kotlin.io.encoding.Base64

class MainContentEditable(
    internal val text: HTMLTextAreaElement,
    divHolder: HTMLDivElement,
) {
    private var line = 0
    private var rescanContent = false
    private val divAutocomplete = document.createElement("div") as HTMLDivElement
    private val divLineNumber = document.createElement("div") as HTMLDivElement
    private val divLineNumberContainer = document.createElement("div") as HTMLDivElement
    private val divContent: HTMLDivElement = document.createElement("div") as HTMLDivElement

    private data class Mod(val letters: String, val pos: Int)

    private val modifications = mutableListOf<Mod>()
    private val undo = mutableListOf<Mod>()
    private var textContent: String
        get() {
            return text.textContent ?: "\n"
        }
        set(value) {
            undo.removeAll(undo)
            if (value.length >= textContent.length) {
                var i = 0
                var j = value.length - textContent.length
                for (c in value) {
                    if (textContent[i] != c) {
                        var seq = ""
                        for (p in 0..<j) {
                            seq += value[i + p]
                        }
                        modifications.add(Mod(seq, i))
                        break
                    }
                    i++
                }
            } else {
                var i = 0
                var j = textContent.length - value.length
                for (c in textContent) {
                    if (value[i] != c) {
                        var seq = ""
                        for (p in 0..j) {
                            seq += textContent[i + p]
                        }
                        modifications.add(Mod(seq, -i))
                        break
                    }
                    i++
                }
            }
            text.textContent = value
        }

    data class Span(
        val pattern: String, val className: String, val inlined: SpanMode
    )

    private var upLoadUrl: String? = null

    val styles: MutableMap<Span?, MutableList<Span>> = mutableMapOf()

    var focus: Int? = 0
    var currentLine: HTMLDivElement? = currentLineComputed
    val currentLineComputed: HTMLDivElement?
        get() {
            if (selectedElement is HTMLDivElement && selectedElement!!.textContent == "") {
                return selectedElement as HTMLDivElement
            } else if (selectedElement?.parentElement is HTMLDivElement) {
                return selectedElement?.parentElement as HTMLDivElement
            } else if (selectedElement?.parentElement?.parentElement is HTMLDivElement) {
                return selectedElement?.parentElement?.parentElement as HTMLDivElement
            } else if (selectedElement?.parentElement?.parentElement?.parentElement is HTMLDivElement) {
                return selectedElement?.parentElement?.parentElement?.parentElement as HTMLDivElement
            }
            return null
        }
    var selection: Selection? = null
    var selectedElement: Node? = null
    var selectedElementPosition = 0
    var currentLinePosition = 0
    private var timeMs = Date.now()

    private var currentContext: Span? = null

    enum class SpanMode {
        INLINED, INLINED_BREAK, START, CONTEXT_START, CONTEXT_END, META;

        companion object {
            fun from(v: String): SpanMode {
                if (v == "INLINED") return INLINED
                else if (v == "CONTEXT_START") return CONTEXT_START
                else if (v == "CONTEXT_END") return CONTEXT_END
                else if (v == "START") return START
                else if (v == "INLINED_BREAK") return INLINED_BREAK
                else return META
            }
        }
    }

    fun trace(str: String) {
        val t = Date.now()
        println("${t - timeMs} $str")
        timeMs = t
    }

    fun undoLetter() {
        val m = modifications.removeLast()
        undo.add(m)
        trace("undo ${m.letters} ${m.pos} ${text.textContent?.length}")
        if (m.pos >= 0) {
            text.textContent = text.textContent?.removeRange(m.pos, m.pos + m.letters.length)
        } else {
            text.textContent = text.textContent?.substring(0, -m.pos) + m.letters + text.textContent?.substring(-m.pos)
        }
    }

    fun redoLetter() {
        val m = undo.removeLast()
        modifications.add(m)
        trace("redo ${m.letters} ${m.pos} ${text.textContent?.length}")
        if (m.pos >= 0) {
            text.textContent = text.textContent?.substring(0, m.pos) + m.letters + text.textContent?.substring(m.pos)
        } else {
            text.textContent = text.textContent?.removeRange(-m.pos, -m.pos + m.letters.length)
        }
    }

    fun initSelection() {
        val winSelection = window.getSelection()
        selection = winSelection
        focus = selection?.focusOffset
        selectedElement = selection?.focusNode

        // position in chars in parent container
        currentLine = currentLineComputed

        if (currentLine != null) {
            selectedElementPosition = 0
            currentLinePosition = selectedElementPosition
            for (child in currentLine!!.childNodes.iterator()) {
                if (child == selectedElement) { // || child == selectedElement?.parentElement || child == selectedElement?.parentElement?.parentElement) {
                    selectedElementPosition += focus ?: 0
                    currentLinePosition += selectedElementPosition
                    trace("child == selectedElement $focus $selectedElementPosition $child ${child is HTMLBRElement}")
                    break
                } else {
                    trace("child == selectedElement ELSE for ($currentLine)")
                    currentLinePosition += child.textContent?.length ?: 0
                }
            }
        } else selectedElementPosition = focus ?: 0
        trace("selectedElementPosition: $selectedElementPosition, currentLinePosition: $currentLinePosition")
    }

    fun autocomplete(e: HTMLDivElement?) {

        val texts = mutableListOf<String>(
            "image:-name-[]",
            "image:-name-[Sunset,200,100]",
            "image::-name-[]",
            "image::<name>[Sunset,200,100]",
            "http://-url-[]",
            "http://-url-[Sunset]",
            "https://-url-[]",
            "https://-url-[sunset]",
        )

        val top = e?.getBoundingClientRect()?.top
        val left = e?.getBoundingClientRect()?.left
        if (top != null && left != null) {
            val visibleElement = divContent.parentElement?.parentElement?.parentElement?.parentElement
            if (visibleElement != null) {
                val scrollTop = document.body.getBoundingClientRect().top
                val bottomVisible = visibleElement.getBoundingClientRect().bottom
                val topVisible = visibleElement.getBoundingClientRect().top
                trace("topVisible: $topVisible, bottomVisible: $bottomVisible, position: $selectedElementPosition, top: $top, left: $left")
                if (e.checkVisibility() && top > topVisible && top < bottomVisible) {
                    divAutocomplete.style.left = "${left + selectedElementPosition * 10}px"
                    divAutocomplete.style.top = "${top - scrollTop + 19}px"
                    divAutocomplete.innerHTML = ""


                    for (text in texts) {
                        val d = document.createElement("div") as HTMLDivElement
                        d.classList.add(ClassName("cmd-autocomplete"))
                        d.textContent = text
                        d.onclick = EventHandler {
                            e.textContent =
                                e.textContent?.substring(0, selectedElementPosition) + text + e.textContent?.substring(
                                    selectedElementPosition
                                )
                            selectedElementPosition = 1
                            divAutocomplete.style.display = "none"
                            selection?.setPosition(e, selectedElementPosition)
                        }
                        divAutocomplete.appendChild(d)
                    }

                    divAutocomplete.style.display = "block"
                } else {
                    divAutocomplete.style.display = "none"
                }
            }
        }
    }

    fun repairSelection() {
        trace("repairSelection currentLine: $currentLine (${currentLine?.textContent}), ${currentLine?.childElementCount}, ${currentLine?.childNodes?.length}")
        if (currentLine != null) {
            var currentPosition = 0
            for (child in currentLine!!.childNodes.iterator()) {

                val childLength = child.textContent?.length
                trace("child: $child (${child.textContent}), childLength = $childLength, currentPosition: $currentPosition, selectedElementPosition: $selectedElementPosition, currentLinePosition: $currentLinePosition)")

                childLength?.plus(currentPosition)?.let {
                    if (it >= currentLinePosition) {
                        val o = if (child.textContent?.length == 0) 0 else 1
                        selection?.setPosition(child, o)
                        return
                    }
                }
                currentPosition += childLength ?: 0
            }
        }
    }

    init {
        val compressedOptions = text.getAttribute("editoroption")
        if (compressedOptions != null && compressedOptions.isNotEmpty()) {
            decompress(compressedOptions)
        }

        divLineNumberContainer.classList.add(ClassName("cm-gutter"), ClassName("cm-lineNumbers"))
        divLineNumber.classList.add(ClassName("cm-gutters"))
        divLineNumber.style.minHeight = "240px"
        divLineNumber.style.position = "sticky"
        divLineNumber.setAttribute("aria-hidden", "true")
        divLineNumber.appendChild(divLineNumberContainer)
        divContent.contentEditable = "true"
        divContent.autocorrect = false
        divContent.autocapitalize = AutoCapitalize.off
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
        divAutocomplete.style.width = "256px"
        divAutocomplete.style.border = "black 1px solid"
        divAutocomplete.style.padding = "10px"

        document.body.appendChild(divAutocomplete)
        divScroll.appendChild(divLineNumber)
        divScroll.appendChild(divContent)

        divHolder.classList.add(ClassName("cm-editor"), ClassName("ͼ1"), ClassName("ͼ2"))
        divHolder.appendChild(divScroll)

        divContent.onpaste = EventHandler { e ->
            Helper.onpaste(e, upLoadUrl!!, { xhr: XMLHttpRequest ->
                if (currentLine != null) {
                    var txtToSave = ""
                    for (c in divContent.children.iterator()) {
                        if (currentLine == c) {
                            txtToSave += xhr.responseText + "\n"
                        } else txtToSave += c.textContent + "\n"
                    }
                    textContent = txtToSave

                    rescanTextarea()
                }
            })
            rescanContent = true
        }

        divContent.ondrop = EventHandler { e ->
            Helper.ondrop(e, upLoadUrl!!, { xhr: XMLHttpRequest ->
                if (currentLine != null) {
                    var txtToSave = ""
                    for (c in divContent.children.iterator()) {
                        if (currentLine == c) {
                            txtToSave += xhr.responseText + "\n"
                        } else txtToSave += c.textContent + "\n"
                    }
                    currentLine = null
                    textContent = txtToSave

                    rescanTextarea()
                }
            })
        }

        divContent.onclick = EventHandler {
            initSelection()
        }

        divContent.onkeydown = EventHandler { event ->
            trace("event.code: ${event.code}, event.ctrlKey: ${event.ctrlKey}")
            if (event.code == KeyCode.Space && event.ctrlKey) {
                trace("autocomplete ...")
                autocomplete(currentLine)
                event.preventDefault()
                event.stopPropagation()
                return@EventHandler
            } else if (event.code == KeyCode.KeyV && event.ctrlKey) {
                trace("past something ...")
//                event.preventDefault()
//                event.stopPropagation()
                return@EventHandler
            } else if (event.code == KeyCode.KeyW && event.ctrlKey) {
                if (event.shiftKey) {
                    trace("redo ...")
                    if (undo.isNotEmpty())
                        redoLetter()
                } else {
                    trace("undo ... $modifications")
                    if (modifications.isNotEmpty())
                        undoLetter()
                }
                rescanTextarea()
                event.preventDefault()
                event.stopPropagation()
                return@EventHandler

            }
            trace("event.code: ${event.code} ends")
        }

        divContent.onkeyup = EventHandler { event ->
            initSelection()
            trace("divContent.onkeyup ${event.key}")
            if (event.key.startsWith("Arrow")) {
                return@EventHandler
            }

            if (event.code == KeyCode.Enter) {
                createCmdLine(null, 0)
                val range = selection?.rangeCount?.let { if (it > 0) selection?.getRangeAt(0) else null }
                var e = range?.commonAncestorContainer

                if (e != null) {
                    if (e is HTMLSpanElement) {
                        trace("Press enter, range = $range, reset style ${range?.commonAncestorContainer}")
                        if (range?.commonAncestorContainer?.parentElement is HTMLDivElement) range.commonAncestorContainer.parentElement?.innerHTML =
                            "<br>"
                        else if (range?.commonAncestorContainer?.parentElement?.parentElement is HTMLDivElement) range.commonAncestorContainer.parentElement?.parentElement?.innerHTML =
                            "<br>"
                    }
                }

                return@EventHandler
            }

            if (!(event.code == KeyCode.KeyW && event.ctrlKey)) {
                var txtToSave = ""
                for (c in divContent.children.iterator()) {
                    if (currentLine == null || currentLine == c) {
                        asciidocToHtml(c as HTMLDivElement)
                    }
                    txtToSave += c.textContent + "\n"
                }
                textContent = txtToSave
                trace("textContent full")

                if (rescanContent) {
                    rescanTextarea()
                    rescanContent = false
                }
            }
        }
    }

    fun escapeHtml(str: String?): String? {
        return str?.replace("&", "&amp;")?.replace("<", "&lt;")?.replace(">", "&gt;")
    }

    fun asciidocToHtml(e: HTMLDivElement) {
        trace("asciidocToHtml +++")
        val txt = escapeHtml(e.textContent)
        if (txt?.isEmpty() == true) {
            e.innerHTML = "<br>"
            return
        }
        if (txt != null) {
            var hasStart: Span? = null
            var inlineMatchSequence: List<Pair<Span, MatchResult>> = mutableListOf<Pair<Span, MatchResult>>()
            val entries = styles[currentContext]!!

            for (entry in entries) {
                if (entry.inlined == SpanMode.START && hasStart == null) {
                    if (txt.startsWith(entry.pattern)) {
                        hasStart = entry
                    }
                } else if (entry.inlined == SpanMode.INLINED || entry.inlined == SpanMode.INLINED_BREAK || entry.inlined == SpanMode.CONTEXT_START || entry.inlined == SpanMode.CONTEXT_END) {
                    val pattern = entry.pattern
                    val regex = Regex(pattern)
                    if (regex.containsMatchIn(txt)) {
                        for (s in regex.findAll(txt)) {
                            inlineMatchSequence = inlineMatchSequence.plusElement(Pair(entry, s))
                        }
                        if (entry.inlined == SpanMode.INLINED_BREAK) break
                        if (entry.inlined == SpanMode.CONTEXT_START && currentContext == null) {
                            currentContext = entry
                            break
                        } else if (entry.inlined == SpanMode.CONTEXT_END && currentContext != null) {
                            currentContext = null
                            break
                        }
                    }
                }
            }
            var result = ""
            val sorted = inlineMatchSequence.sortedBy { it.second.range.first }

            var i = 0
            for (c in sorted) {
                val spanStyle: Span = c.first
                val match: MatchResult = c.second
                val start = match.range.first
                val ends = match.range.endInclusive
                val replace =
                    """${match.groupValues[1]}<span class="${spanStyle.className}">${match.groupValues[2]}</span>${match.groupValues[3]}"""
                result += txt.substring(i..start - 1) + replace
                i = ends + 1
            }
            result += txt.substring(i)

            if (hasStart != null) {
                val cn = hasStart.className
                result = """<span class="$cn">$result</span>"""
            }

            if (e.innerHTML != result) {
                trace("selection?.focusOffset: ${selection?.focusOffset} ${e.innerHTML}||${result}")
                trace("selection?.focusNode: ${selection?.focusNode}")


                e.innerHTML = result
                if (!rescanContent) repairSelection()
            }
        }
        trace("asciidocToHtml ---")
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
//            number.style.height = "22.4px"
            number.textContent = line.toString()
            divLineNumberContainer.appendChild(number)
        }

        if (s != null) {
            val cmd: HTMLDivElement = document.createElement("div") as HTMLDivElement
            cmd.classList.add(ClassName("cm-line"))
            cmd.contentEditable = "true"

            cmd.innerHTML = s
            cmd.onchange = EventHandler {
                trace("onchange")
            }
            if (index == 0) divContent.appendChild(cmd)
            else divContent.insertBefore(divContent.children[index], cmd)
            return cmd
        }
        return null
    }

    fun decompress(str: String) {
        val b = Base64.decode(str)
        val uint8Array = b.toUint8Array()
        val ds = DecompressionStream(CompressionFormat.deflate)
        val writer = ds.writable.getWriter()
        writer.writeAsync(uint8Array)
        writer.closeAsync()

        val res = Response(ds.readable).arrayBuffer()
        res.then { arrayBuffer ->
            val decoder = TextDecoder()
            val str = decoder.decode(arrayBuffer as AllowSharedBufferSource)
            trace(str)//arrayBuffer.unsafeCast<String>())
            for (line in str.lines()) {
                if (line.startsWith("§§")) {
                    var pos1 = 2
                    var pos2 = line.indexOf("§", pos1)
                    val cn = line.substring(pos1, pos2)
                    pos1 = pos2
                    pos2 = line.indexOf("§", ++pos1)
                    val pattern = line.substring(pos1, pos2)
                    pos1 = pos2
                    pos2 = line.indexOf("§", ++pos1)
                    val inlined = SpanMode.from(line.substring(pos1, pos2))
                    println("cn: $cn, pattern: $pattern, inline: $inlined")

                    val s = Span(pattern, cn, inlined)

                    var l = styles[currentContext]
                    if (l == null) {
                        l = mutableListOf()
                    }
                    l.add(s)
                    styles[currentContext] = l
                    if (s.inlined == SpanMode.CONTEXT_START) currentContext = s
                    if (s.inlined == SpanMode.CONTEXT_END) currentContext = null
                } else if (line.isNotEmpty() && !line.contains('§')) {
                    trace("upLoadUrl = $line")
                    upLoadUrl = line
                }
            }

            rescanTextarea()
        }
    }

    fun rescanTextarea() {
        trace("rescanTextarea")
        divContent.innerHTML = ""
        if (text.textContent?.length == 0) {
            text.textContent = "\n"
        }

        text.textContent?.split("\n")?.forEach {
            if (it.isNotEmpty()) asciidocToHtml(createCmdLine(escapeHtml(it), 0)!!)
            else createCmdLine("<br>", 0)
        }

    }
}