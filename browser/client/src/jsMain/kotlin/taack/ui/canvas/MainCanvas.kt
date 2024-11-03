package taack.ui.canvas

import kotlinx.browser.window
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
import web.assembly.ImportExportKind.Companion.function
import web.canvas.CanvasRenderingContext2D
import web.clipboard.ClipboardEvent
import web.dom.document
import web.events.Event
import web.events.EventHandler
import web.events.addEventListener
import web.file.FileReader
import web.html.HTMLButtonElement
import web.html.HTMLCanvasElement
import web.html.HTMLDivElement
import web.html.HTMLImageElement
import web.http.CrossOrigin
import web.uievents.DragEvent
import web.uievents.KeyboardEvent
import web.uievents.MouseEvent
import kotlin.math.max
import kotlin.math.min

class MainCanvas(private val divHolder: HTMLDivElement, private val divScroll: HTMLDivElement) {
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

            "F2" -> {
                commandDoList.add(
                    ChangeStyleCommand(drawables, initialDrawables, currentDrawable, H2Canvas(currentText!!.txt))
                )
            }

            "F3" -> {
                commandDoList.add(
                    ChangeStyleCommand(drawables, initialDrawables, currentDrawable, H3Canvas(currentText!!.txt))
                )
            }

            "F4" -> {
                commandDoList.add(
                    ChangeStyleCommand(drawables, initialDrawables, currentDrawable, H4Canvas(currentText!!.txt))
                )
            }

            "F1" -> {
                commandDoList.add(
                    ChangeStyleCommand(drawables, initialDrawables, currentDrawable, PCanvas(currentText!!.txt))
                )
            }

            "F5" -> {
                commandDoList.add(
                    ChangeStyleCommand(drawables, initialDrawables, currentDrawable, LiCanvas(currentText!!.txt))
                )
            }

            "F6" -> {
                commandDoList.add(
                    ChangeStyleCommand(drawables, initialDrawables, currentDrawable, Li2Canvas(currentText!!.txt))
                )
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

    init {
        canvas.id = "canvas"
        canvas.width = window.innerWidth
        canvas.height = window.innerHeight
        canvas.tabIndex = 1
        divHolder.draggable = true
        divHolder.style.border = "1px solid red"
        divScroll.style.border = "1px solid blue"

        val bBold = document.createElement("button") as HTMLButtonElement
        bBold.id = "buttonBold"
        bBold.innerHTML = "<b style='margin: 0;height: 23px;'>BOLD</b>"
        bBold.onclick = EventHandler {
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
        divHolder.appendChild(bBold)
        val bNormal = document.createElement("button") as HTMLButtonElement
        bNormal.id = "buttonNormal"
        bNormal.innerHTML = "<span style='margin: 0;height: 23px;'>Normal</span>"
        bNormal.onclick = EventHandler {
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
        divHolder.appendChild(bNormal)
        val bMono = document.createElement("button") as HTMLButtonElement
        bMono.id = "buttonMono"
        bMono.innerHTML = "<code style='margin: 0;height: 23px;'>Mono</code>"
        bMono.onclick = EventHandler {
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
        divHolder.appendChild(bMono)
        val bBoldMono = document.createElement("button") as HTMLButtonElement
        bBoldMono.id = "buttonBoldMono"
        bBoldMono.innerHTML = "<code style='margin: 0;height: 23px;'><b>Mono</b></code>"
        bBoldMono.onclick = EventHandler {
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
        divHolder.appendChild(bBoldMono)
        val bScript = document.createElement("button") as HTMLButtonElement
        bScript.id = "buttonScript"
        bScript.innerHTML = "<code style='margin: 0;height: 23px;'><em>Kroki</em></code>"
        bScript.onclick = EventHandler {
            if (currentDrawable != null)
                commandDoList.add(
                    ChangeStyleCommand(drawables, initialDrawables, currentDrawable, CanvasKroki(currentText!!.txt))
                )
            draw()
        }
        divHolder.appendChild(bScript)
        val bH2 = document.createElement("button") as HTMLButtonElement
        bH2.id = "bH2"
        bH2.innerHTML =
            "<span style='margin: 0;height: 23px;font-size: 18px; font-weight: bold; color: #ba3925'>H2</span>"
        bH2.onclick = EventHandler {
            if (currentDrawable != null)
                commandDoList.add(
                    ChangeStyleCommand(drawables, initialDrawables, currentDrawable, H2Canvas(currentText!!.txt))
                )
            draw()
        }
        divHolder.appendChild(bH2)
        val bH3 = document.createElement("button") as HTMLButtonElement
        bH3.id = "bH3"
        bH3.innerHTML =
            "<span style='margin: 0;height: 23px;font-size: 16px; font-weight: bold; color: #ba3925'>H3</span>"
        bH3.onclick = EventHandler {
            if (currentDrawable != null)
                commandDoList.add(
                    ChangeStyleCommand(drawables, initialDrawables, currentDrawable, H3Canvas(currentText!!.txt))
                )
            draw()
        }
        divHolder.appendChild(bH3)
        val bH4 = document.createElement("button") as HTMLButtonElement
        bH4.id = "bH4"
        bH4.innerHTML =
            "<span style='margin: 0;height: 23px;font-size: 14px; font-weight: bold; color: #ba3925'>H4</span>"
        bH4.onclick = EventHandler {
            if (currentDrawable != null)
                commandDoList.add(
                    ChangeStyleCommand(drawables, initialDrawables, currentDrawable, H4Canvas(currentText!!.txt))
                )
            draw()
        }
        divHolder.appendChild(bH4)
        val bP = document.createElement("button") as HTMLButtonElement
        bP.id = "bP"
        bP.innerHTML = "<span style='margin: 0;height: 23px;'>P</span>"
        bP.onclick = EventHandler {
            if (currentDrawable != null)
                commandDoList.add(
                    ChangeStyleCommand(drawables, initialDrawables, currentDrawable, PCanvas(currentText!!.txt))
                )
            draw()
        }

        divHolder.appendChild(bP)
        val bBullet = document.createElement("button") as HTMLButtonElement
        bBullet.id = "bBullet"
        bBullet.innerHTML = " • Bullet"
        bBullet.onclick = EventHandler {
            if (currentDrawable != null)
                commandDoList.add(
                    ChangeStyleCommand(drawables, initialDrawables, currentDrawable, LiCanvas(currentText!!.txt))
                )
            draw()
        }
        divHolder.appendChild(bBullet)
        val bBullet2 = document.createElement("button") as HTMLButtonElement
        bBullet2.id = "bBullet2"
        bBullet2.innerHTML = "    ‧ Bullet"
        bBullet2.onclick = EventHandler {
            if (currentDrawable != null)
                commandDoList.add(
                    ChangeStyleCommand(drawables, initialDrawables, currentDrawable, Li2Canvas(currentText!!.txt))
                )
            draw()
        }
        divHolder.appendChild(bBullet2)
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

        window.onresize = {
            trace("window resize")
            canvas.width = document.body!!.offsetWidth
            canvas.height = window.innerHeight - 10
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

        document.onpaste = EventHandler { event: ClipboardEvent ->
            trace("canvasEvent paste")
            val txt = event.clipboardData!!.getData("text")
            commandDoList.add(
                AddCharCommand(
                    currentText!!,
                    txt,
                    caretPosInCurrentText
                )
            )
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
        val h2 = H2Canvas("Topology Filters and Selectors Example for various data layout")
        initialDrawables.add(h2)

        val h3 = H3Canvas("Directed Acyclic Graphs (the most common in computer sciences)")
        initialDrawables.add(h3)

        val p1 =
            PCanvas("DSL are AI friendly, so we want to be able to use more natural language in the future to generate our assets, but generation will be translated into those DSLs, in order to be human editable, efficiently.")
        initialDrawables.add(p1)

        val h31 = H3Canvas("For Assemblies and bodies")
        initialDrawables.add(h31)

        val h4 = H4Canvas("Category")
        initialDrawables.add(h4)

        initialDrawables.add(CanvasTable.createTable())

        val s = CanvasKroki(
            """
            GraphViz
            digraph G {Hello->World}
            """.trimIndent()
        )
        initialDrawables.add(s)

        val p2 = PCanvas(
            """
            Matched by feature, body name, but also by position DSL.
            Matched by feature, body name, but also by position DSL.
            Matched by feature, body name, but also by position DSL.
            Matched by feature, body name, but also by position DSL.
            Matched by feature, body name, but also by position DSL.
            Matched by feature, body name, but also by position DSL.
            Matched by feature, body name, but also by position DSL.
            Matched by feature, body name, but also by position DSL.
            Matched by feature, body name, but also by position DSL.
            Matched by feature, body name, but also by position DSL.
            Matched by feature, body name, but also by position DSL.
            Matched by feature, body name, but also by position DSL.
            Matched by feature, body name, but also by position DSL.
            Matched by feature, body name, but also by position DSL.
            Matched by feature, body name, but also by position DSL.
            Matched by feature, body name, but also by position DSL.
            Matched by feature, body name, but also by position DSL.
            Matched by feature, body name, but also by position DSL.
            Matched by feature, body name, but also by position DSL.
            Matched by feature, body name, but also by position DSL.
            Matched by feature, body name, but also by position DSL.
            Matched by feature, body name, but also by position DSL.
        """.trimIndent()
        )
        initialDrawables.add(p2)
        val p3 = PCanvas(p2.txt)
        initialDrawables.add(p3)
        val p4 = PCanvas(p2.txt)
        initialDrawables.add(p4)

        val image = CanvasImg("https://mdn.github.io/shared-assets/images/examples/rhino.jpg", "Coucou", 0)
        initialDrawables.add(image)
        drawables.addAll(initialDrawables)
    }

    private fun draw() {
        traceIndent("MainCanvas::draw")
        CanvasText.num1 = 0
        CanvasText.num2 = 0
        CanvasText.figNum = 1
        posYGlobal = -dy

        trace("Clear")
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

        trace("Draw all drawables")
        for (text in drawables) {
            posYGlobal = text.draw(ctx, canvas.width.toDouble() - canvasInnerBorder, posYGlobal, canvasInnerBorder)
        }

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
        divHolder.style.height = "${posYGlobal + dy}px"

        traceDeIndent("MainCanvas::draw")
    }
}