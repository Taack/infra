package taack.ui.canvas

import taack.ui.base.Helper.Companion.trace
import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.base.element.Form
import taack.ui.canvas.command.*
import taack.ui.canvas.item.CanvasCaret
import taack.ui.canvas.item.CanvasImg
import taack.ui.canvas.item.CanvasLink
import taack.ui.canvas.script.CanvasKroki
import taack.ui.canvas.table.CanvasTable
import taack.ui.canvas.table.TxtHeaderCanvas
import taack.ui.canvas.table.TxtRowCanvas
import taack.ui.canvas.text.*
import web.canvas.CanvasRenderingContext2D
import web.clipboard.ClipboardEvent
import web.dom.document
import web.events.Event
import web.events.EventHandler
import web.events.addEventListener
import web.file.File
import web.file.FileReader
import web.html.*
import web.http.CrossOrigin
import web.uievents.DragEvent
import web.uievents.KeyboardEvent
import web.uievents.MouseEvent
import web.window.window
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

class MainCanvas(
    internal val embeddingForm: Form,
    internal val textarea: HTMLTextAreaElement,
    private val divHolder: HTMLDivElement,
    private val divScroll: HTMLDivElement
) {

    inner class MyMutableList(private val b: MutableList<ICanvasDrawable>) : MutableList<ICanvasDrawable> by b {
        override fun add(element: ICanvasDrawable): Boolean {
            currentDrawableIndex = b.size
            return b.add(element)
        }

        override fun removeAt(index: Int): ICanvasDrawable {
            if (index > 0) {
                currentDrawableIndex = index - 1
                return b.removeAt(index)
            } else if (index == 0 && b.size > 1) {
                val ret = b.removeAt(0)
                b.add(0, PCanvas(""))
                currentDrawableIndex = 0
                return ret
            } else throw IndexOutOfBoundsException("index: $index, size: ${b.size}")
        }

        override fun remove(element: ICanvasDrawable): Boolean {
            val i = b.indexOf(element)
            trace("REMOVE $i")
            if (i > 0) {
                b.removeAt(i)
                currentDrawableIndex = i - 1
                return true
            } else if (i == 0 && b.size > 1) {
                b.removeAt(0)
                b.add(0, PCanvas(""))
                currentDrawableIndex = 0
                return true
            }
            return false
        }
    }

    private val dprX = 2.0
    private val dprY = 2.0
    val canvas: HTMLCanvasElement = document.createElement("canvas") as HTMLCanvasElement
    private val canvasInnerBorder = 10.0
    private val ctx: CanvasRenderingContext2D
        get() = canvas.getContext(CanvasRenderingContext2D.ID) as CanvasRenderingContext2D
    private val texts: List<CanvasText>
        get() = drawables.mapNotNull { it.getSelectedText(currentMouseEvent?.offsetX, currentMouseEvent?.offsetY) }
            .toMutableList()
    private val currentLine: CanvasLine
        get() = currentText!!.lines[currentText!!.indexOfLine(_caretPosInCurrentText)]
    private val _drawables: MutableList<ICanvasDrawable> = mutableListOf()
    internal val drawables = MyMutableList(_drawables)
    private val initialDrawables = mutableListOf<ICanvasDrawable>()
    private var dy: Double = 0.0
    private var _caretPosInCurrentText: Int = 0
    private var caretPosInCurrentText: Int
        get() = _caretPosInCurrentText
        set(value) = run {
            var v: Int
            traceIndent("BEFORE _caretPosInCurrentText: $_caretPosInCurrentText, value: $value, currentText: ${currentText?.txt?.length}, currentLine: $currentLine")
            if (value > currentText!!.txt.length) { // Go to next line, below current line
                val j = texts.indexOf(currentText)
                trace("value > currentText!!.txt.length, j: $j, texts.size: ${texts.size}")
                if (j >= 0 && j < texts.size - 1) {
                    currentDrawableIndex = j + 1
                    v = _caretPosInCurrentText
                    if (v > currentText!!.txt.length) {
                        v = currentText!!.txt.length
                    }
                } else {
                    v = currentText!!.txt.length + 1
                }
            } else if (value < 0) { // Cursor goes up
                val j = currentDrawableIndex
                trace("value < 0 indexOfText = $j")
                if (j > 0) { // Go to previous line, above current line
                    currentDrawableIndex = j - 1
                    v = _caretPosInCurrentText
                    trace("value < 0 v = $v")
                } else { // If first line, stay there
                    v = 0
                }
            } else if (value == 0) {
                v = 0
            } else { // Move inside the same line (EXCEPT if at the line edges)
                val i = currentText!!.indexOfLine(currentLine)
                trace("ELSE branch i: $i, caretPosInLine: $caretPosInLine, currentLine.length: ${currentLine.length}")
                v = value
//                if (caretPosInLine < currentLine.length) {
//                    if (i <= 0) {
//                        val j = texts.indexOf(currentText)
//                        trace("ELSE branch j: $j")
//                        if (j >= 0) {
//                            currentDrawable = texts[j]
//                            v = currentText!!.lines.last().posEnd
//                        } else {
//                            v = 0
//                        }
//                    }
//                } else if (caretPosInLine >= currentLine.length) {
//                    if (i < currentText!!.lines.size) {
//                        trace("ELSE branch: caretPosInLine - decrease >= currentLine.length => < value: $value, currentText!!.lines.size: ${currentText!!.lines.size}")
//                        v = value
//                    } else {
//                        val j = texts.indexOf(currentText) + 1
//                        trace("ELSE branch: caretPosInLine - decrease >= currentLine.length => j: $j, texts.size: ${texts.size}")
//                        if (j < texts.size) {
//                            currentDrawable = texts[j]
//                            v = 0
//                        }
//                    }
//                }
            }
            _caretPosInCurrentText = if (v < 0) 0 else v
            traceDeIndent("AFTER  _caretPosInCurrentText: $_caretPosInCurrentText, value: $value, currentText: $currentText, currentLine: $currentLine")
        }
    private var currentDrawableIndex: Int = 0

    //        set(value) {
//            _caretPosInCurrentText = 0
//            field = value
//        }
    private val currentDrawable: ICanvasDrawable
        get() = drawables[currentDrawableIndex]
    private val currentText: CanvasText?
        get() = drawables[currentDrawableIndex].getSelectedText(currentMouseEvent?.offsetX, currentMouseEvent?.offsetY)
    private var currentDoubleClick: Triple<CanvasLine, Int, Int>? = null
    private var currentMouseEvent: MouseEvent? = null
    private var currentKeyboardEvent: KeyboardEvent? = null
    private var isDoubleClick: Boolean = false
    private var charSelectStartNInText: Int?
        get() = currentDoubleClick?.second
        set(value) = run { currentDoubleClick = currentDoubleClick?.copy(second = value!!) }
    private var charSelectEndNInText: Int?
        get() = currentDoubleClick?.third
        set(value) = run { currentDoubleClick = currentDoubleClick?.copy(third = value!!) }
    private var posYGlobal: Double = 0.0
    private val commandDoList = mutableListOf<ICanvasCommand>()
    private val commandUndoList = mutableListOf<ICanvasCommand>()
    private val caretPosInLine
        get() = caretPosInCurrentText - currentLine.posBegin

    private fun addDrawable() {
        var doNotDraw = false
        when (currentKeyboardEvent!!.key) {
            "Backspace" -> {
                trace("MainCanvas::addDrawable press Backspace")
                if (caretPosInCurrentText == 0) {
                    val txt = currentText!!.txt
                    val i = currentDrawableIndex
                    commandDoList.add(
                        DeleteDrawableCommand(
                            drawables, i
                        )
                    )
                    currentDrawableIndex = i - 1
                    _caretPosInCurrentText = currentText!!.txt.length
                    commandDoList.add(
                        AddCharCommand(
                            currentText!!,
                            txt
                        )
                    )

                } else
                    commandDoList.add(
                        RmCharCommand(
                            drawables,
                            currentDrawable.getSelectedText(currentMouseEvent?.offsetX, currentMouseEvent?.offsetY)!!,
                            caretPosInCurrentText--
                        )
                    )
            }

            "Tab" -> {
                trace("MainCanvas::addDrawable press Tab")
                if (currentKeyboardEvent!!.shiftKey)
                    commandDoList.add(
                        DeIndentCommand(currentDrawable)
                    )
                else
                    commandDoList.add(
                        IndentCommand(currentDrawable)
                    )

            }

            "Delete" -> {
                trace("MainCanvas::addDrawable press Delete")
                if (currentKeyboardEvent!!.ctrlKey) {
                    commandDoList.add(
                        DeleteDrawableCommand(drawables, currentDrawableIndex)
                    )
                } else {
                    val pos1 = caretPosInCurrentText
                    val pos2: Int? = null
                    commandDoList.add(
                        DeleteCharCommand(drawables, currentDrawable.getSelectedText()!!, pos1, pos2)
                    )
                }
            }

            "Enter" -> {
                trace("MainCanvas::addDrawable press Enter $caretPosInCurrentText")
                val i = currentDrawableIndex
                if (caretPosInCurrentText == 0) {
                    val d = PCanvas("")
                    commandDoList.add(
                        AddDrawableCommand(drawables, i, d)
                    )
                } else if (currentDrawable is CanvasKroki) {
                    commandDoList.add(
                        AddCharCommand(
                            currentText!!,
                            "\n",
                            caretPosInCurrentText++
                        )
                    )
                } else {
                    val i2 = i + 1
                    if (currentKeyboardEvent!!.ctrlKey && currentDrawable !is CanvasTable) {
                        commandDoList.add(
                            AddTableCommand(drawables, i2)
                        )
                    } else
                        when (currentText) {
                            is H2Canvas -> {
                                val d = H3Canvas("")
                                commandDoList.add(
                                    AddDrawableCommand(drawables, i2, d)
                                )
                                currentDrawableIndex = i2
                            }

                            is H3Canvas -> {
                                val d = H4Canvas("")
                                commandDoList.add(
                                    AddDrawableCommand(drawables, i2, d)
                                )
                                currentDrawableIndex = i2
                            }

                            is TxtHeaderCanvas -> {
                                trace("TxtHeaderCanvas")
                                val table = currentDrawable as CanvasTable
                                if (currentKeyboardEvent!!.shiftKey)
                                    commandDoList.add(
                                        RemoveTableColumnCommand(table, currentText as TxtHeaderCanvas)
                                    )
                                else commandDoList.add(
                                    AddTableColumnCommand(table, currentText as TxtHeaderCanvas)
                                )
                            }

                            is TxtRowCanvas -> {
                                trace("TxtRowCanvas")
                                val table = currentDrawable as CanvasTable
                                if (currentKeyboardEvent!!.shiftKey)
                                    commandDoList.add(
                                        RemoveTableRowCommand(table, currentText as TxtRowCanvas)
                                    )
                                else commandDoList.add(
                                    AddTableRowCommand(table, currentText as TxtRowCanvas)
                                )
                            }

                            else -> {
                                var initTxt = ""
                                if (caretPosInCurrentText != 0 && caretPosInCurrentText != currentText!!.txt.length) {
                                    initTxt = currentText!!.txt.substring(caretPosInCurrentText)
                                    commandDoList.add(
                                        DeleteCharCommand(
                                            drawables,
                                            currentText!!,
                                            caretPosInCurrentText,
                                            currentText!!.txt.length
                                        )
                                    )
                                }
                                _caretPosInCurrentText = 0
                                val d = PCanvas(initTxt)
                                commandDoList.add(
                                    AddDrawableCommand(drawables, i2, d)
                                )
                                currentDrawableIndex = i2
                            }
                        }
                }
            }

            "ArrowUp" -> {
                trace("MainCanvas::addDrawable press ArrowUp value: ${-(1 + currentLine.length)}")
                caretPosInCurrentText -= (1 + currentLine.length)
            }

            "ArrowDown" -> {
                trace("MainCanvas::addDrawable press ArrowDown currentLine.length: ${currentLine.length}")
                caretPosInCurrentText += (1 + currentLine.length)
            }

            "ArrowLeft" -> {
                caretPosInCurrentText--
            }

            "ArrowRight" -> {
                if (currentKeyboardEvent!!.ctrlKey && isDoubleClick) {
                    val decay =
                        currentText!!.txt.substring(charSelectEndNInText!! + 1).indexOfFirst { !it.isLetter() } + 1
                    if (decay == 0) {
                        charSelectEndNInText = currentText!!.txt.length
                    }
                    charSelectEndNInText = charSelectEndNInText?.plus(decay)
                } else {
                    caretPosInCurrentText++
                }
            }

            "End" -> {
                trace("MainCanvas::addDrawable press End")
                if (currentKeyboardEvent!!.ctrlKey) {
                    if (currentKeyboardEvent!!.shiftKey) {
                        currentDrawableIndex = texts.size
                    }
                    caretPosInCurrentText = currentText!!.lines.last().posEnd - 1
                }
                caretPosInCurrentText = currentLine.posEnd
            }

            "Home" -> {
                trace("MainCanvas::addDrawable press Home")
                if (currentKeyboardEvent!!.ctrlKey) {
                    if (currentKeyboardEvent!!.shiftKey) {
                        currentDrawableIndex = texts.size
                    }
                    caretPosInCurrentText = 0
                }
                caretPosInCurrentText = currentLine.posBegin
            }

            "Shift", "ShiftLeft", "ShiftRight", "Control", "ControlLeft", "ControlRight", "AltGraph" -> {
                doNotDraw = true
            }

            else -> {
                trace("MainCanvas::addDrawable else branch ${currentKeyboardEvent!!.key}, CTRL: ${currentKeyboardEvent!!.ctrlKey}, SHIFT: ${currentKeyboardEvent!!.shiftKey}")
                if (currentKeyboardEvent != null) {
                    if (currentKeyboardEvent!!.ctrlKey) {
                        if (currentKeyboardEvent!!.key[0] == 'z' && !currentKeyboardEvent!!.shiftKey && commandDoList.size > 0) {
                            trace("MainCanvas::addDrawable undo commandDoList: ${commandDoList.size}, commandUndoList: ${commandUndoList.size}")
                            commandUndoList.add(commandDoList.removeLast())
                        } else if (currentKeyboardEvent!!.key[0] == 'Z' && commandUndoList.size > 0) {
                            trace("MainCanvas::addDrawable redo commandDoList: ${commandDoList.size}, commandUndoList: ${commandUndoList.size}")
                            commandDoList.add(commandUndoList.removeLast())
                        }
                    } else
                        if (currentText != null) {
                            commandDoList.add(
                                AddCharCommand(
                                    currentText!!,
                                    currentKeyboardEvent!!.key[0].toString(),
                                    _caretPosInCurrentText++
                                )
                            )
                        }
                }
            }
        }
        if (!doNotDraw)
            draw()
    }

    private fun createButton(id: String, innerHtml: String, handler: () -> Unit) {
        val b = document.createElement("button") as HTMLButtonElement
        b.id = id + textarea.name
        b.innerHTML = innerHtml
        b.type = ButtonType.button
        b.classList.add("btn")
        b.classList.add("btn-light")
        b.style.margin = "2px"
        b.style.height = "29px"
        //  b.style.width = "80px"
        b.contentEditable = "false"
        b.onclick = EventHandler { e ->
            e.preventDefault()
            e.stopPropagation()
            handler()
        }
        divHolder.appendChild(b)
    }

    init {

        canvas.id = "canvas" + textarea.name
        if (divHolder.clientWidth > 0) {
            canvas.width = floor(divHolder.clientWidth * dprX).toInt()
            canvas.style.width = "${divHolder.clientWidth}px"
        } else trace("divHolder.clientWidth == 0 !!!")
        if (divScroll.clientHeight > 0) {
            canvas.height = floor(divScroll.clientHeight * dprY).toInt()
            canvas.style.height = "${divScroll.clientHeight}px"
        } else trace("divScroll.clientHeight == 0 !!!")

        trace("Canvas width: ${canvas.width}, height: ${canvas.height}")

//        ctx.setTransform(dpr, 0.0, 0.0, dpr, 0.0, 0.0)
        ctx.scale(dprX, dprY)


        canvas.tabIndex = 1
        canvas.style.border = "0"
//        divHolder.draggable = false
//        divHolder.contentEditable = "false"
        divHolder.style.border = "0"
        divScroll.style.border = "0"

        createButton("buttonBold", "<b style='margin: 0;height: 23px;'>BOLD</b>") {
            if (currentDoubleClick != null)
                commandDoList.add(
                    AddStyleCommand(
                        currentText!!,
                        TextStyle.BOLD,
                        currentDoubleClick!!.second,
                        currentDoubleClick!!.third
                    )
                )
            currentDoubleClick = null
            draw()
        }
        createButton("buttonMono", "<code style='margin: 0;height: 23px;'>Mono</code>") {
            if (currentDoubleClick != null)
                commandDoList.add(
                    AddStyleCommand(
                        currentText!!,
                        TextStyle.MONOSPACED,
                        currentDoubleClick!!.second,
                        currentDoubleClick!!.third
                    )
                )
            currentDoubleClick = null
            draw()
        }
        createButton("buttonBoldMono", "<code style='margin: 0;height: 23px;'><b>Mono</b></code>") {
            if (currentDoubleClick != null)
                commandDoList.add(
                    AddStyleCommand(
                        currentText!!,
                        TextStyle.BOLD_MONOSPACED,
                        currentDoubleClick!!.second,
                        currentDoubleClick!!.third
                    )
                )
            currentDoubleClick = null
            draw()
        }
//        createButton("buttonScript", "<code style='margin: 0;height: 23px;'><em>Kroki</em></code>") {
//            if (currentDrawable != null)
//                commandDoList.add(
//                    ChangeStyleCommand(drawables, initialDrawables, currentDrawable, CanvasKroki(currentText!!.txt))
//                )
//            draw()
//        }
        createButton("buttonTable", "<span style='margin: 0;height: 23px;'>Table</span>") {
                commandDoList.add(
                    AddTableCommand(drawables, currentDrawableIndex)
                )
            draw()

        }

        createButton(
            "bH2",
            "<span style='margin: 0;height: 23px;font-size: 18px; font-weight: bold; color: #ba3925'>H2</span>"
        ) {
                commandDoList.add(
                    ChangeStyleCommand(drawables, currentDrawableIndex, H2Canvas(currentText!!.txt))
                )
            draw()
        }
        createButton(
            "bH3",
            "<span style='margin: 0;height: 23px;font-size: 16px; font-weight: bold; color: #ba3925'>H3</span>"
        ) {
                commandDoList.add(
                    ChangeStyleCommand(drawables, currentDrawableIndex, H3Canvas(currentText!!.txt))
                )
            draw()
        }
        createButton(
            "bH4",
            "<span style='margin: 0;height: 23px;font-size: 14px; font-weight: bold; color: #ba3925'>H4</span>"
        ) {
                commandDoList.add(
                    ChangeStyleCommand(drawables, currentDrawableIndex, H4Canvas(currentText!!.txt))
                )
            draw()
        }
        createButton("bP", "<span style='margin: 0;height: 23px;'>P</span>") {
                commandDoList.add(
                    ChangeStyleCommand(drawables, currentDrawableIndex, PCanvas(currentText!!.txt))
                )
            draw()
        }
        createButton("bBullet", " • ") {
                commandDoList.add(
                    ChangeStyleCommand(drawables, currentDrawableIndex, LiCanvas(currentText!!.txt))
                )
            draw()
        }
        createButton("bBullet2", "    ‧ ") {
                commandDoList.add(
                    ChangeStyleCommand(drawables, currentDrawableIndex, Li2Canvas(currentText!!.txt))
                )
            draw()
        }
//        createButton("bAsciidoc", "ADoc") {
//            draw()
//            val asciidoc = ICanvasDrawable.dumpAsciidoc(drawables)
//            textarea.textContent = asciidoc
//            prompt("Copy to clipboard: Ctrl+C, Enter", asciidoc)
//        }

        divHolder.appendChild(canvas)

        divScroll.addEventListener(Event.SCROLL, { ev: Event ->
            trace("divScroll scroll")
            dy = divScroll.scrollTop
            divHolder.style.transform = "translate(0px, ${dy}px)"
            isDoubleClick = false
            draw()
            ev.preventDefault()
            ev.stopPropagation()
        })

        window.onresize = EventHandler {
            posYGlobal = -dy
            isDoubleClick = false
            draw()
        }
        canvas.onclick = EventHandler { event: MouseEvent ->
            traceIndent("canvas click")
            isDoubleClick = false
            if (event.detail == 3) {
                isDoubleClick = true
                charSelectStartNInText = 0
                charSelectEndNInText =
                    currentDrawable.getSelectedText(event.offsetX, event.offsetY)!!.txt.length
                trace("canvas click double click == triple click")
            }

            trace("setting currentMouseEvent = $event")
            currentMouseEvent = event
            event.preventDefault()
            event.stopPropagation()
            for (i in 0..<drawables.size) {
                if (drawables[i].isClicked(event.offsetX, event.offsetY)) {
                    currentDrawableIndex = i
                    val text = currentDrawable.getSelectedText(event.offsetX, event.offsetY)!!
                    val currentClick = text.click(ctx, event.offsetX, event.offsetY)
                    caretPosInCurrentText = currentClick!!.second
                }
            }
            traceDeIndent("canvas click => $_caretPosInCurrentText, $currentDrawable, ${currentText?.txt}")
            draw()
        }

        canvas.onkeydown = EventHandler { event: KeyboardEvent ->
            currentKeyboardEvent = event
            if (!event.ctrlKey) isDoubleClick = false

            val pasteEvent = event.ctrlKey && event.key[0] == 'v'

            if (!pasteEvent) {
                addDrawable()
                event.preventDefault()
            } else {
                trace("paste event")
            }
        }

        canvas.ondblclick = EventHandler { event: MouseEvent ->
            trace("canvas dblclick")
            event.preventDefault()
//            event.stopPropagation()
            isDoubleClick = true
            for (i in 0..drawables.size) {
                if (drawables[i].isClicked(event.offsetX, event.offsetY)) {
                    if (currentDrawable is CanvasImg || currentDrawable is CanvasLink)
                        commandDoList.add(DeleteDrawableCommand(drawables, i))
                    else {
                        currentDrawableIndex = i
                        currentDoubleClick = currentDrawable.doubleClick(ctx, event.offsetX, event.offsetY)
                    }
                }
            }
            draw()
        }

        document.onpaste = EventHandler { event: ClipboardEvent ->
            trace("canvasEvent paste $currentText $currentMouseEvent $caretPosInCurrentText")
            val txt = event.clipboardData!!.getData("text")
            event.preventDefault()
            event.stopPropagation()
            if (currentText != null && txt.isNotEmpty()) {
                commandDoList.add(
                    AddCharCommand(
                        currentText!!,
                        txt,
                        caretPosInCurrentText
                    )
                )
                trace("canvasEvent paste: $txt")
            } else if (event.clipboardData!!.items.length > 0) {
                // Use DataTransferItemList interface to access the file(s)
                for (item in event.clipboardData!!.items) {
                    // If dropped items aren't files, reject them
                    if (item.kind === "file") {
                        val file = item.getAsFile()
                        trace("canvasEvent1 file[].name = ${file?.name}")
                        if (file != null) {
                            placeFile(file)
                        }
                    }
                }
            }
            draw()
        }

        divScroll.ondrop = EventHandler { event: DragEvent ->
            trace("canvasEvent drop $currentText $currentMouseEvent $caretPosInCurrentText")
            event.preventDefault()
            event.stopPropagation()
            if (event.dataTransfer?.items?.length!! > 0) {
                // Use DataTransferItemList interface to access the file(s)
                for (item in event.dataTransfer?.items!!) {
                    // If dropped items aren't files, reject them
                    if (item.kind === "file") {
                        val file = item.getAsFile()
                        trace("canvasEvent1 file[].name = ${file?.name}")
                        if (file != null) {
                            placeFile(file)
                        }
                    }
                }
            } else {
                // Use DataTransfer interface to access the file(s)
                for (file in event.dataTransfer?.files!!) {
                    trace("canvasEvent2 file[].name = ${file.name}")
                }
            }

            val txt = event.dataTransfer!!.getData("text")

            commandDoList.add(
                AddCharCommand(
                    currentText!!,
                    txt,
                    caretPosInCurrentText
                )
            )

            trace("canvasEvent drop on ${textarea.name}: $txt")
        }

        divHolder.ondragover = EventHandler { event: DragEvent ->
//            trace("canvasEvent ondragover")
            event.preventDefault()
//            event.stopPropagation()
//            val txt = event.dataTransfer!!.getData("text")
//            trace("canvasEvent ondragover: $txt")
        }
        divHolder.ondrag = EventHandler { event: DragEvent ->
            trace("canvasEvent ondrag")
            event.preventDefault()
            event.stopPropagation()
            val txt = event.dataTransfer!!.getData("text")
            trace("canvasEvent ondrag: $txt")
        }
        addInitialTexts()
        currentDrawableIndex = 0
        draw()
    }

    private fun placeFile(file: File) {
        val reader = FileReader()
        if (file.type.startsWith("image/")) {
            reader.onload = EventHandler {
                val img = document.createElement("img") as HTMLImageElement
                img.crossOrigin = CrossOrigin.anonymous
                img.onload = EventHandler {
                    val c = document.createElement("canvas") as HTMLCanvasElement
                    val rw = img.width / min(img.width, 1024)
                    val rh = img.height / min(img.height, 1024)
                    val r = max(rw, rh)
                    c.width = img.width / r
                    c.height = img.height / r
                    val ctx = c.getContext(CanvasRenderingContext2D.ID)
                    ctx!!.drawImage(
                        img,
                        0.0,
                        0.0,
                        img.width.toDouble(),
                        img.height.toDouble(),
                        0.0,
                        0.0,
                        c.width.toDouble(),
                        c.height.toDouble()
                    )

                    val dataUrl = c.toDataURL(file.type)
                    var index = currentDrawableIndex
                    if (index == -1) {
                        index = 0
                    }

                    commandDoList.add(
                        AddImageCommand(
                            drawables,
                            index,
                            CanvasImg(dataUrl, file.name, 0),
                        )
                    )
                    val d = PCanvas("")
                    currentDrawableIndex = index + 1
                    commandDoList.add(
                        AddDrawableCommand(drawables, index + 1, d)
                    )
                }
                img.src = reader.result.toString()

            }
            reader.readAsDataURL(file)
        } else {
            reader.onload = EventHandler {
                var index = currentDrawableIndex
                if (index == -1) {
                    index = 0
                }
                commandDoList.add(
                    AddDrawableCommand(
                        drawables,
                        index,
                        CanvasLink(file.name, file.name, 0),
                    )
                )
                val d = PCanvas("")
                currentDrawableIndex = index + 1
                commandDoList.add(
                    AddDrawableCommand(drawables, index + 1, d)
                )
            }
            reader.readAsDataURL(file)
        }

        if (embeddingForm.mapFileToSend["${textarea.name}File"] == null) {
            embeddingForm.mapFileToSend["${textarea.name}File"] = mutableListOf()
        }
        embeddingForm.mapFileToSend["${textarea.name}File"]!!.add(file)
    }

    private fun addInitialTexts() {
        if (textarea.innerText.isNotBlank()) {
            trace("addInitialTexts ${textarea.innerText}")
            initialDrawables.addAll(ICanvasDrawable.readAsciidoc(this))
        } else {
            trace("addInitialTexts BLANK")
            initialDrawables.add(PCanvas(""))
        }
//        val h2 = H2Canvas("Topology Filters and Selectors Example for various data layout")
//        initialDrawables.add(h2)
//
//        val h3 = H3Canvas("Directed Acyclic Graphs (the most common in computer sciences)")
//        initialDrawables.add(h3)
//
//        val p1 =
//            PCanvas("DSL are AI friendly, so we want to be able to use more natural language in the future to generate our assets, but generation will be translated into those DSLs, in order to be human editable, efficiently.")
//        initialDrawables.add(p1)
//
//        val h31 = H3Canvas("For Assemblies and bodies")
//        initialDrawables.add(h31)
//
//        val h4 = H4Canvas("Category")
//        initialDrawables.add(h4)
//
//        initialDrawables.add(CanvasTable.createTable())
//
//        val s = CanvasKroki(
//            """
//            GraphViz
//            digraph G {Hello->World}
//            """.trimIndent()
//        )
//        initialDrawables.add(s)
//
//        val p2 = PCanvas(
//            """
//            Matched by feature, body name, but also by position DSL.
//            Matched by feature, body name, but also by position DSL.
//            Matched by feature, body name, but also by position DSL.
//            Matched by feature, body name, but also by position DSL.
//            Matched by feature, body name, but also by position DSL.
//            Matched by feature, body name, but also by position DSL.
//            Matched by feature, body name, but also by position DSL.
//            Matched by feature, body name, but also by position DSL.
//            Matched by feature, body name, but also by position DSL.
//            Matched by feature, body name, but also by position DSL.
//            Matched by feature, body name, but also by position DSL.
//            Matched by feature, body name, but also by position DSL.
//            Matched by feature, body name, but also by position DSL.
//            Matched by feature, body name, but also by position DSL.
//            Matched by feature, body name, but also by position DSL.
//            Matched by feature, body name, but also by position DSL.
//            Matched by feature, body name, but also by position DSL.
//            Matched by feature, body name, but also by position DSL.
//            Matched by feature, body name, but also by position DSL.
//            Matched by feature, body name, but also by position DSL.
//            Matched by feature, body name, but also by position DSL.
//            Matched by feature, body name, but also by position DSL.
//        """.trimIndent()
//        )
//        initialDrawables.add(p2)
//        val p3 = PCanvas(p2.txt)
//        initialDrawables.add(p3)
//        val p4 = PCanvas(p2.txt)
//        initialDrawables.add(p4)
//
//        val image = CanvasImg("https://mdn.github.io/shared-assets/images/examples/rhino.jpg", "Hello", 0)
//        initialDrawables.add(image)
        drawables.addAll(initialDrawables)
    }

    private fun draw() {
        traceIndent("MainCanvas::draw")
        if (divHolder.clientWidth > 0) {
            canvas.width = floor(divHolder.clientWidth * dprX).toInt()
            canvas.style.width = "${divHolder.clientWidth}px"
        } else trace("divHolder.clientWidth == 0 !!!")
        if (divScroll.clientHeight > 0) {
            canvas.height = floor(divScroll.clientHeight * dprY).toInt()
            canvas.style.height = "${divScroll.clientHeight}px"
        } else trace("divScroll.clientHeight == 0 !!!")
        ctx.scale(dprX, dprY)

//        canvas.width = divHolder.clientWidth
        CanvasText.num1 = 0
        CanvasText.num2 = 0
        CanvasText.figNum = 1
        posYGlobal = -dy

        trace("Clear ${canvas.width.toDouble()} x ${canvas.height.toDouble()}")
        ctx.clearRect(0.0, 0.0, canvas.width.toDouble(), canvas.height.toDouble())

        trace("Reset text")
        for (text in drawables) {
            text.reset()
        }

        trace("Reset Drawables")
        drawables.clear()
        drawables.addAll(initialDrawables)

        trace("Execute commandList")
        val executedCommands = mutableListOf<ICanvasCommand>()
        for (cmd in commandDoList) {
            if (cmd.doIt())
                executedCommands.add(cmd)
        }
        commandDoList.clear()
        commandDoList.addAll(executedCommands)

        trace("Draw all drawables +++")
        for (text in drawables) {
            try {
                posYGlobal = text.draw(ctx, divHolder.clientWidth - canvasInnerBorder, posYGlobal, canvasInnerBorder)
            } catch (e: Throwable) {
                trace(e.message ?: "")
            }
        }
        trace("Draw all drawables ---")

        trace("currentText == $currentText, ${currentText?.txt}")
        if (currentText != null) {
            trace("Draw caret currentText != null, caretPosInCurrentText = $caretPosInCurrentText, caretPosInLine = $caretPosInLine, currentLine!!.length = ${currentLine.length}")
            CanvasCaret.draw(ctx, currentText!!, currentLine, caretPosInLine)
            if (isDoubleClick && currentDoubleClick != null) {
                trace("Draw dblClick")
                CanvasCaret.drawDblClick(
                    ctx,
                    currentText!!,
                    currentDoubleClick!!.first,
                    caretPosInLine,
                    currentDoubleClick!!.second,
                    currentDoubleClick!!.third
                )
            }
        }
        divHolder.style.minHeight = "${posYGlobal + dy + 100}px"
        val asciidoc = ICanvasDrawable.dumpAsciidoc(this)
        textarea.textContent = asciidoc

        traceDeIndent("MainCanvas::draw ${divHolder.clientWidth} $currentText")
    }
}