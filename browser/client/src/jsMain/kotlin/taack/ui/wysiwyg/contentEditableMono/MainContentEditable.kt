package taack.ui.wysiwyg.contentEditableMono

import js.buffer.ArrayBuffer
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
import kotlin.js.Promise

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
            HEADER1(Span("== ", "asciidoc-h2", false)),
            HEADER2(Span("=== ", "asciidoc-h3", false)),
            HEADER3(Span("==== ", "asciidoc-h4", false)),
            HEADER4(Span("===== ", "asciidoc-h5", false)),
            TITLE(Span(".", "asciidoc-title", false)),
            bullet1(Span("()(^--$)()", "asciidoc-bullet1", true)),
            bullet2(Span("()(^----$)()", "asciidoc-bullet2", true)),
            bullet3(Span("()(^------$)()", "asciidoc-bullet3", true)),
            META(Span("()(^\\[[^[\\]]*\\]$)()", "asciidoc-meta", true)),
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
            IMAGE(Span("()(^image::[^[:]*\\[[^\\]]*\\]$)()", "asciidoc-image", true)),
            IMAGE_INLINE(Span("([^\\w\\d]?)(image:[^[:]*\\[[^\\]]*\\])([^\\w\\d]?)", "asciidoc-inline-image", true)),
        }
    }

    var focus: Int? = 0
    var currentLine: HTMLDivElement? = currentLineComputed
    val currentLineComputed: HTMLDivElement?
        get() {
            if (selectedElement?.parentElement is HTMLDivElement) {
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
            for (child in currentLine!!.childNodes.iterator()) {
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
        if (top != null) {
            val visibleElement = divContent.parentElement?.parentElement?.parentElement?.parentElement
            if (visibleElement != null) {

                val scrollTop = document.body.getBoundingClientRect().top
                val bottomVisible = visibleElement.getBoundingClientRect().bottom
                val topVisible = visibleElement.getBoundingClientRect().top
                println("topVisible: $topVisible, bottomVisible: $bottomVisible")
                if (e.checkVisibility() && top > topVisible && top < bottomVisible) {
                    divAutocomplete.style.left = "${e.getBoundingClientRect().left + position * 10}px"
                    divAutocomplete.style.top = "${e.getBoundingClientRect().top - scrollTop + 19}px"
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
        val compressedOptions =  text.getAttribute("editoroption")
        if (compressedOptions != null && compressedOptions.isNotEmpty()) {
            println("editoroption = ${compressedOptions}")
            println("decompressing editoroption = ${decompress(compressedOptions)}")
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

        if (text.textContent?.length == 0) {
            text.textContent = "\n"
        }

        text.textContent?.split("\n")?.forEach {
            if (it.isNotEmpty())
                asciidocToHtml(createCmdLine(it, 0)!!)
            else createCmdLine("<br>", 0)
        }

        divContent.onpaste = EventHandler { e ->
            e.clipboardData?.files?.length?.let {
                if (it > 0) {
                    for (f in e.clipboardData!!.files) {
                        println("f: $f")
                    }
                    e.preventDefault()
                    e.stopPropagation()
                }
            }
        }

        divContent.ondrop = EventHandler { e ->
            println("ondrop")
            e.dataTransfer?.files?.length?.let {
                if (it > 0) {
                    for (f in e.dataTransfer!!.files) {
                        println("f: $f")
                    }
                    e.preventDefault()
                    e.stopPropagation()
                }
            }
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
            var inlineMatchSequence: List<Pair<CmdLine.SpanStyle, MatchResult>> =
                mutableListOf<Pair<CmdLine.SpanStyle, MatchResult>>()
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
                val replace =
                    """${match.groupValues[1]}<span class="${spanStyle.span.className}">${match.groupValues[2]}</span>${match.groupValues[3]}"""
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


    fun decompress(str: String): String {
        println("CanvasScriptCommon::decompress: $str")
        return js("""
function decompress(txt, encoding) {
    var byteArray = new TextEncoder('utf-8').encode(txt);
//    var byteArray = Uint8Array.from(atob(txt), c => c.charCodeAt(0));
    var cs = new DecompressionStream(encoding);
    var writer = cs.writable.getWriter();
    writer.write(byteArray);
    writer.close();
    return new Response(cs.readable).arrayBuffer();

//  var ds = new DecompressionStream(encoding);
//  var decompressedStream = atob(txt).stream().pipeThrough(ds);
//  return new Response(decompressedStream).arrayBuffer();
}
decompress(str, "deflate");
"""
        )
    }


    fun compress(str: String): Promise<ArrayBuffer> {
        println("CanvasScriptCommon::compress: $str")
        return js("""
function compress(string, encoding) {
    var byteArray = new TextEncoder('utf-8').encode(string);
    var cs = new CompressionStream(encoding);
    var writer = cs.writable.getWriter();
    writer.write(byteArray);
    writer.close();
    return new Response(cs.readable).arrayBuffer();
}
compress(str, "deflate");
"""
        )
    }

//        override val txtScript: String
//        get() {
//            if (srcURI != null) {
//                val txt = txt.substring(srcURI!!.length)
//                compress(txt).then {
//                    val bytes = Uint8Array(it)
//                    val len = bytes.length
//                    val chars = CharArray(len)
//                    for (i in 0 until len) {
//                        chars[i] = bytes[i].toInt().toChar()
//                    }
//                    return@then chars.concatToString()
//                }.then {
//                    imageSrc =
//                        "${location.protocol}//${location.hostname}:8000/" + srcURI + "/svg/" + btoa(
//                            it
//                        ).replace(Regex("\\+"), "-").replace(Regex("/+"), "_")
//                    image = CanvasImg(imageSrc!!, srcURI!!, 0)
//                }
//            }
//            return txt
//        }


//    def asciidocRenderScript(String script) {
//        script = script.replaceAll('-', '+').replaceAll('_', '/')
//        File cached = Path.of(scriptCachePath.toString(), script).toFile()
//        if (cached.exists()) {
//            render cached.text
//        } else {
//            byte[] b64 = Base64.getDecoder().decode(script)
//            Inflater inflater = new Inflater()
//            inflater.setInput(b64)
//
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
//            byte[] buffer = new byte[1024]
//
//            while (!inflater.finished()) {
//                int decompressedSize = inflater.inflate(buffer)
//                outputStream.write(buffer, 0, decompressedSize)
//            }
//
//            println(new String(outputStream.toByteArray()))
//
//        }
//    }

}