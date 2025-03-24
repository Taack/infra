package taack.ui.wysiwyg.canvasMono

import taack.ui.base.Helper.Companion.trace
import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceEnabled
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.base.element.Form
import taack.ui.wysiwyg.canvasMono.command.*
import taack.ui.wysiwyg.canvasMono.item.CanvasCaret
import taack.ui.wysiwyg.canvasMono.text.*
import web.canvas.CanvasRenderingContext2D
import web.clipboard.ClipboardEvent
import web.dom.document
import web.events.Event
import web.events.EventHandler
import web.events.addEventListener
import web.file.File
import web.file.FileReader
import web.html.*
import web.uievents.DragEvent
import web.uievents.KeyboardEvent
import web.uievents.MouseEvent
import web.window.window
import kotlin.math.floor
import kotlin.math.min

class MainCanvas(
    internal val embeddingForm: Form,
    internal val textarea: HTMLTextAreaElement,
    private val divHolder: HTMLDivElement,
    private val divScroll: HTMLDivElement
) {

    inner class MyMutableList(private val b: MutableList<ICanvasDrawable>) : MutableList<ICanvasDrawable> by b {
        override fun add(element: ICanvasDrawable): Boolean {
            return b.add(element)
        }

        override fun add(index: Int, element: ICanvasDrawable) {
            return b.add(index, element)
        }

        override fun removeAt(index: Int): ICanvasDrawable {
            if (index > 0) {
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
            if (i > 0) {
                b.removeAt(i)
                return true
            } else if (i == 0 && b.size > 1) {
                b.removeAt(0)
                b.add(0, PCanvas(""))
                return true
            }
            return false
        }
    }

    private var currentTableRowIndex = 0

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
        get() = currentText!!.lines[currentText!!.indexOfLine(caretPosInCurrentText)]
    private val _drawables: MutableList<ICanvasDrawable> = mutableListOf()
    internal val drawables = MyMutableList(_drawables)
    private val initialDrawables = mutableListOf<ICanvasDrawable>()
    private var dy: Double = 0.0
    private var caretPosInCurrentText: Int = 0
    private var currentDrawableIndex: Int = 0
    private val currentDrawable: ICanvasDrawable
        get() = drawables[currentDrawableIndex]
    private val currentText: CanvasText?
        get() {
            if (currentDrawableIndex < drawables.size) {
                return drawables[currentDrawableIndex].getSelectedText(
                    currentMouseEvent?.offsetX,
                    currentMouseEvent?.offsetY
                )
            } else
                return null
        }
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
                traceIndent("MainCanvas::addDrawable +++ press Backspace $caretPosInCurrentText, $currentDrawableIndex")
                if (caretPosInCurrentText == 0 && currentDrawableIndex > 0) {
                    val txt = currentText!!.txt
                    val i = currentDrawableIndex
                    commandDoList.add(
                        DeleteDrawableCommand(
                            drawables, i
                        )
                    )
                    caretPosInCurrentText = currentText!!.txt.length
                    currentDrawableIndex = i - 1
                    commandDoList.add(
                        AddCharCommand(
                            drawables,
                            currentDrawableIndex,
                            currentTableRowIndex,
                            null,
                            txt,
                            currentMouseEvent
                        )
                    )
                } else if (caretPosInCurrentText > 0)
                    commandDoList.add(
                        RmCharCommand(
                            drawables,
                            currentDrawableIndex,
                            currentTableRowIndex,
                            caretPosInCurrentText--,
                            currentMouseEvent
                        )
                    )
                traceDeIndent("MainCanvas::addDrawable --- press Backspace $currentDrawableIndex ${currentMouseEvent?.offsetX} ${currentMouseEvent?.offsetY}")
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
                        DeleteCharCommand(
                            drawables,
                            currentDrawableIndex,
                            currentTableRowIndex,
                            pos1,
                            pos2,
                            currentMouseEvent
                        )
                    )
                }
            }

            "Enter" -> {
                traceIndent("MainCanvas::addDrawable press Enter +++ $caretPosInCurrentText, $currentDrawableIndex, ${currentKeyboardEvent!!.shiftKey}")
                val i = currentDrawableIndex

                if (caretPosInCurrentText == 0) {
                    val d = PCanvas("")
                    commandDoList.add(
                        AddDrawableCommand(drawables, i, d)
                    )
                } else {
                    val i2 = i + 1

                    val initTxt = ""
                    when (currentText) {
                        is H2Canvas -> {
                            val d = H3Canvas(initTxt)
                            commandDoList.add(
                                AddDrawableCommand(drawables, i2, d)
                            )
                        }

                        is H3Canvas -> {
                            val d = H4Canvas(initTxt)
                            commandDoList.add(
                                AddDrawableCommand(drawables, i2, d)
                            )
                        }


                        else -> {
                            val d = PCanvas(initTxt)
                            commandDoList.add(
                                AddDrawableCommand(drawables, i2, d)
                            )
                        }
                    }
                    currentDrawableIndex = i2
                }

                traceDeIndent("MainCanvas::addDrawable press Enter --- $caretPosInCurrentText, $currentDrawableIndex")
            }

            "ArrowUp" -> {
                trace("MainCanvas::addDrawable press ArrowUp value: currentDrawableIndex: $currentDrawableIndex")
                if (currentDrawableIndex > 0) {
                    currentDrawableIndex--
                    caretPosInCurrentText = min(currentLine.length, caretPosInCurrentText)
                }
            }

            "ArrowDown" -> {
                trace("MainCanvas::addDrawable press ArrowDown currentDrawableIndex: $currentDrawableIndex")
                if (currentDrawableIndex < drawables.size - 1) {
                    currentDrawableIndex++
                    caretPosInCurrentText = min(currentLine.length, caretPosInCurrentText)
                }
            }

            "ArrowLeft" -> {
                if (caretPosInCurrentText > 0) caretPosInCurrentText--
                else if (currentDrawableIndex > 0) {
                    currentDrawableIndex--
                    caretPosInCurrentText = currentText!!.txt.length
                }
            }

            "ArrowRight" -> {
                if (currentKeyboardEvent!!.ctrlKey && isDoubleClick) {
                    val decay =
                        currentText!!.txt.substring(charSelectEndNInText!! + 1).indexOfFirst { !it.isLetter() } + 1
                    if (decay == 0) {
                        charSelectEndNInText = currentText!!.txt.length
                    }
                    charSelectEndNInText = charSelectEndNInText?.plus(decay)
                } else if (currentText!!.txt.length > caretPosInCurrentText) {
                    caretPosInCurrentText++
                } else if (currentDrawableIndex < drawables.size - 1) {
                    currentDrawableIndex++
                    caretPosInCurrentText = 0
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
                                    drawables,
                                    currentDrawableIndex,
                                    currentTableRowIndex,
                                    caretPosInCurrentText++,
                                    currentKeyboardEvent!!.key[0].toString(),
                                    currentMouseEvent
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

        createButton(
            "bH2",
            "<span style='margin: 0;height: 23px;font-size: 18px; font-weight: bold; color: #ba3925'>H2</span>"
        ) {
            val d = H2Canvas(currentText!!.txt)
            commandDoList.add(
                ChangeStyleCommand(drawables, currentDrawableIndex, d)
            )
            draw()
        }
        createButton(
            "bH3",
            "<span style='margin: 0;height: 23px;font-size: 16px; font-weight: bold; color: #ba3925'>H3</span>"
        ) {
            val d = H3Canvas(currentText!!.txt)
            commandDoList.add(
                ChangeStyleCommand(drawables, currentDrawableIndex, d)
            )

            draw()
        }
        createButton(
            "bH4",
            "<span style='margin: 0;height: 23px;font-size: 14px; font-weight: bold; color: #ba3925'>H4</span>"
        ) {
            val d = H4Canvas(currentText!!.txt)
            commandDoList.add(
                ChangeStyleCommand(drawables, currentDrawableIndex, d)
            )
            draw()
        }
        createButton("bP", "<span style='margin: 0;height: 23px;'>P</span>") {
            val d = PCanvas(currentText!!.txt)
            commandDoList.add(
                ChangeStyleCommand(drawables, currentDrawableIndex, d)
            )
            draw()
        }
        createButton("bBullet", " • ") {
            val d = LiCanvas(currentText!!.txt)
            commandDoList.add(
                ChangeStyleCommand(drawables, currentDrawableIndex, d)
            )
            draw()
        }
        createButton("bBullet2", "    ‧ ") {
            val d = Li2Canvas(currentText!!.txt)

            commandDoList.add(
                ChangeStyleCommand(drawables, currentDrawableIndex, d)
            )
            draw()
        }

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
            traceDeIndent("canvas click => $caretPosInCurrentText, $currentDrawableIndex, ${currentText?.txt}")
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
            for (i in 0..<drawables.size) {
                if (drawables[i].isClicked(event.offsetX, event.offsetY)) {
                        currentDrawableIndex = i
                        currentDoubleClick = currentDrawable.doubleClick(ctx, event.offsetX, event.offsetY)
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
                        drawables,
                        currentDrawableIndex,
                        currentTableRowIndex,
                        caretPosInCurrentText,
                        txt,
                        currentMouseEvent
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
                    drawables,
                    currentDrawableIndex,
                    currentTableRowIndex,
                    caretPosInCurrentText,
                    txt,
                    currentMouseEvent
                )
            )

            trace("canvasEvent drop on ${textarea.name}: $txt")
        }

        divHolder.ondragover = EventHandler { event: DragEvent ->
            event.preventDefault()
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

        reader.onload = EventHandler {
            var index = currentDrawableIndex
            if (index == -1) {
                index = 0
            }
            val d = PCanvas("")
            currentDrawableIndex = index + 1
            commandDoList.add(
                AddDrawableCommand(drawables, index + 1, d)
            )
        }
        reader.readAsDataURL(file)


        if (embeddingForm.mapFileToSend["${textarea.name}File"] == null) {
            embeddingForm.mapFileToSend["${textarea.name}File"] = mutableListOf()
        }
        embeddingForm.mapFileToSend["${textarea.name}File"]!!.add(file)
    }

    private fun addInitialTexts() {
        if (textarea.innerText.isNotBlank()) {
            trace("addInitialTexts ${textarea.innerText}")
          //TODO  initialDrawables.addAll(ICanvasDrawable.readAsciidoc(this))
        } else {
            trace("addInitialTexts BLANK")
            initialDrawables.add(PCanvas(""))
        }

        drawables.addAll(initialDrawables)
    }

    private fun draw() {
        traceEnabled = true
        traceIndent("MainCanvas::draw, currentDrawableIndex: $currentDrawableIndex")
        if (divHolder.clientWidth > 0) {
            canvas.width = floor(divHolder.clientWidth * dprX).toInt()
            canvas.style.width = "${divHolder.clientWidth}px"
        } else trace("divHolder.clientWidth == 0 !!!")
        if (divScroll.clientHeight > 0) {
            canvas.height = floor(divScroll.clientHeight * dprY).toInt()
            canvas.style.height = "${divScroll.clientHeight}px"
        } else trace("divScroll.clientHeight == 0 !!!")
        ctx.scale(dprX, dprY)

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

        trace("Execute commandList, currentDrawableIndex: $currentDrawableIndex")
        val executedCommands = mutableListOf<ICanvasCommand>()
        for (cmd in commandDoList) {
            if (cmd.doIt()) {
                trace("Command executed successfully, currentDrawableIndex: $currentDrawableIndex")
                executedCommands.add(cmd)
            } else trace("$cmd does not do it !!")
        }
        commandDoList.clear()
        commandDoList.addAll(executedCommands)

        trace("Draw all drawables +++, currentDrawableIndex: $currentDrawableIndex")
        for (text in drawables) {
            try {
                posYGlobal = text.draw(ctx, divHolder.clientWidth - canvasInnerBorder, posYGlobal, canvasInnerBorder)
            } catch (e: Throwable) {
                trace(e.message ?: "")
            }
        }
        trace("Draw all drawables ---, currentDrawableIndex: $currentDrawableIndex")

        if (currentText != null) {
            trace("Draw caret currentText != null, currentDrawableIndex: ${currentDrawableIndex}, caretPosInCurrentText = $caretPosInCurrentText, caretPosInLine = $caretPosInLine, currentLine!!.length = ${currentLine.length}")
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
        val asciidoc = "ICanvasDrawable.dumpAsciidoc(this)"
//TODO        val asciidoc = ICanvasDrawable.dumpAsciidoc(this)
        textarea.textContent = asciidoc

        traceDeIndent("MainCanvas::draw ${divHolder.clientWidth} $currentText")
        traceEnabled = true
    }
}