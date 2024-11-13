package taack.ui.canvas

import taack.ui.base.Helper.Companion.trace
import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.canvas.command.*
import taack.ui.canvas.item.CanvasCaret
import taack.ui.canvas.item.CanvasImg
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
import web.file.FileReader
import web.html.*
import web.http.CrossOrigin
import web.prompts.prompt
import web.uievents.DragEvent
import web.uievents.KeyboardEvent
import web.uievents.MouseEvent
import web.window.window
import kotlin.math.max
import kotlin.math.min

class MainCanvas(private val textarea: HTMLTextAreaElement, private val divHolder: HTMLDivElement, private val divScroll: HTMLDivElement) {
    val canvas: HTMLCanvasElement = document.createElement("canvas") as HTMLCanvasElement
    private val canvasInnerBorder = 10.0
    private val ctx: CanvasRenderingContext2D =
        canvas.getContext(CanvasRenderingContext2D.ID) as CanvasRenderingContext2D
    private val texts: List<CanvasText>
        get() = drawables.mapNotNull { it.getSelectedText(currentMouseEvent?.offsetX, currentMouseEvent?.offsetY) }
            .toMutableList()
    private val lineOverLine: CanvasLine
        get() {
            val i = currentText!!.indexOfLine(_caretPosInCurrentText)
            return if (i > 0)
                currentText!!.lines[i - 1]
            else
                currentText!!.lines[0]
        }
    private val currentLine: CanvasLine
        get() = currentText!!.lines[currentText!!.indexOfLine(_caretPosInCurrentText)]
    private val drawables = mutableListOf<ICanvasDrawable>()
    private val initialDrawables = mutableListOf<ICanvasDrawable>()
    private var dy: Double = 0.0
    private var _caretPosInCurrentText: Int = 0
    private var caretPosInCurrentText: Int
        get() = _caretPosInCurrentText
        set(value) = run {
            var v = value
            val decrease = _caretPosInCurrentText - value
            if (caretPosInLine < decrease) {
                val i = currentText!!.indexOfLine(currentLine)
                if (i <= 0) {
                    val j = texts.indexOf(currentText) - 1
                    if (j >= 0) {
                        currentDrawable = texts[j]
                        v = currentText!!.lines.last().posEnd
                    } else {
                        v = 0
                    }
                }
            } else if (caretPosInLine - decrease > currentLine.length) {
                val i = currentText!!.indexOfLine(currentLine)
                if (i < currentText!!.lines.size - 1) {
                    v = value
                } else {
                    val j = texts.indexOf(currentText) + 1
                    if (j < texts.size) {
                        currentDrawable = texts[j]
                        v = 0
                    }
                }
            }
            _caretPosInCurrentText = v
        }
    private var currentDrawable: ICanvasDrawable? = null
    private val currentText: CanvasText?
        get() = currentDrawable?.getSelectedText(currentMouseEvent?.offsetX, currentMouseEvent?.offsetY)
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
        get() = _caretPosInCurrentText - currentLine.posBegin

    private fun addDrawable() {
        var doNotDraw = false
        when (currentKeyboardEvent!!.key) {
            "Backspace" -> {
                trace("MainCanvas::addDrawable press Backspace")
                commandDoList.add(
                    RmCharCommand(
                        drawables,
                        currentDrawable!!.getSelectedText(currentMouseEvent?.offsetX, currentMouseEvent?.offsetY)!!,
                        caretPosInCurrentText--
                    )
                )
            }

            "Tab" -> {
                trace("MainCanvas::addDrawable press Delete")
                if (currentDrawable != null)
                    if (currentKeyboardEvent!!.shiftKey)
                        commandDoList.add(
                            DeIndentCommand(currentDrawable!!)
                        )
                    else
                        commandDoList.add(
                            IndentCommand(currentDrawable!!)
                        )

            }

            "Delete" -> {
                trace("MainCanvas::addDrawable press Delete")
                if (currentKeyboardEvent!!.ctrlKey && currentDrawable != null) {
                    commandDoList.add(
                        DeleteTextCommand(drawables, currentDrawable!!.getSelectedText()!!)
                    )
                } else {
                    val pos1 = caretPosInCurrentText
                    val pos2: Int? = null
                    commandDoList.add(
                        DeleteCharCommand(drawables, currentDrawable!!.getSelectedText()!!, pos1, pos2)
                    )
                }
            }

            "Enter" -> {
                trace("MainCanvas::addDrawable press Enter")
                if (currentDrawable is CanvasKroki) {
                    commandDoList.add(
                        AddCharCommand(
                            currentText!!,
                            "\n",
                            caretPosInCurrentText++
                        )
                    )
                } else {
                    val i = drawables.indexOf(currentText!!) + 1
                    if (currentKeyboardEvent!!.ctrlKey && currentDrawable !is CanvasTable) {
                        commandDoList.add(
                            AddTableCommand(drawables, i)
                        )
                    } else
                        when (currentText) {
                            is H2Canvas -> {
                                commandDoList.add(
                                    AddTextCommand(drawables, i, H3Canvas(">"))
                                )
                            }

                            is H3Canvas -> {
                                commandDoList.add(
                                    AddTextCommand(drawables, i, H4Canvas(">"))
                                )
                            }

                            is TxtHeaderCanvas -> {
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
                                commandDoList.add(
                                    AddTextCommand(drawables, i, PCanvas(">"))
                                )
                            }
                        }
                }
            }

            "ArrowUp" -> {
                caretPosInCurrentText -= (if (caretPosInCurrentText == currentText!!.txt.length) 1 else 0) + lineOverLine.length
            }

            "ArrowDown" -> {
                caretPosInCurrentText += (if (caretPosInCurrentText == 0) 1 else 0) + currentLine.length
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
                        currentDrawable = texts.last()
                    }
                    caretPosInCurrentText = currentText!!.lines.last().posEnd - 1
                }
                caretPosInCurrentText = currentLine.posEnd
            }

            "Home" -> {
                trace("MainCanvas::addDrawable press Home")
                if (currentKeyboardEvent!!.ctrlKey) {
                    if (currentKeyboardEvent!!.shiftKey) {
                        currentDrawable = texts.first()
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
                                    caretPosInCurrentText++
                                )
                            )
                        }
                }
            }
        }
        if (!doNotDraw)
            draw()
    }


    /*
    *
    * public inline fun <E : Event, C : EventTarget, T : EventTarget, D : E> EventHandler(
    noinline handler: (D) -> Unit
): EventHandler<E, C, T> where D : HasTargets<C, T>
    * */

    private fun createButton(id: String, innerHtml: String, handler: () -> Unit) {
        val b = document.createElement("button") as HTMLButtonElement
        b.id = id + textarea.name
        b.innerHTML = innerHtml
        b.type = ButtonType.button
        b.classList.add("btn")
        b.classList.add("btn-light")
        b.style.margin = "2px"
        b.style.height = "29px"
        b.style.width = "80px"
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
        canvas.width = divHolder.clientWidth
        canvas.height = window.innerHeight
        canvas.tabIndex = 1
        canvas.style.border = "1px solid black"
        divHolder.draggable = true
        divHolder.contentEditable = "true"
        divHolder.style.border = "1px solid red"
        divScroll.style.border = "1px solid blue"

        createButton("buttonBold", "<b style='margin: 0;height: 23px;'>BOLD</b>") {
            if (currentDrawable != null && currentDoubleClick != null)
                commandDoList.add(
                    AddStyleCommand(
                        currentText!!,
                        CanvasStyle.Type.BOLD,
                        currentDoubleClick!!.second,
                        currentDoubleClick!!.third
                    )
                )
            draw()
        }
        createButton("buttonNormal","<span style='margin: 0;height: 23px;'>Normal</span>") {
            if (currentDrawable != null && currentDoubleClick != null)
                commandDoList.add(
                    AddStyleCommand(
                        currentText!!,
                        CanvasStyle.Type.NORMAL,
                        currentDoubleClick!!.second,
                        currentDoubleClick!!.third
                    )
                )
            draw()
        }
        createButton("buttonMono", "<code style='margin: 0;height: 23px;'>Mono</code>") {
            if (currentDrawable != null && currentDoubleClick != null)
                commandDoList.add(
                    AddStyleCommand(
                        currentText!!,
                        CanvasStyle.Type.MONOSPACED,
                        currentDoubleClick!!.second,
                        currentDoubleClick!!.third
                    )
                )
            draw()
        }
        createButton("buttonBoldMono", "<code style='margin: 0;height: 23px;'><b>Mono</b></code>") {
            if (currentDrawable != null && currentDoubleClick != null)
                commandDoList.add(
                    AddStyleCommand(
                        currentText!!,
                        CanvasStyle.Type.BOLD_MONOSPACED,
                        currentDoubleClick!!.second,
                        currentDoubleClick!!.third
                    )
                )
            draw()
        }
        createButton("buttonScript", "<code style='margin: 0;height: 23px;'><em>Kroki</em></code>") {
            if (currentDrawable != null)
                commandDoList.add(
                    ChangeStyleCommand(drawables, initialDrawables, currentDrawable, CanvasKroki(currentText!!.txt))
                )
            draw()
        }
        createButton("bH2", "<span style='margin: 0;height: 23px;font-size: 18px; font-weight: bold; color: #ba3925'>H2</span>") {
            if (currentDrawable != null)
                commandDoList.add(
                    ChangeStyleCommand(drawables, initialDrawables, currentDrawable, H2Canvas(currentText!!.txt))
                )
            draw()
        }
        createButton("bH3", "<span style='margin: 0;height: 23px;font-size: 16px; font-weight: bold; color: #ba3925'>H3</span>") {
            if (currentDrawable != null)
                commandDoList.add(
                    ChangeStyleCommand(drawables, initialDrawables, currentDrawable, H3Canvas(currentText!!.txt))
                )
            draw()
        }
        createButton("bH4", "<span style='margin: 0;height: 23px;font-size: 14px; font-weight: bold; color: #ba3925'>H4</span>") {
            if (currentDrawable != null)
                commandDoList.add(
                    ChangeStyleCommand(drawables, initialDrawables, currentDrawable, H4Canvas(currentText!!.txt))
                )
            draw()
        }
        createButton("bP", "<span style='margin: 0;height: 23px;'>P</span>") {
            if (currentDrawable != null)
                commandDoList.add(
                    ChangeStyleCommand(drawables, initialDrawables, currentDrawable, PCanvas(currentText!!.txt))
                )
            draw()
        }
        createButton("bBullet", " • ") {
            if (currentDrawable != null)
                commandDoList.add(
                    ChangeStyleCommand(drawables, initialDrawables, currentDrawable, LiCanvas(currentText!!.txt))
                )
            draw()
        }
        createButton("bBullet2", "    ‧ ") {
            if (currentDrawable != null)
                commandDoList.add(
                    ChangeStyleCommand(drawables, initialDrawables, currentDrawable, Li2Canvas(currentText!!.txt))
                )
            draw()
        }
        createButton("bAsciidoc", "ADoc") {
            draw()
            val asciidoc = ICanvasDrawable.dumpAsciidoc(drawables)
            textarea.textContent = asciidoc
            prompt("Copy to clipboard: Ctrl+C, Enter", asciidoc)
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
            trace("canvas click")
            isDoubleClick = false
            if (event.detail == 3) {
                isDoubleClick = true
                charSelectStartNInText = 0
                charSelectEndNInText =
                    currentDrawable?.getSelectedText(event.offsetX, event.offsetY)!!.txt.length
                trace("canvas click double click == triple click")
            }

            trace("setting currentMouseEvent = $event")
            currentMouseEvent = event
            event.preventDefault()
            event.stopPropagation()
            for (d in drawables) {
                if (d.isClicked(event.offsetX, event.offsetY)) {
                    currentDrawable = d
                    val text = d.getSelectedText(event.offsetX, event.offsetY)!!
                    val currentClick = text.click(ctx, event.offsetX, event.offsetY)
                    _caretPosInCurrentText = currentClick!!.second
                }
            }
            draw()
        }

        canvas.onkeydown = EventHandler { event: KeyboardEvent ->
            currentKeyboardEvent = event
            if (!event.ctrlKey) isDoubleClick = false

            addDrawable()
            event.preventDefault()
            event.stopPropagation()

        }

        canvas.ondblclick = EventHandler { event: MouseEvent ->
            trace("canvas dblclick")
            event.preventDefault()
//            event.stopPropagation()
            isDoubleClick = true
            for (d in drawables) {
                if (d.isClicked(event.offsetX, event.offsetY)) {
                    currentDrawable = d
                    currentDoubleClick = d.doubleClick(ctx, event.offsetX, event.offsetY)
                }
            }
            draw()
        }

        divHolder.onpaste = EventHandler { event: ClipboardEvent ->
            trace("canvasEvent paste $currentText $currentMouseEvent")
            val txt = event.clipboardData!!.getData("text")
            event.preventDefault()
            event.stopPropagation()
            if (currentText != null) {
                commandDoList.add(
                    AddCharCommand(
                        currentText!!,
                        txt,
                        caretPosInCurrentText
                    )
                )
            }
            trace("canvasEvent paste: $txt")
            draw()
        }

        divHolder.ondrop = EventHandler { event: DragEvent ->
            trace("canvasEvent drop")
            event.preventDefault()
            if (event.dataTransfer?.items?.length!! > 0) {
                // Use DataTransferItemList interface to access the file(s)
                for (item in event.dataTransfer?.items!!) {
                    // If dropped items aren't files, reject them
                    if (item.kind === "file") {
                        val file = item.getAsFile()
                        trace("canvasEvent1 file[].name = ${file?.name}")
                        if (file != null) {
                            val reader = FileReader()
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
                                    ctx!!.drawImage(img, 0.0, 0.0, img.width.toDouble(), img.height.toDouble(), 0.0, 0.0, c.width.toDouble(), c.height.toDouble())

                                    val dataurl = c.toDataURL(file.type)
                                    commandDoList.add(
                                        AddImageCommand(
                                            drawables,
                                            drawables.indexOf(currentDrawable),
                                            CanvasImg(dataurl, file.name, 0),
                                        )
                                    )
                                }
                                img.src = reader.result.toString()

                            }
                            reader.readAsDataURL(file)
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
            trace("canvasEvent drop: $txt")
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
        draw()
    }

    private fun addInitialTexts() {
        if (textarea.innerText.isNotBlank()) {
            trace("addInitialTexts ${textarea.innerText}")
            initialDrawables.add(PCanvas(textarea.innerText))
        } else {
            trace("addInitialTexts BLANK")
            initialDrawables.add(PCanvas("Type Here"))
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
//        val image = CanvasImg("https://mdn.github.io/shared-assets/images/examples/rhino.jpg", "Coucou", 0)
//        initialDrawables.add(image)
        drawables.addAll(initialDrawables)
    }

    private fun draw() {
        traceIndent("MainCanvas::draw")
        canvas.width = divHolder.clientWidth
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
        for (cmd in commandDoList) {
            cmd.doIt()
        }

        trace("Draw all drawables +++")
        for (text in drawables) {
            try {
                posYGlobal = text.draw(ctx, canvas.width.toDouble() - canvasInnerBorder, posYGlobal, canvasInnerBorder)
            } catch (e: Throwable) {
                trace(e.message ?: "")
            }
        }
        trace("Draw all drawables ---")

        trace("currentText == $currentText")
        if (currentText != null) {
            trace("Draw caret currentLine != null caretPosInLine = $caretPosInLine, currentLine!!.length = ${currentLine.length}")
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
        divHolder.style.minHeight = "${posYGlobal + dy}px"

        traceDeIndent("MainCanvas::draw ${divHolder.clientWidth} $currentText")
    }
}