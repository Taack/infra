package taack.ui.canvas

import kotlinx.browser.document
import kotlinx.browser.window
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.canvas.command.*
import taack.ui.canvas.item.CanvasCaret
import taack.ui.canvas.item.CanvasImg
import taack.ui.canvas.item.Menu
import taack.ui.canvas.item.MenuEntry
import taack.ui.canvas.table.CanvasTable
import taack.ui.canvas.table.TxtHeaderCanvas
import taack.ui.canvas.table.TxtRowCanvas
import taack.ui.canvas.text.*
import web.canvas.CanvasRenderingContext2D
import web.events.Event
import web.events.EventHandler
import web.events.addEventListener
import web.html.HTMLCanvasElement
import web.html.HTMLDivElement
import web.uievents.KeyboardEvent
import web.uievents.MouseEvent

class MainCanvas(private val divHolder: HTMLDivElement, private val divScroll: HTMLDivElement) {
    val canvas: HTMLCanvasElement = document.createElement("canvas") as HTMLCanvasElement
    private val canvasInnerBorder = 10.0
    private val ctx: CanvasRenderingContext2D =
        canvas.getContext(CanvasRenderingContext2D.ID) as CanvasRenderingContext2D
    private val texts: List<CanvasText>
        get() = drawables.mapNotNull { it.getSelectedText(currentMouseEvent?.offsetX, currentMouseEvent?.offsetY) }
            .toMutableList()
    private var currentLine: CanvasLine?
        get() = currentClick?.first
        set(value) = run { currentClick = currentClick?.copy(first = value) }
    private val drawables = mutableListOf<ICanvasDrawable>()
    private val initialDrawables = mutableListOf<ICanvasDrawable>()
    private val divContainer: HTMLDivElement = document.createElement("div") as HTMLDivElement
    private var dy: Double = 0.0
    private var caretPosInCurrentText: Int
        get() = currentClick?.second ?: 0
        set(value) = run { currentClick = currentClick?.copy(second = value) }
    private var currentDrawable: ICanvasDrawable? = null
    private val currentText: CanvasText?
        get() = currentDrawable?.getSelectedText(currentMouseEvent?.offsetX, currentMouseEvent?.offsetY)
    private var currentClick: Pair<CanvasLine?, Int>? = null
    private var currentDoubleClick: Triple<CanvasLine, Int, Int>? = null
    private var currentMouseEvent: MouseEvent? = null
    private var currentKeyboardEvent: KeyboardEvent? = null
    private var charOffset: Int = 0
    private var wordOffset: Int = 0
    private var isDoubleClick: Boolean = false
    private var charSelectStartNInText: Int?
        get() = currentDoubleClick?.second
        set(value) = run { currentDoubleClick = currentDoubleClick?.copy(second = value!!) }
    private var charSelectEndNInText: Int?
        get() = currentDoubleClick?.third
        set(value) = run { currentDoubleClick = currentDoubleClick?.copy(third = value!!) }
    private var currentMenuEntries: List<MenuEntry>? = null
    private var menu: Menu? = null
    private var posYGlobal: Double = 0.0
    private var recomputeCurrentLineAfterDraw = false
    private val commandDoList = mutableListOf<ICanvasCommand>()
    private val commandUndoList = mutableListOf<ICanvasCommand>()

    private fun changeTextCanvasStyle(text: CanvasText) {
        trace("MainCanvas::changeTextCanvasStyle")
        if (currentDrawable != null && !(currentDrawable is CanvasTable)) {
            val i = drawables.indexOf(currentDrawable!!)
            if (i != -1) {
                drawables.remove(currentDrawable!!)
                drawables.add(i, text)
                currentDrawable = text
                currentLine = null
            }
        }

    }

    private fun addDrawable() {
        var doNotDraw = false
        if (menu == null)
            when (currentKeyboardEvent!!.key) {
                "Backspace" -> {
                    trace("MainCanvas::addDrawable press Backspace")
                    commandDoList.add(
                        RmCharCommand(
                            drawables,
                            currentDrawable!!.getSelectedText(currentMouseEvent?.offsetX, currentMouseEvent?.offsetY)!!,
                            caretPosInCurrentText + charOffset--
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
                        val pos1 = caretPosInCurrentText + charOffset
                        val pos2: Int? = null
                        commandDoList.add(
                            DeleteCharCommand(drawables, currentDrawable!!.getSelectedText()!!, pos1, pos2)
                        )
                    }
                }

                "Enter" -> {
                    trace("MainCanvas::addDrawable press Enter")
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
                    currentLine = null
                }

                "ArrowUp" -> {
                    trace("MainCanvas::addDrawable press ArrowUp")
                    if (currentLine == null) {
                        val j = texts.indexOf(currentText) - 1
                        if (j >= 0) {
                            currentDrawable = texts[j]
                            currentLine = currentText!!.lines.last()
                        }
                    } else {
                        val i = currentText!!.findLine(currentLine!!)
                        if (i > 0 && i < currentText!!.lines.size) {
                            caretPosInCurrentText -= currentLine!!.posBegin
                            currentLine = currentText!!.lines[i - 1]
                            caretPosInCurrentText += currentLine!!.posBegin
                        } else {
                            val j = texts.indexOf(currentText) - 1
                            if (j >= 0) {
                                currentDrawable = texts[j]
                                currentLine = currentText!!.lines.last()
                            }
                        }
                    }
                }

                "ArrowDown" -> {
                    trace("MainCanvas::addDrawable press ArrowDown")
                    if (currentLine == null) {
                        val j = texts.indexOf(currentText) + 1
                        if (j > 0 && j < texts.size) {
                            currentDrawable = texts[j]
                            currentLine = currentText!!.lines.first()
                        }
                    } else {
                        val i = currentText!!.findLine(currentLine!!)

                        if (i >= 0 && i < currentText!!.lines.size - 1) {
                            caretPosInCurrentText -= currentLine!!.posBegin
                            currentLine = currentText!!.lines[i + 1]
                            caretPosInCurrentText += currentLine!!.posBegin
                        } else {
                            val j = texts.indexOf(currentText) + 1
                            if (j > 0 && j < texts.size) {
                                currentDrawable = texts[j]
                                currentLine = currentText!!.lines.first()
                            }
                        }
                    }
                }

                "ArrowRight" -> {
                    trace("MainCanvas::addDrawable press ArrowRight")
                    if (currentKeyboardEvent!!.ctrlKey && isDoubleClick) {
                        val decay = currentText!!.txt.substring(charSelectEndNInText!! + 1).indexOf(' ') + 1
                        if (decay == 0) {
                            charSelectEndNInText = currentText!!.txt.length
                        }
                        charSelectEndNInText = charSelectEndNInText?.plus(decay)
                    } else {
                        charOffset++
                    }
                }

                "F2" -> {
                    changeTextCanvasStyle(H2Canvas(currentText!!.txt))
                }

                "F3" -> {
                    changeTextCanvasStyle(H3Canvas(currentText!!.txt))
                }

                "F4" -> {
                    changeTextCanvasStyle(H4Canvas(currentText!!.txt))
                }

                "F1" -> {
                    changeTextCanvasStyle(PCanvas(currentText!!.txt))
                }

                "F5" -> {
                    changeTextCanvasStyle(LiCanvas(currentText!!.txt))
                }

                "F6" -> {
                    changeTextCanvasStyle(Li2Canvas(currentText!!.txt))
                }

                "End" -> {
                    trace("MainCanvas::addDrawable press End")
                    charOffset = 0
                    if (currentKeyboardEvent!!.ctrlKey) {
                        if (currentKeyboardEvent!!.shiftKey) {
                            currentDrawable = texts.last()
                        }
                        currentLine = currentText!!.lines.last()
                    }
                    caretPosInCurrentText = currentLine!!.posEnd
                }

                "Home" -> {
                    trace("MainCanvas::addDrawable press Home")
                    charOffset = 0
                    if (currentKeyboardEvent!!.ctrlKey) {
                        if (currentKeyboardEvent!!.shiftKey) {
                            currentDrawable = texts.first()
                        }
                        currentLine = currentText!!.lines.first()
                    }
                    caretPosInCurrentText = currentLine!!.posBegin
                }

                "Shift", "ShiftLeft", "ShiftRight", "Control", "ControlLeft", "ControlRight" -> {
                    doNotDraw = true
                }

                "ArrowLeft" -> {
                    trace("MainCanvas::addDrawable press ArrowLeft")
                    charOffset--
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
                                        currentKeyboardEvent!!.key[0],
                                        caretPosInCurrentText + charOffset++
                                    )
                                )
//                        currentText?.addChar(currentKeyboardEvent!!.key[0], caretPosInCurrentText + charOffset++)
                                recomputeCurrentLineAfterDraw = true
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
        divContainer.appendChild(canvas)
        divHolder.appendChild(canvas)

        divScroll.addEventListener(Event.SCROLL, { ev: Event ->
            trace("divScroll scroll")
            menu = null
            dy = divScroll.scrollTop
            divHolder.style.transform = "translate(0px, ${dy}px)"
            isDoubleClick = false
            draw()
            ev.preventDefault()
            ev.stopPropagation()
        })

        window.onresize = {
            trace("window resize")
            menu = null
            canvas.width = document.body!!.offsetWidth
            canvas.height = window.innerHeight - 10
            posYGlobal = -dy
            isDoubleClick = false
            draw()
        }

        canvas.oncontextmenu = EventHandler { event: MouseEvent ->
            trace("canvas contextmenu")
            if (currentDrawable != null && currentClick != null)
                currentDrawable!!.getContextualMenuEntries(currentDoubleClick!!)
            if (currentMenuEntries != null) {
                event.preventDefault()
                event.stopPropagation()

                menu = Menu(currentMenuEntries!!)
                menu!!.draw(ctx, event.offsetX, event.offsetY)
            }
        }

        canvas.onclick = EventHandler { event: MouseEvent ->
            trace("canvas click")
            if (menu != null) {
                menu!!.onClick(event.offsetX, event.offsetY)
                menu = null
            } else {
                charOffset = 0
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
                        currentClick = text.click(ctx, event.offsetX, event.offsetY)
                        console.log(currentClick)
                        if (currentDoubleClick != null)
                            currentMenuEntries = text.getContextualMenuEntries(currentDoubleClick!!)
                    }
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
            event.stopPropagation()
            isDoubleClick = true
            wordOffset = 0
            for (d in drawables) {
                if (d.isClicked(event.offsetX, event.offsetY)) {
                    currentDrawable = d
                    currentDoubleClick = d.doubleClick(ctx, event.offsetX, event.offsetY)
                    if (currentDoubleClick != null)
                        currentMenuEntries = d.getContextualMenuEntries(currentDoubleClick!!)
                    else currentDoubleClick = null
                }
            }
            draw()
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

        val image = CanvasImg("Coucou", 0)
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

        trace("Remove New Drawables")
        for (text in drawables) {
            if (text !in initialDrawables)
                drawables.remove(text)
        }

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
            if (currentLine == null) {
                trace("Draw caret1 currentLine == null")
                currentLine = currentText!!.lines.first()
                caretPosInCurrentText = currentLine!!.posEnd
                charOffset = 0
            } else if (recomputeCurrentLineAfterDraw) {
                trace("Draw caret2 currentLine == ${currentLine}, caretPosInCurrentText == $caretPosInCurrentText, charOffset == $charOffset")
                currentLine =
                    currentText!!.lines.find { it.posBegin <= charOffset + currentClick!!.second && it.posEnd >= charOffset + currentClick!!.second }
            }

            if (currentLine != null) {
                trace("Draw caret currentLine != null ")
                val caretPosInLine = caretPosInCurrentText - currentLine!!.posBegin
                CanvasCaret.draw(ctx, currentText!!, currentLine!!, caretPosInLine + charOffset)
                if (isDoubleClick && currentDoubleClick != null) {
                    trace("Draw dblClick")
                    CanvasCaret.drawDblClick(
                        ctx,
                        currentText!!,
                        currentDoubleClick!!.first,
                        caretPosInLine + charOffset,
                        currentDoubleClick!!.second,
                        currentDoubleClick!!.third
                    )
                }
            }
        }
        divHolder.style.height = "${posYGlobal + dy}px"

        traceDeIndent("MainCanvas::draw")
    }
}