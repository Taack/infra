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
import web.dom.Node
import web.dom.Text
import web.dom.document
import web.encoding.TextDecoder
import web.events.EventHandler
import web.html.*
import web.keyboard.*
import web.mutation.MutationObserver
import web.mutation.MutationObserverInit
import web.mutation.MutationRecord
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
    val autocompletes: MutableMap<SpanMode?, MutableList<AutocompleteEntry>> = mutableMapOf()
    val menues: MutableMap<Span?, MutableList<MenuEntry>> = mutableMapOf()
    private var timeMs = Date.now()
    private var currentContext: Span? = null
    private var upLoadUrl: String? = null
    private var lsp: LspClient? = null
    private var diagnostics: Array<dynamic> = emptyArray()
    private var completionRequest = 0
    private var mutationObserver: MutationObserver? = null
    private val selectedElementPosition: Int
        get() {
            return window.getSelection()?.anchorOffset ?: 0
        }
    private val divContent: HTMLDivElement = document.createElement("div") as HTMLDivElement
    private val divLineNumberContainer = document.createElement("div") as HTMLDivElement
    private val divLineNumber = document.createElement("div") as HTMLDivElement
    private val divAutocomplete = document.createElement("div") as HTMLDivElement
    private val currentLine: HTMLDivElement?
        get() {
            if (currentNode is HTMLDivElement) return currentNode as HTMLDivElement
            else if (currentNode?.parentElement is HTMLDivElement) return currentNode?.parentElement as HTMLDivElement
            else if (currentNode?.parentElement?.parentElement is HTMLDivElement) return currentNode!!.parentElement!!.parentElement as HTMLDivElement
            else return null
        }

    private val currentNode: Node?
        get() {
            return window.getSelection()?.focusNode
        }

    private var rescanContent = false

    enum class SpanMode {
        INLINED, INLINED_BREAK, START, CONTEXT_START, CONTEXT_END, START_CHAR_SEQ, META;

        companion object {
            fun from(v: String?): SpanMode {
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
        val caption: String, val decorator: String, val inlined: SpanMode
    )

    data class AutocompleteEntry(
        val caption: String, val insertText: String, val inlined: SpanMode
    )

    /**
     * A state of the text, with the caret, for undo and redo.
     */
    private data class Snapshot(val text: String, val line: Int, val character: Int)

    private val undoStack = mutableListOf<Snapshot>()
    private val redoStack = mutableListOf<Snapshot>()
    private var lastCaret = Pair(0, 0)
    private var lastEditTime = 0.0
    private var lastEditGrowing: Boolean? = null
    private var restoring = false

    private var textContent: String
        get() {
            return text.textContent ?: "\n"
        }
        set(value) {
            val old = text.textContent ?: ""
            if (value != old && !restoring) {
                // Consecutive keystrokes of the same kind (typing or erasing) are undone together
                val now = Date.now()
                val growing = value.length > old.length
                if (undoStack.isEmpty() || now - lastEditTime > 1000 || growing != lastEditGrowing) {
                    undoStack.add(Snapshot(old, lastCaret.first, lastCaret.second))
                    if (undoStack.size > 200) undoStack.removeAt(0)
                }
                redoStack.clear()
                lastEditTime = now
                lastEditGrowing = growing
            }
            text.textContent = value
            caretPosition()?.let { lastCaret = it }
            lsp?.change(value)
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
        val lspUrl = text.getAttribute("editorlspurl")
        if (!lspUrl.isNullOrEmpty()) {
            lsp = LspClient(lspUrl) {
                diagnostics = it
                showDiagnostics()
            }
            lsp!!.open(textContent)
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
//        divContent.setAttribute("aria-multiline", "false")
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
            // Caret of the text before this keystroke, saved with it in the undo history
            caretPosition()?.let { lastCaret = it }
            // AltGr is reported as Ctrl+Alt on some platforms and types characters
            val shortcut = (event.ctrlKey && !event.altKey) || event.metaKey
            if (!shortcut && replaceMultilineSelection(event.key)) {
                event.preventDefault()
                event.stopPropagation()
                return@EventHandler
            }
            if (event.code == KeyCode.Space && event.ctrlKey) {
                trace("autocomplete ...")
                autocomplete()
                event.preventDefault()
                event.stopPropagation()
            } else if (event.code == KeyCode.Escape) {
                divAutocomplete.style.display = "none"
            } else if (event.code == KeyCode.KeyV && event.ctrlKey) {
                trace("past something ...")
                return@EventHandler
            } else if (isUndoRedo(event.code, event.ctrlKey || event.metaKey)) {
                // The browser undo stack is unusable: lines are rewritten on each keystroke
                if (event.code == KeyCode.KeyY || event.shiftKey) redo() else undo()
                event.preventDefault()
                event.stopPropagation()
                return@EventHandler
            }
            trace("event.code: ${event.code} ends")
        }

        divContent.onkeyup = EventHandler { event ->
            trace("divContent.onkeyup ${event.key}")
            if (normalizeLines()) return@EventHandler
            if (event.key.startsWith("Arrow")) {
                return@EventHandler
            }

            if (event.code == KeyCode.Enter || event.code == KeyCode.NumpadEnter) {
                // Record the line break on its own, the undo history needs the caret before it
                var txtToSave = ""
                for (c in divContent.children.iterator()) txtToSave += c.textContent + "\n"
                textContent = txtToSave
                createLineNumbers()
                showDiagnostics()
                return@EventHandler
            }

            if (!isUndoRedo(event.code, event.ctrlKey || event.metaKey)) {
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

        val mutationObserver = MutationObserver({ m: Array<out MutationRecord>, o: MutationObserver ->
            val mutation = m.last()
            val an = mutation.addedNodes[0]
            if (an is HTMLDivElement) updateSelectionNewLine(an)
            else {
                mutation.removedNodes[0]
            }
            currentNode
        })
        val options = MutationObserverInit()
        options.childList = true
        mutationObserver.observe(divContent, options)
        this.mutationObserver = mutationObserver
        readTextarea()
    }

    fun updateSelection(e: HTMLDivElement, node: Node?, p: Int, i: Int) {
        trace("updateSelection n: $node, p: $p, i: $i")
        window.getSelection()!!.removeAllRanges()
        val range = document.createRange()
        if (node != null) {
            if (e.childNodes.length >= i) {
                e.childNodes[i]?.textContent?.length?.let {
                    if (it > p) {
                        val cn = if (e.childNodes[i] is Text) e.childNodes[i] else e.childNodes[i].childNodes[0]
                        range.setStart(cn, p)
                        range.collapse(true)
                        window.getSelection()!!.addRange(range)
                        return
                    }
                }
            }
        }
        val n = e.childNodes.asList().last()
        if (n is HTMLSpanElement) {
            val n2 = e.appendChild(document.createTextNode(""))
            range.setStart(n2, 0)
        } else range.setStart(n, n.textContent!!.length)
        range.collapse(true)
        window.getSelection()!!.addRange(range)

    }

    fun updateSelectionNewLine(e: HTMLDivElement) {
        trace("updateSelectionNewLine $e")
        window.getSelection()!!.removeAllRanges()
        val range = document.createRange()
        range.setStart(e, 0)
        range.collapse(true)
        window.getSelection()!!.addRange(range)
    }

    /**
     * Ctrl+Z undoes, Ctrl+Shift+Z and Ctrl+Y redo. Ctrl+W (Ctrl+Shift+W to redo) is kept for compatibility,
     * although Chrome reserves it to close the tab.
     */
    private fun isUndoRedo(code: KeyCode, ctrl: Boolean): Boolean =
        ctrl && (code == KeyCode.KeyZ || code == KeyCode.KeyY || code == KeyCode.KeyW)

    fun undo() {
        val s = undoStack.removeLastOrNull() ?: return
        val caret = caretPosition() ?: lastCaret
        redoStack.add(Snapshot(textContent, caret.first, caret.second))
        restore(s)
    }

    fun redo() {
        val s = redoStack.removeLastOrNull() ?: return
        val caret = caretPosition() ?: lastCaret
        undoStack.add(Snapshot(textContent, caret.first, caret.second))
        restore(s)
    }

    private fun restore(s: Snapshot) {
        trace("restore ${s.line}:${s.character}")
        restoring = true
        textContent = s.text
        restoring = false
        // The next edit starts a new undo step
        lastEditGrowing = null
        readTextarea()
        setCaret(s.line, s.character)
    }

    fun autocomplete() {
        val topElement = (if (currentNode is Text) currentNode?.parentElement else currentNode) as HTMLElement
        val top = topElement.getBoundingClientRect().top
        val left = topElement.getBoundingClientRect().left
        val scrollTop = document.body.getBoundingClientRect().top
        divAutocomplete.style.left = "${left + selectedElementPosition * 10}px"
        divAutocomplete.style.top = "${top - scrollTop + 19}px"
        divAutocomplete.innerHTML = HtmlSource("")

        trace("currentLine: $currentLine")
        val spanMode = currentLine?.textContent?.length?.let { if (it > 0) SpanMode.INLINED else SpanMode.START }

        if (autocompletes[spanMode] != null) {
            for (text in autocompletes[spanMode]!!) {
                val d = document.createElement("li") as HTMLLIElement
                d.classList.add(ClassName("cmd-autocomplete"))
                d.textContent = text.caption
                d.onclick = EventHandler {
                    topElement.textContent = topElement.textContent?.substring(
                        0,
                        selectedElementPosition + 1
                    ) + text.insertText + topElement.textContent?.substring(
                        selectedElementPosition + 1
                    )
                    divAutocomplete.style.display = "none"
                    if (topElement is HTMLDivElement) {
                        trace("Convert Element")
                        asciidocToHtml(topElement)
                    }

                }
                divAutocomplete.appendChild(d)
            }
        }

        val request = ++completionRequest
        val position = caretPosition()
        if (lsp != null && position != null) {
            lsp!!.completion(position.first, position.second) { items ->
                if (request != completionRequest) return@completion
                for (item in items) {
                    val d = document.createElement("li") as HTMLLIElement
                    d.classList.add(ClassName("cmd-autocomplete"), ClassName("lsp-completion"))
                    d.textContent = item.label.unsafeCast<String>()
                    val detail = item.detail
                    if (detail != null) {
                        val small = document.createElement("small")
                        small.textContent = detail.unsafeCast<String>()
                        d.appendChild(small)
                    }
                    d.onclick = EventHandler {
                        divAutocomplete.style.display = "none"
                        applyCompletion(item, position.first, position.second)
                    }
                    divAutocomplete.appendChild(d)
                }
            }
        }

        divAutocomplete.style.display = "block"
    }

    /**
     * Caret position as LSP (line, character): line is the index of the line div,
     * character counts UTF-16 units like JS strings do.
     */
    private fun caretPosition(): Pair<Int, Int>? {
        val selection = window.getSelection() ?: return null
        val focus = selection.focusNode ?: return null
        return positionOf(focus, selection.focusOffset)
    }

    /**
     * Convert a DOM position (node, offset) inside divContent to (line, character).
     */
    private fun positionOf(node: Node, offset: Int): Pair<Int, Int>? {
        val children = divContent.children
        if (node == divContent) {
            if (children.length == 0) return Pair(0, 0)
            if (offset >= children.length) return Pair(children.length - 1, children[children.length - 1]?.textContent?.length ?: 0)
            return Pair(offset, 0)
        }
        var lineNode: Node? = node
        while (lineNode != null && lineNode.parentNode != divContent) lineNode = lineNode.parentNode
        if (lineNode == null) return null
        var index = -1
        for (i in 0 until children.length) {
            if (children[i] == lineNode) index = i
        }
        if (index < 0) return null
        val range = document.createRange()
        range.selectNodeContents(lineNode)
        range.setEnd(node, offset)
        return Pair(index, range.toString().length)
    }

    /**
     * The browser mishandles typing over a selection spanning several line divs (Ctrl+A for instance):
     * it leaves empty or stray lines. Replace such a selection in the text instead.
     *
     * @return true if the key was handled
     */
    private fun replaceMultilineSelection(key: String): Boolean {
        val newText = when {
            key == "Backspace" || key == "Delete" -> ""
            key == "Enter" -> "\n"
            key.length == 1 -> key
            else -> return false
        }
        val selection = window.getSelection() ?: return false
        if (selection.isCollapsed || selection.rangeCount == 0) return false
        val range = selection.getRangeAt(0)
        val start = positionOf(range.startContainer, range.startOffset) ?: return false
        val end = positionOf(range.endContainer, range.endOffset) ?: return false
        if (start.first == end.first) return false
        trace("replaceMultilineSelection $start $end")
        replaceText(start.first, start.second, end.first, end.second, newText)
        return true
    }

    /**
     * Replace a range of the text, rebuild the lines and put the caret after the new text.
     */
    private fun replaceText(startLine: Int, startChar: Int, endLine: Int, endChar: Int, newText: String) {
        val txt = textContent
        val lines = txt.split("\n")
        fun offset(l: Int, c: Int): Int {
            var o = 0
            for (i in 0 until minOf(l, lines.size)) o += lines[i].length + 1
            return minOf(o + c, txt.length)
        }
        val before = txt.substring(0, offset(startLine, startChar)) + newText
        textContent = before + txt.substring(offset(endLine, endChar))
        readTextarea()
        setCaret(before.count { it == '\n' }, before.length - (before.lastIndexOf('\n') + 1))
    }

    /**
     * Once the last line div is deleted, the browser puts what is typed directly in divContent,
     * outside any line, and the editor no longer sees it: rebuild the lines, keeping the caret.
     *
     * @return true if the lines were rebuilt
     */
    private fun normalizeLines(): Boolean {
        val nodes = divContent.childNodes.asList()
        if (nodes.isNotEmpty() && nodes.all { it is HTMLDivElement }) return false
        val selection = window.getSelection()
        val focus = selection?.focusNode
        val focusOffset = selection?.focusOffset ?: 0
        var txt = ""
        var caret = -1
        for (n in nodes) {
            if (n is HTMLDivElement) {
                if (caret < 0 && focus != null && n.contains(focus)) {
                    val range = document.createRange()
                    range.selectNodeContents(n)
                    range.setEnd(focus, focusOffset)
                    caret = txt.length + range.toString().length
                }
                txt += (n.textContent ?: "") + "\n"
            } else if (n is Text) {
                if (n == focus) caret = txt.length + focusOffset
                txt += n.textContent ?: ""
            }
        }
        if (caret < 0) caret = txt.trimEnd('\n').length
        if (!txt.endsWith("\n")) txt += "\n"
        trace("normalizeLines caret: $caret")
        textContent = txt
        readTextarea()
        val before = txt.substring(0, caret)
        val line = before.count { it == '\n' }
        val character = before.length - (before.lastIndexOf('\n') + 1)
        setCaret(line, character)
        return true
    }

    private fun setCaret(line: Int, character: Int) {
        // Drop the pending records of a rebuild, else the observer callback moves the caret to the start of the last line
        mutationObserver?.takeRecords()
        val children = divContent.children
        if (children.length == 0) return
        // A line past the end means the end of the text
        val lineDiv = children[minOf(line, children.length - 1)] ?: return
        var remaining = if (line < children.length) character else Int.MAX_VALUE
        val range = document.createRange()
        fun place(node: Node): Boolean {
            if (node is Text) {
                if (remaining <= node.length) {
                    range.setStart(node, remaining)
                    return true
                }
                remaining -= node.length
                return false
            }
            for (c in node.childNodes.asList()) if (place(c)) return true
            return false
        }
        if (!place(lineDiv)) {
            range.selectNodeContents(lineDiv)
            range.collapse(false)
        }
        range.collapse(true)
        divContent.focus()
        window.getSelection()?.removeAllRanges()
        window.getSelection()?.addRange(range)
        lastCaret = Pair(line, character)
    }

    private fun applyCompletion(item: dynamic, line: Int, character: Int) {
        val edit: dynamic = item.textEdit
        val newText = (if (edit != null) edit.newText else item.insertText ?: item.label).unsafeCast<String>()
        val startLine = if (edit != null) edit.range.start.line.unsafeCast<Int>() else line
        val startChar = if (edit != null) edit.range.start.character.unsafeCast<Int>() else character
        val endLine = if (edit != null) edit.range.end.line.unsafeCast<Int>() else line
        val endChar = if (edit != null) edit.range.end.character.unsafeCast<Int>() else character

        replaceText(startLine, startChar, endLine, endChar, newText)
    }

    private fun showDiagnostics() {
        val lines = divContent.children
        val gutter = divLineNumberContainer.children
        val classes = arrayOf(ClassName("lsp-error"), ClassName("lsp-warning"), ClassName("lsp-info"))
        for (i in 0 until lines.length) {
            lines[i]?.classList?.remove(*classes)
            lines[i]?.removeAttribute("title")
        }
        for (i in 0 until gutter.length) gutter[i]?.classList?.remove(*classes)

        val messages = mutableMapOf<Int, MutableList<String>>()
        val worst = mutableMapOf<Int, Int>()
        for (d in diagnostics) {
            val severity = (d.severity ?: 1).unsafeCast<Int>()
            val source = (d.source ?: "lsp").unsafeCast<String>()
            for (l in d.range.start.line.unsafeCast<Int>()..d.range.end.line.unsafeCast<Int>()) {
                messages.getOrPut(l) { mutableListOf() }.add("$source: ${d.message}")
                worst[l] = minOf(worst[l] ?: 4, severity)
            }
        }
        for ((l, m) in messages) {
            val c = classes[minOf(worst[l]!!, 3) - 1]
            lines[l]?.classList?.add(c)
            lines[l]?.setAttribute("title", m.joinToString("\n"))
            gutter[l]?.classList?.add(c)
        }
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
                    if (line.startsWith("§§Style")) scanningStyle = true
                    if (line.startsWith("§§Autocomplete")) scanningAutocomplete = true
                    if (line.startsWith("§§MenuEntry")) scanningMenuEntry = true
                    if (scanningStyle && !scanningAutocomplete) {
                        trace("Add Styles")

                        val s = Span(pattern, cn, inlined)

                        var l = styles[currentContext]
                        if (l == null) {
                            l = mutableListOf()
                        }
                        l.add(s)
                        styles[currentContext] = l
                        if (s.inlined == SpanMode.CONTEXT_START) currentContext = s
                        if (s.inlined == SpanMode.CONTEXT_END) currentContext = null
                    } else if (!scanningMenuEntry && scanningAutocomplete) {
                        trace("Add Autocompletes")
                        scanningStyle = false
                        scanningAutocomplete = true
                        val s = AutocompleteEntry(pattern, cn, inlined)

                        var l = autocompletes[s.inlined]
                        if (l == null) {
                            l = mutableListOf()
                        }
                        l.add(s)
                        autocompletes[s.inlined] = l

                    } else if (scanningMenuEntry) {
                        trace("Add MenuEntries")
                        scanningStyle = false
                        scanningAutocomplete = false
                        scanningMenuEntry = true
                        val s = MenuEntry(pattern, cn, inlined)

                        var l = menues[currentContext]
                        if (l == null) {
                            l = mutableListOf()
                        }
                        l.add(s)
                        menues[currentContext] = l

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

//    fun asciidocToHtml(e: HTMLDivElement) {
//        trace("asciidocToHtml +++ ${window.getSelection()?.anchorOffset} ${window.getSelection()?.focusNode}")
//        var txt = escapeHtml(e.textContent)
//        if (txt?.isEmpty() == true) {
//            e.innerHTML = HtmlSource("<br>")
//            return
//        }
//        if (txt != null) {
//            var hasStart: Span? = null
//            var hasStartCharSeq: Span? = null
//            var inlineMatchSequence: List<Pair<Span, MatchResult>> = mutableListOf<Pair<Span, MatchResult>>()
//            val entries = styles[currentContext]!!
//            var inContext = false
//
//            for (entry in entries) {
//                if (entry.inlined == SpanMode.CONTEXT_START) {
//                    inContext = true
//                    break
//                }
//                if (entry.inlined == SpanMode.CONTEXT_END) {
//                    if (inContext && txt!!.startsWith(entry.pattern)) {
//                        inContext = false
//                        break
//                    }
//                }
//                if (inContext) break
//
//                if (entry.inlined == SpanMode.START_CHAR_SEQ) {
//                    if (txt!!.startsWith(entry.pattern)) {
//                        hasStartCharSeq = entry
//                        txt = txt.substring(entry.pattern.length)
//                    }
//                }
//
//                if (entry.inlined == SpanMode.START && hasStart == null) {
//                    if (txt!!.startsWith(entry.pattern)) {
//                        hasStart = entry
//                        break
//                    }
//                } else if (entry.inlined == SpanMode.INLINED || entry.inlined == SpanMode.INLINED_BREAK || entry.inlined == SpanMode.CONTEXT_START || entry.inlined == SpanMode.CONTEXT_END) {
//                    val pattern = entry.pattern
//                    val regex = Regex(pattern)
//                    if (regex.containsMatchIn(txt!!)) {
//                        for (s in regex.findAll(txt)) {
//                            inlineMatchSequence = inlineMatchSequence.plusElement(Pair(entry, s))
//                        }
//                        if (entry.inlined == SpanMode.INLINED_BREAK) break
//                        if (entry.inlined == SpanMode.CONTEXT_START && currentContext == null) {
//                            currentContext = entry
//                            break
//                        } else if (entry.inlined == SpanMode.CONTEXT_END && currentContext != null) {
//                            currentContext = null
//                            break
//                        }
//                    }
//                }
//            }
//            var result: String = (if (hasStartCharSeq != null) {
//                """<span class="${hasStartCharSeq.className}">${hasStartCharSeq.pattern}</span>"""
//            } else "")
//            val sorted = inlineMatchSequence.sortedBy { it.second.range.first }
//
//            var i = 0
//            for (c in sorted) {
//                val spanStyle: Span = c.first
//                val match: MatchResult = c.second
//                val start = match.range.first
//                val ends = match.range.endInclusive
//                val replace =
//                    """${match.groupValues[1]}<span typeInlined="${spanStyle.inlined}" class="${spanStyle.className}">${match.groupValues[2]}</span>${match.groupValues[3]}"""
//                result += txt!!.substring(i..<start) + replace
//                i = ends + 1
//            }
//            result += txt!!.substring(i)
//
//            if (hasStart != null) {
//                val cn = hasStart.className
//                result = """<span class="$cn">$result</span>"""
//            }
//
//            if (e.innerHTML.toString() != result) {
//                val n = currentNode
//                val p = selectedElementPosition
//                var i = 0
//                e.childNodes.asList().forEachIndexed { index, node ->
//                    if (node == n || node == n?.parentNode) i = index
//                }
//                e.innerHTML = HtmlSource(result)
//                updateSelection(e, n, p, i)
//            }
//        }
//        trace("asciidocToHtml --- ${window.getSelection()?.anchorOffset} ${window.getSelection()?.focusNode}")
////        TODO: window.getSelection()?.setPosition(e.childNodes[e.childElementCount], )
//    }

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

            var txtCursor = 0
            outerWhile@ while (txt!!.length > txtCursor) {
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
                    if (inContext) {
                        break
                    }

                    if (txtCursor == 0) {
                        if (entry.inlined == SpanMode.START_CHAR_SEQ) {
                            if (txt.startsWith(entry.pattern)) {
                                hasStartCharSeq = entry
                                txtCursor = entry.pattern.length
                                break
                            }
                        }
                    }

                    if (txtCursor == 0 && entry.inlined == SpanMode.START && hasStart == null) {
                        if (txt.startsWith(entry.pattern)) {
                            hasStart = entry
                            break@outerWhile
                        }
                    } else if (entry.inlined == SpanMode.INLINED || entry.inlined == SpanMode.INLINED_BREAK || entry.inlined == SpanMode.CONTEXT_START || entry.inlined == SpanMode.CONTEXT_END) {
                        val pattern = entry.pattern
                        val regex = Regex(pattern)

                        if (txtCursor < txt.length) {
                            val s = regex.find(txt, txtCursor)
                            if (s != null && s.range.first == txtCursor) {
                                txtCursor = s.range.last
                                inlineMatchSequence = inlineMatchSequence.plusElement(Pair(entry, s))
                                break
                            }
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
                txtCursor++
            }

            var i = 0
            var result: String = (if (hasStartCharSeq != null) {
                i += hasStartCharSeq.pattern.length
                """<span class="${hasStartCharSeq.className}">${hasStartCharSeq.pattern}</span>"""
            } else "")

            for (c in inlineMatchSequence) {
                val spanStyle: Span = c.first
                val match: MatchResult = c.second
                val start = match.range.first
                val ends = match.range.endInclusive
                val replace =
                    """${match.groupValues[1]}<span typeInlined="${spanStyle.inlined}" class="${spanStyle.className}">${match.groupValues[2]}</span>${match.groupValues[3]}"""
                result += txt!!.substring(i..<start) + replace
                i = ends + 1
            }
            result += txt!!.substring(i)

            if (hasStart != null) {
                val cn = hasStart.className
                result = """<span class="$cn">$result</span>"""
            }

            if (e.innerHTML.toString() != result) {
                val n = currentNode
                val p = selectedElementPosition
                var i = 0
                e.childNodes.asList().forEachIndexed { index, node ->
                    if (node == n || node == n?.parentNode) i = index
                }
                e.innerHTML = HtmlSource(result)
                updateSelection(e, n, p, i)
            }
        }
        trace("asciidocToHtml --- ${window.getSelection()?.anchorOffset} ${window.getSelection()?.focusNode}")
//        TODO: window.getSelection()?.setPosition(e.childNodes[e.childElementCount], )
    }

    fun appendDivToContent(s: String?): HTMLDivElement {
        val cmd: HTMLDivElement = document.createElement("div") as HTMLDivElement
        cmd.classList.add(ClassName("cm-line"))
        cmd.contentEditable = "true"
        cmd.innerHTML = HtmlSource(s ?: "<br>")
        return cmd
    }

    fun createLineNumbers() {
        divLineNumberContainer.innerHTML = HtmlSource("")
        for (i in 1 until max(divContent.childNodes.length + 1, 2)) {
            val number: HTMLDivElement = document.createElement("div") as HTMLDivElement
            if (i == 1) number.style.marginTop = "4px"
            number.classList.add(ClassName("cm-gutterElement"))
            number.textContent = i.toString()
            divLineNumberContainer.appendChild(number)
        }
//        updateSelectionNewLine(cmd)
    }

    fun readTextarea() {
        trace("readTextarea")
        divContent.innerHTML = HtmlSource("")
        if (text.textContent?.length == 0) {
            text.textContent = "\n"
        }
        if (text.textContent != null && text.textContent!!.isNotEmpty()) {
            // Only the final line break: trimEnd() also dropped the spaces ending the last line
            text.textContent!!.removeSuffix("\n").removeSuffix("\r").split("\n").forEach {
                if (it.isNotEmpty()) {
                    val d = appendDivToContent(escapeHtml(it))
                    divContent.appendChild(d)
                    createLineNumbers()
                    if (currentLine != null)
                        asciidocToHtml(d)
                } else {
                    divContent.appendChild(appendDivToContent("<br>"))
                    createLineNumbers()
                }
            }
        }
        showDiagnostics()
    }

}