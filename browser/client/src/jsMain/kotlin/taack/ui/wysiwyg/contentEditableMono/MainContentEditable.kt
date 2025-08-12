package taack.ui.wysiwyg.contentEditableMono

import js.buffer.AllowSharedBufferSource
import js.date.Date
import js.iterable.iterator
import js.typedarrays.toUint8Array
import org.w3c.dom.HTMLBRElement
import org.w3c.fetch.Response
import web.compression.CompressionFormat
import web.compression.DecompressionStream
import web.compression.deflate
import web.cssom.ClassName
import web.dom.ElementId
import web.dom.Node
import web.dom.document
import web.encoding.TextDecoder
import web.events.EventHandler
import web.form.FormData
import web.html.*
import web.http.POST
import web.http.RequestMethod
import web.keyboard.KeyCode
import web.selection.Selection
import web.window.window
import web.xhr.XMLHttpRequest
import kotlin.io.encoding.Base64

class MainContentEditable(
    internal val text: HTMLTextAreaElement,
    private val divHolder: HTMLDivElement,
) {
    private var line = 0
    private var rescanContent = false
    private val divAutocomplete = document.createElement("div") as HTMLDivElement
    private val divLineNumber = document.createElement("div") as HTMLDivElement
    private val divLineNumberContainer = document.createElement("div") as HTMLDivElement

    private val divContent: HTMLDivElement = document.createElement("div") as HTMLDivElement

    data class Span(
        val pattern: String, val className: String, val inlined: Boolean, val delimiter: Boolean = false
    )

    private var upLoadUrl: String? = null

    val styles: MutableList<Span> = mutableListOf()

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
    var position = 0

    private var timeMs = Date.now()

    fun trace(str: String) {
        val t = Date.now()
        println("${t - timeMs} $str")
        timeMs = t
    }

    fun initSelection() {
        val winSelection = window.getSelection()
        selection = winSelection
        focus = selection?.focusOffset
        selectedElement = selection?.focusNode

        trace("selection: $selection, currentLine: $currentLine (${currentLine?.className} ${currentLine?.innerHTML})")
        trace("selectedElement: $selectedElement, parent: ${selectedElement?.parentElement}, focus: $focus")

        // position in chars in parent container
        currentLine = currentLineComputed

        if (currentLine != null) {
            position = 0
            for (child in currentLine!!.childNodes.iterator()) {

                if (child == selectedElement || child == selectedElement?.parentElement || child == selectedElement?.parentElement?.parentElement) {
                    position += focus ?: 0
                    trace("child == selectedElement $focus $position $child ${child is HTMLBRElement}")
                    break
                } else if (child is HTMLSpanElement) {
                    position += child.textContent?.length ?: 0
                }
            }
        } else position = focus ?: 0
        trace("position: $position")
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
                trace("topVisible: $topVisible, bottomVisible: $bottomVisible, position: $position, top: $top, left: $left")
                if (e.checkVisibility() && top > topVisible && top < bottomVisible) {
                    divAutocomplete.style.left = "${left + position * 10}px"
                    divAutocomplete.style.top = "${top - scrollTop + 19}px"
                    divAutocomplete.innerHTML = ""


                    for (text in texts) {
                        val d = document.createElement("div") as HTMLDivElement
                        d.classList.add(ClassName("cmd-autocomplete"))
                        d.textContent = text
                        d.onclick = EventHandler {
                            e.textContent =
                                e.textContent?.substring(0, position) + text + e.textContent?.substring(position)
                            position = 1
                            divAutocomplete.style.display = "none"
                            selection?.setPosition(e, position)
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
                trace("child: $child (${child.textContent})")

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
        divAutocomplete.style.width = "256px"
        divAutocomplete.style.border = "black 1px solid"
        divAutocomplete.style.padding = "10px"

        document.body.appendChild(divAutocomplete)
        divScroll.appendChild(divLineNumber)
        divScroll.appendChild(divContent)

        divHolder.classList.add(ClassName("cm-editor"), ClassName("ͼ1"), ClassName("ͼ2"))
        divHolder.appendChild(divScroll)

        divContent.onpaste = EventHandler { e ->
            e.clipboardData?.files?.length?.let {
                if (it > 0) {
                    for (f in e.clipboardData!!.files) {
                        trace("f: $f")
                    }
                    e.preventDefault()
                    e.stopPropagation()
                }
            }
            trace("divContent.onpaste $e ${e.target} ${e.clipboardData?.items}")
            rescanContent = true
        }

        divContent.ondrop = EventHandler { e ->
            trace("ondrop")
            if (upLoadUrl != null) e.dataTransfer?.files?.length?.let {
                if (it > 0) {
                    val fd = FormData()
                    for (f in e.dataTransfer!!.files) {
                        trace("f: $f")
                        trace("f: ${f.name}")

                        fd.append("filePath", f)
                    }
                    val xhr = XMLHttpRequest()
                    xhr.onloadend = EventHandler {
                        if (currentLine != null) {
                            currentLine?.innerText += xhr.responseText
                        }
                    }
                    xhr.open(RequestMethod.POST, upLoadUrl!!)
                    xhr.send(fd)
                    e.preventDefault()
                    e.stopPropagation()
                }
            }
        }

        divContent.onclick = EventHandler { e ->
            initSelection()
        }

        divContent.onkeydown = EventHandler { event ->
            trace("event.code: ${event.code}, event.ctrlKey: ${event.ctrlKey}")
            if (event.code == "Space" as KeyCode && event.ctrlKey) {
                trace("autocomplete ...")
                autocomplete(currentLine)
                event.preventDefault()
                event.stopPropagation()
                return@EventHandler
            } else if (event.code == "KeyV" as KeyCode && event.ctrlKey) {
                trace("past something ...")
//                event.preventDefault()
//                event.stopPropagation()
                return@EventHandler
            }
            trace("event.code: ${event.code} ends")
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
                        trace("Press enter, range = $range, reset style ${range?.commonAncestorContainer}")
                        if (range?.commonAncestorContainer?.parentElement is HTMLDivElement) range.commonAncestorContainer.parentElement?.innerHTML =
                            "<br>"
                        else if (range?.commonAncestorContainer?.parentElement?.parentElement is HTMLDivElement) range.commonAncestorContainer.parentElement?.parentElement?.innerHTML =
                            "<br>"
                    }
                }

                return@EventHandler
            }

            text.textContent = ""
            var txtToSave = ""
            trace("textContent empty")
            for (c in divContent.children.iterator()) {
                if (currentLine == null || currentLine == c) {
                    asciidocToHtml(c as HTMLDivElement)
                }
                txtToSave += c.textContent + "\n"
            }
            text.textContent = txtToSave
            trace("textContent full")

            if (rescanContent) {
                rescanTextarea()
                rescanContent = false
            }
        }
    }

    fun asciidocToHtml(e: HTMLDivElement) {
        trace("asciidocToHtml +++")
        val txt = e.textContent
        if (txt?.isEmpty() == true) {
            e.innerHTML = "<br>"
            return
        }
        if (txt != null) {
            var hasStart: Span? = null
            var inlineMatchSequence: List<Pair<Span, MatchResult>> = mutableListOf<Pair<Span, MatchResult>>()
            for (entry in styles) {
                if (!entry.inlined && hasStart == null) {
                    if (txt.startsWith(entry.pattern)) {
                        hasStart = entry
                    }
                } else if (entry.inlined) {
                    val pattern = entry.pattern
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
                    val inlined = line.substring(pos1, pos2) == "true"
                    pos1 = pos2
                    pos2 = line.indexOf("§", ++pos1)
                    val delimiter = line.substring(pos1, pos2) == "true"
                    trace("cn: $cn, pattern: $pattern, inline: $inlined, delimiter: $delimiter")
                    styles.add(Span(pattern, cn, inlined, delimiter))
                } else if (line.isNotEmpty()) {
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
            if (it.isNotEmpty()) asciidocToHtml(createCmdLine(it, 0)!!)
            else createCmdLine("<br>", 0)
        }

    }
}