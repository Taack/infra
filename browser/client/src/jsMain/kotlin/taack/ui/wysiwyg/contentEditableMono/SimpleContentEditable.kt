package taack.ui.wysiwyg.contentEditableMono

import js.array.asList
import js.buffer.AllowSharedBufferSource
import js.iterable.iterator
import js.typedarrays.toUint8Array
import org.w3c.fetch.Response
import taack.ui.base.Helper
import web.compression.CompressionFormat
import web.compression.DecompressionStream
import web.compression.deflate
import web.cssom.ClassName
import web.dom.ElementId
import web.dom.document
import web.encoding.TextDecoder
import web.events.EventHandler
import web.html.*
import web.keyboard.*
import web.window.window
import web.xhr.XMLHttpRequest
import kotlin.io.encoding.Base64
import kotlin.js.Date
import kotlin.math.max

class SimpleContentEditable(
    internal val text: HTMLTextAreaElement,
    divHolder: HTMLDivElement,
) {
    val styles: MutableMap<Span?, MutableList<Span>> = mutableMapOf()
    private var timeMs = Date.now()
    private var currentContext: Span? = null
    private var upLoadUrl: String? = null
    private var selectedElementPosition = 0
    private val divContent: HTMLDivElement = document.createElement("div") as HTMLDivElement
    private val divLineNumberContainer = document.createElement("div") as HTMLDivElement
    private val divLineNumber = document.createElement("div") as HTMLDivElement
    private val divAutocomplete = document.createElement("div") as HTMLDivElement
    private val currentLine: HTMLDivElement?
        get() {
            val n = window.getSelection()?.focusNode
            if (n is HTMLDivElement) return n
            else if (n is HTMLSpanElement) return n.parentElement as HTMLDivElement
            else return null
        }

    private var rescanContent = false

    enum class SpanMode {
        INLINED, INLINED_BREAK, START, CONTEXT_START, CONTEXT_END, START_CHAR_SEQ, META;

        companion object {
            fun from(v: String): SpanMode {
                if (v == "INLINED") return INLINED
                else if (v == "CONTEXT_START") return CONTEXT_START
                else if (v == "CONTEXT_END") return CONTEXT_END
                else if (v == "START") return START
                else if (v == "START_CHAR_SEQ") return START_CHAR_SEQ
                else if (v == "INLINED_BREAK") return INLINED_BREAK
                else return META
            }
        }
    }

    data class Span(
        val pattern: String, val className: String, val inlined: SpanMode
    )

    data class MenuEntry(
        val caption: String, val inlined: SpanMode, val decorator: String
    )

    data class AutocompleteEntry(
        val caption: String, val inlined: SpanMode, val insertText: String
    )

    private data class Mod(val letters: String, val pos: Int)

    private val undo = mutableListOf<Mod>()
    private val modifications = mutableListOf<Mod>()
    private var textContent: String
        get() {
            return text.textContent ?: "\n"
        }
        set(value) {
            undo.removeAll(undo)
            if (value.length >= textContent.length) {
                var i = 0
                val j = value.length - textContent.length
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
                val j = textContent.length - value.length
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


    fun trace(str: String) {
        val t = Date.now()
        println("${t - timeMs} $str")
        timeMs = t
    }

    init {
        val compressedOptions = text.getAttribute("editoroption")
        if (!compressedOptions.isNullOrEmpty()) {
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
        divAutocomplete.classList.add(ClassName("taack-autocomplete"))

        document.body.appendChild(divAutocomplete)
        divScroll.appendChild(divLineNumber)
        divScroll.appendChild(divContent)

        divHolder.classList.add(ClassName("cm-editor"), ClassName("ͼ1"), ClassName("ͼ2"))
        divHolder.appendChild(divScroll)

        divContent.onpaste = EventHandler { e ->
            Helper.onpaste(e, upLoadUrl!!, { xhr: XMLHttpRequest ->
                trace("onpast::url $upLoadUrl")
                if (currentLine != null) {
                    var txtToSave = ""
                    for (c in divContent.children.iterator()) {
                        if (currentLine == c) {
                            txtToSave += xhr.responseText + "\n"
                        } else txtToSave += c.textContent + "\n"
                    }
                    textContent = txtToSave

                    readTextarea()
                }
            })
            rescanContent = true
        }

        divContent.ondrop = EventHandler { e ->
            Helper.ondrop(e, upLoadUrl!!, { xhr: XMLHttpRequest ->
                trace("ondrop::url $upLoadUrl")
                if (currentLine != null) {
                    var txtToSave = ""
                    for (c in divContent.children.iterator()) {
                        if (currentLine == c) {
                            txtToSave += xhr.responseText + "\n"
                        } else txtToSave += c.textContent + "\n"
                    }
                    textContent = txtToSave

                    readTextarea()
                }
            })
        }

        divContent.onclick = EventHandler {
//            initSelection()
        }

        divContent.onkeydown = EventHandler { event ->
            trace("event.code: ${event.code}, event.ctrlKey: ${event.ctrlKey}")
            if (event.code == KeyCode.Space && event.ctrlKey) {
                trace("autocomplete ...")
                autocomplete()
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
                readTextarea()
                event.preventDefault()
                event.stopPropagation()
                return@EventHandler

            }
            trace("event.code: ${event.code} ends")
        }

        divContent.onkeyup = EventHandler { event ->
//            initSelection()
            trace("divContent.onkeyup ${event.key}")
            if (event.key.startsWith("Arrow")) {
                return@EventHandler
            }

            if (event.code == KeyCode.Enter) {
                createCmdLine(null, 0)
//                val range = selection?.rangeCount?.let { if (it > 0) selection?.getRangeAt(0) else null }
//                var e = range?.commonAncestorContainer
//
//                if (e != null) {
//                    if (e is HTMLSpanElement) {
//                        trace("Press enter, range = $range, reset style ${range?.commonAncestorContainer}")
//                        if (range?.commonAncestorContainer?.parentElement is HTMLDivElement) range.commonAncestorContainer.parentElement?.innerHTML =
//                            HtmlSource("<br>")
//                        else if (range?.commonAncestorContainer?.parentElement?.parentElement is HTMLDivElement) range.commonAncestorContainer.parentElement?.parentElement?.innerHTML =
//                            HtmlSource("<br>")
//                    }
//                }

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
                trace("textContent full $rescanContent")

                if (rescanContent) {
                    readTextarea()
                    rescanContent = false
                }
            }
        }
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

    fun autocomplete() {

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
        val e = currentLine
        if (e is HTMLDivElement) {
            val top = e.getBoundingClientRect().top
            val left = e.getBoundingClientRect().left
            val visibleElement = divContent.parentElement?.parentElement?.parentElement?.parentElement
            if (visibleElement != null) {
                val scrollTop = document.body.getBoundingClientRect().top
                val bottomVisible = visibleElement.getBoundingClientRect().bottom
                val topVisible = visibleElement.getBoundingClientRect().top
                trace("topVisible: $topVisible, bottomVisible: $bottomVisible, position: $selectedElementPosition, top: $top, left: $left")
                if (e.checkVisibility() && top > topVisible && top < bottomVisible) {
                    divAutocomplete.style.left = "${left + selectedElementPosition * 10}px"
                    divAutocomplete.style.top = "${top - scrollTop + 19}px"
                    divAutocomplete.innerHTML = HtmlSource("")


                    for (text in texts) {
                        val d = document.createElement("li") as HTMLLIElement
                        d.classList.add(ClassName("cmd-autocomplete"))
                        d.textContent = text
                        d.onclick = EventHandler {
                            e.textContent =
                                e.textContent?.substring(0, selectedElementPosition) + text + e.textContent?.substring(
                                    selectedElementPosition
                                )
                            selectedElementPosition = 1
                            divAutocomplete.style.display = "none"
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

//    fun initSelection() {
//        val winSelection = window.getSelection()
//        selection = winSelection
//        focus = selection?.focusOffset
//        selectedElement = selection?.focusNode
//
//        // position in chars in parent container
//        currentLine = currentLineComputed
//
//        if (currentLine != null) {
//            selectedElementPosition = 0
//            currentLinePosition = selectedElementPosition
//            for (child in currentLine!!.childNodes.iterator()) {
//                if (child == selectedElement) { // || child == selectedElement?.parentElement || child == selectedElement?.parentElement?.parentElement) {
//                    selectedElementPosition += focus ?: 0
//                    currentLinePosition += selectedElementPosition
//                    trace("child == selectedElement $focus $selectedElementPosition $child ${child is HTMLBRElement}")
//                    break
//                } else {
//                    trace("child == selectedElement ELSE for ($currentLine)")
//                    currentLinePosition += child.textContent?.length ?: 0
//                }
//            }
//        } else selectedElementPosition = focus ?: 0
//        trace("selectedElementPosition: $selectedElementPosition, currentLinePosition: $currentLinePosition")
//    }

//    fun contextMenu() {
//        val texts = mutableListOf< Pair<String, String>>(
//            "Bold",
//            "image:-name-[Sunset,200,100]",
//            "image::-name-[]",
//            "image::<name>[Sunset,200,100]",
//            "http://-url-[]",
//            "http://-url-[Sunset]",
//            "https://-url-[]",
//            "https://-url-[sunset]",
//        )
//
//    }

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
            var scanningStyle = false
            var scanningAutocomplete = false
            var scanningMenuEntry = false

            for (line in str.lines()) {
                if (line.contains('§')) {
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

                    if (line.startsWith("§§Style") || scanningStyle) {
                        scanningStyle = true

                        val s = Span(pattern, cn, inlined)

                        var l = styles[currentContext]
                        if (l == null) {
                            l = mutableListOf()
                        }
                        l.add(s)
                        styles[currentContext] = l
                        if (s.inlined == SpanMode.CONTEXT_START) currentContext = s
                        if (s.inlined == SpanMode.CONTEXT_END) currentContext = null
                    } else if (line.startsWith("§§Autocomplete") || scanningAutocomplete) {
                        scanningStyle = false
                        scanningAutocomplete = true

                    } else if (line.startsWith("§§MenuEntry") || scanningMenuEntry) {
                        scanningStyle = false
                        scanningAutocomplete = false
                        scanningMenuEntry = true
                    }
                } else if (line.isNotEmpty() && !line.contains('§')) {
                    trace("upLoadUrl = $line")
                    upLoadUrl = line
                }
            }

            readTextarea()
        }
    }

    fun escapeHtml(str: String?): String? {
        return str?.replace("&", "&amp;")?.replace("<", "&lt;")?.replace(">", "&gt;")
    }

    fun asciidocToHtml(e: HTMLDivElement) {
        trace("asciidocToHtml +++ ${window.getSelection()?.anchorOffset} ${window.getSelection()?.focusNode}")
        var txt = escapeHtml(e.textContent)
        if (txt?.isEmpty() == true) {
            e.innerHTML = HtmlSource("<br>")
            return
        }
        if (txt != null) {
            var hasStart: Span? = null
            var hasStartCharSeq: Span? = null
            var inlineMatchSequence: List<Pair<Span, MatchResult>> = mutableListOf<Pair<Span, MatchResult>>()
            val entries = styles[currentContext]!!
            var inContext = false

            for (entry in entries) {
                if (entry.inlined == SpanMode.CONTEXT_START) {
                    inContext = true
                    break
                }
                if (entry.inlined == SpanMode.CONTEXT_END) {
                    if (inContext && txt!!.startsWith(entry.pattern)) {
                        inContext = false
                        break
                    }
                }
                if (inContext) break

                if (entry.inlined == SpanMode.START_CHAR_SEQ) {
                    if (txt!!.startsWith(entry.pattern)) {
                        hasStartCharSeq = entry
                        txt = txt.substring(entry.pattern.length)
                    }
                }

                if (entry.inlined == SpanMode.START && hasStart == null) {
                    if (txt!!.startsWith(entry.pattern)) {
                        hasStart = entry
                        break
                    }
                } else if (entry.inlined == SpanMode.INLINED || entry.inlined == SpanMode.INLINED_BREAK || entry.inlined == SpanMode.CONTEXT_START || entry.inlined == SpanMode.CONTEXT_END) {
                    val pattern = entry.pattern
                    val regex = Regex(pattern)
                    if (regex.containsMatchIn(txt!!)) {
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
            var result: String = (if (hasStartCharSeq != null) {
                """<span class="${hasStartCharSeq.className}">${hasStartCharSeq.pattern}</span>"""
            } else "") as String
            val sorted = inlineMatchSequence.sortedBy { it.second.range.first }

            var i = 0
            for (c in sorted) {
                val spanStyle: Span = c.first
                val match: MatchResult = c.second
                val start = match.range.first
                val ends = match.range.endInclusive
                val replace =
                    """${match.groupValues[1]}<span class="${spanStyle.className}">${match.groupValues[2]}</span>${match.groupValues[3]}"""
                result += txt!!.substring(i..<start) + replace
                i = ends + 1
            }
            result += txt!!.substring(i)

            if (hasStart != null) {
                val cn = hasStart.className
                result = """<span class="$cn">$result</span>"""
            }

            if (e.innerHTML.toString() != result) {
                e.innerHTML = HtmlSource(result)
                window.getSelection()!!.removeAllRanges()
                val range = document.createRange()
                val n = e.childNodes.asList().last()
                if (n is HTMLSpanElement) {
                    val n2 = e.appendChild(document.createTextNode(""))
                    range.setStart(n2, 0)
                } else range.setStart(n, n.textContent!!.length)
                range.collapse(true)
                window.getSelection()!!.addRange(range)
            }
        }
        trace("asciidocToHtml --- ${window.getSelection()?.anchorOffset} ${window.getSelection()?.focusNode}")
//        TODO: window.getSelection()?.setPosition(e.childNodes[e.childElementCount], )
    }

    fun createCmdLine(s: String?, index: Int): HTMLDivElement? {
        divLineNumberContainer.innerHTML = HtmlSource("")

        val cmd: HTMLDivElement = document.createElement("div") as HTMLDivElement
        cmd.classList.add(ClassName("cm-line"))
        cmd.contentEditable = "true"

        cmd.innerHTML = HtmlSource(s ?: "<br>")
        cmd.onchange = EventHandler {
            trace("onchange")
        }
        if (index == 0) divContent.appendChild(cmd)
        else divContent.insertBefore(divContent.children[index], cmd)
        for (i in 1 until max(divContent.childElementCount + 1, 2)) {
            val number: HTMLDivElement = document.createElement("div") as HTMLDivElement
            if (i == 1) number.style.marginTop = "4px"
            number.classList.add(ClassName("cm-gutterElement"))
            number.textContent = i.toString()
            divLineNumberContainer.appendChild(number)
        }
        return cmd
    }

    fun readTextarea() {
        trace("readTextarea")
        divContent.innerHTML = HtmlSource("")
        if (text.textContent?.length == 0) {
            text.textContent = "\n"
        }

        text.textContent?.split("\n")?.forEach {
            if (it.isNotEmpty()) asciidocToHtml(createCmdLine(escapeHtml(it), 0)!!)
            else createCmdLine("", 0)
        }

    }

}