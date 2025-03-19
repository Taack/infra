package taack.ui.wysiwyg.canvasMono

import taack.ui.base.Helper.Companion.trace
import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceEnabled
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.base.element.Form
import taack.ui.wysiwyg.canvasMono.command.*
import taack.ui.wysiwyg.canvasMono.item.CanvasCaret
import taack.ui.wysiwyg.canvasMono.text.*
import taack.ui.wysiwyg.parser.AsciidocParser
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
    private val embeddingForm: Form,
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

    val posInTextarea: Int
        get() {
            var res = caretPosInCurrentText
            for (i in 0..<currentDrawableIndex) {
                res += drawables[i].getSelectedText()!!.txtVar.length + 1
            }
            return res
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
        get() = currentText!!.lines[currentText!!.indexOfLine(caretPosInCurrentText)]
    private val _drawables: MutableList<ICanvasDrawable> = mutableListOf()
    internal val drawables = MyMutableList(_drawables)
    private var dy: Double = 0.0
    var caretPosInCurrentText: Int = 0
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

    private val initialText = textarea.defaultValue

    private fun addDrawable() {
        var doNotDraw = false
        when (currentKeyboardEvent!!.key) {
            "Backspace" -> {
                traceIndent("MainCanvas::addDrawable +++ press Backspace $caretPosInCurrentText, $currentDrawableIndex")
                commandDoList.add(
                    DeleteCharMonoCommand(
                        this,
                        posInTextarea
                    )
                )
                caretPosInCurrentText--
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
                commandDoList.add(
                    DeleteCharMonoCommand(this, posInTextarea + 1)
                )
            }

            "Enter" -> {
                trace("MainCanvas::addDrawable press Enter +++")
                commandDoList.add(
                    AddCharMonoCommand(
                        this,
                        posInTextarea,
                        "\n"
                    )
                )
                currentDrawableIndex++
                caretPosInCurrentText = 0
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
                    } else {
                        commandDoList.add(
                            AddCharMonoCommand(
                                this,
                                posInTextarea,
                                currentKeyboardEvent!!.key[0].toString()
                            )
                        )
                        caretPosInCurrentText += 1
                    }
                }
            }
        }
        if (!doNotDraw)
            draw()
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
            var sum = 0
            for (i in 0..<drawables.size) {
                if (drawables[i].isClicked(event.offsetX, event.offsetY)) {
                    currentDrawableIndex = i
                    val text = currentDrawable.getSelectedText(event.offsetX, event.offsetY)!!
                    val currentClick = text.click(ctx, event.offsetX, event.offsetY)
                    caretPosInCurrentText = currentClick!!.second
                    break
                }
                sum += drawables[i].getSelectedText()!!.txtVar.length
            }
            traceDeIndent("canvas click => posInTextarea $posInTextarea $caretPosInCurrentText, $currentDrawableIndex, ${currentText?.txt}")
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
                    AddCharMonoCommand(
                        this,
                        posInTextarea,
                        txt
                    )
                )
                caretPosInCurrentText += txt.length
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
                AddCharMonoCommand(
                    this,
                    posInTextarea,
                    txt
                )
            )
            caretPosInCurrentText += txt.length
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
        drawables.addAll(AsciidocParser().parse(textarea.value))
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
            currentDrawableIndex = index + 1
        }
        reader.readAsDataURL(file)


        if (embeddingForm.mapFileToSend["${textarea.name}File"] == null) {
            embeddingForm.mapFileToSend["${textarea.name}File"] = mutableListOf()
        }
        embeddingForm.mapFileToSend["${textarea.name}File"]!!.add(file)
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
        textarea.value = initialText

        trace("Execute commandList, textarea.value: ${textarea.value}")
        val executedCommands = mutableListOf<ICanvasCommand>()
        for (cmd in commandDoList) {
            if (cmd.doIt()) {
                trace("Command executed successfully, currentDrawableIndex: $currentDrawableIndex")
                executedCommands.add(cmd)
            } else trace("$cmd does not do it !!")
        }
        commandDoList.clear()
        commandDoList.addAll(executedCommands)


        drawables.addAll(AsciidocParser().parse(textarea.value))
        trace("Draw all drawables +++, drawables: $drawables")

        for (text in drawables) {
            try {
                posYGlobal = text.draw(ctx, divHolder.clientWidth - canvasInnerBorder, posYGlobal, canvasInnerBorder)
            } catch (e: Throwable) {
                trace(e.message ?: "")
            }
        }
        trace("Draw all drawables ---, textarea.value: ${textarea.value}")

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
        } else if (caretPosInCurrentText == 0) {
            CanvasCaret.draw(ctx, null, null, 0)
        }
        divHolder.style.minHeight = "${posYGlobal + dy + 100}px"

        traceDeIndent("MainCanvas::draw ${divHolder.clientWidth} $currentText")
        traceEnabled = true
    }
}