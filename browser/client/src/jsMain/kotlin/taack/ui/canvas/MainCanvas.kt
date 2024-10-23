package taack.ui.canvas

import kotlinx.browser.document
import kotlinx.browser.window
import taack.ui.canvas.item.CanvasCaret
import taack.ui.canvas.item.Menu
import taack.ui.canvas.item.MenuEntry
import taack.ui.canvas.table.*
import taack.ui.canvas.text.*
import web.canvas.CanvasRenderingContext2D
import web.events.Event
import web.events.EventHandler
import web.events.addEventListener
import web.html.HTMLCanvasElement
import web.html.HTMLDivElement
import web.uievents.KeyboardEvent
import web.uievents.MouseButton
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
    private var lineOffset: Int = 0
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


    private fun changeTextCanvasStyle(text: CanvasText) {
        if (currentDrawable != null && !(currentDrawable is CanvasTable)) {
            val i = drawables.indexOf(currentDrawable!!)
            if (i != -1) {
                drawables.remove(currentDrawable!!)
                text.txt = currentText!!.txt
                drawables.add(i, text)
                currentDrawable = text
                currentClick = null
            }
        }

    }

    private fun addDrawable() {
        if (menu == null)
            when (currentKeyboardEvent!!.key) {
                "Backspace" -> {
                    if (currentText?.rmChar(caretPosInCurrentText + charOffset) == 0) {
                        drawables.remove(currentText!!)
                    } else
                        charOffset--
                }

                "Delete" -> {
                    var pos1 = caretPosInCurrentText + charOffset
                    var pos2: Int? = null
                    if (isDoubleClick && currentKeyboardEvent!!.ctrlKey) {
                        pos1 = charSelectStartNInText!!
                        pos2 = charSelectEndNInText!! - charSelectStartNInText!!
                        isDoubleClick = false
                        println("pg: ${caretPosInCurrentText + charOffset} pb1: $pos1 pb2: $pos2")
                    }
                    if (currentText?.delChar(pos1, pos2) == 0) {
                        drawables.remove(currentText!!)
                    }
                }

                "Enter" -> {
                    val i = drawables.indexOf(currentText!!) + 1
                    when (currentText) {
                        is H2Canvas -> {
                            currentDrawable = H3Canvas()
                            drawables.add(i, currentText as H3Canvas)
                        }

                        is H3Canvas -> {
                            currentDrawable = H4Canvas()
                            drawables.add(i, currentText as H4Canvas)
                        }

                        else -> {
                            currentDrawable = PCanvas()
                            drawables.add(i, currentText as PCanvas)
                        }
                    }
                    currentLine = null
                    caretPosInCurrentText = 0
                    charOffset = 0
                }

                "ArrowUp" -> {
                    if (currentLine == null) {
                        val j = texts.indexOf(currentText) - 1
                        console.log("ArrowUp => Previous Text")
                        if (j >= 0) {
                            currentDrawable = texts[j]
                            currentLine = currentText!!.lines.last()
                        }
                    } else {
                        val i = currentText!!.findLine(currentLine!!)
                        console.log("ArrowUp +++ $i $lineOffset ${currentText!!.lines}")
                        if (i > 0 && i < currentText!!.lines.size) {
                            caretPosInCurrentText -= currentLine!!.posBegin
                            currentLine = currentText!!.lines[i - 1]
                            caretPosInCurrentText += currentLine!!.posBegin
                            console.log("ArrowUp => Previous Line $currentLine")
                        } else {
                            val j = texts.indexOf(currentText) - 1
                            console.log("ArrowUp => Previous Text")
                            if (j >= 0) {
                                currentDrawable = texts[j]
                                currentLine = currentText!!.lines.last()
                            }
                        }
                    }
                }

                "ArrowDown" -> {
                    if (currentLine == null) {
                        val j = texts.indexOf(currentText) + 1
                        console.log("ArrowDown => Next Text")
                        if (j > 0 && j < texts.size) {
                            currentDrawable = texts[j]
                            currentLine = currentText!!.lines.first()
                        }
                    } else {
                        val i = currentText!!.findLine(currentLine!!)
                        console.log("ArrowDown $i $lineOffset $currentLine ${currentText!!.lines}")

                        if (i >= 0 && i < currentText!!.lines.size - 1) {
                            caretPosInCurrentText -= currentLine!!.posBegin
                            currentLine = currentText!!.lines[i + 1]
                            caretPosInCurrentText += currentLine!!.posBegin
                            console.log("ArrowDown => Next Line $currentLine")
                        } else {
                            val j = texts.indexOf(currentText) + 1
                            console.log("ArrowDown => Next Text")
                            if (j > 0 && j < texts.size) {
                                currentDrawable = texts[j]
                                currentLine = currentText!!.lines.first()
                            }
                        }
                    }
                }

                "ArrowRight" -> {
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
                    changeTextCanvasStyle(H2Canvas())
                }

                "F3" -> {
                    changeTextCanvasStyle(H3Canvas())
                }

                "F4" -> {
                    changeTextCanvasStyle(H4Canvas())
                }

                "F1" -> {
                    changeTextCanvasStyle(PCanvas())
                }

                "F5" -> {
                    changeTextCanvasStyle(LiCanvas())
                }

                "F6" -> {
                    changeTextCanvasStyle(Li2Canvas())
                }

                "End" -> {
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

                }

                "ArrowLeft" -> {
                    charOffset--
                }

                else -> {
                    if (currentKeyboardEvent != null)
                        currentText?.addChar(currentKeyboardEvent!!.key[0], caretPosInCurrentText + charOffset++)
                }
            }

        draw()
    }

    private fun addDrawable(drawable: ICanvasDrawable) {
        drawables.add(drawable)
    }

    private fun logMouseEvent(ev: MouseEvent) {
        console.log("logMouseEvent", ev)
    }

    private fun logKeyEvent(ev: KeyboardEvent) {
        console.log("logKeyEvent", ev)
    }

    init {
        canvas.id = "canvas"
        canvas.width = window.innerWidth
        canvas.height = window.innerHeight
        canvas.tabIndex = 1
        divContainer.appendChild(canvas)
        divHolder.appendChild(canvas)

        divScroll.addEventListener(Event.SCROLL, { ev: Event ->
            console.log("onscroll", ev)
            menu = null
            dy = divScroll.scrollTop
            divHolder.style.transform = "translate(0px, ${dy}px)"
            isDoubleClick = false
            draw()
            ev.preventDefault()
            ev.stopPropagation()
        })

        window.onresize = {
            menu = null
            console.log("onresize ${document.body!!.offsetWidth}")
            canvas.width = document.body!!.offsetWidth
            canvas.height = window.innerHeight - 10
            posYGlobal = -dy
            isDoubleClick = false
            draw()
        }

        canvas.oncontextmenu = EventHandler { event: MouseEvent ->
            console.log(event)
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
            logMouseEvent(event)
            if (menu != null) {
                menu!!.onClick(event.offsetX, event.offsetY)
                menu = null
            } else {
                charOffset = 0
                isDoubleClick = false
                if (event.detail == 3) {
                    isDoubleClick = true
                    charSelectStartNInText = 0
                    charSelectEndNInText = currentDrawable?.getSelectedText(event.offsetX, event.offsetY)!!.txt.length
                    console.log("click double click == triple click")
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

        canvas.onmousemove = EventHandler { event: MouseEvent ->
            if (event.buttons != MouseButton.MAIN)
                console.log("onmousemove ${event.button} ${event.buttons} ${event.clientX} ${event.clientY} ${event.offsetX} ${event.offsetY} ${event.pageX} ${event.pageY} ${event.ctrlKey} ${event.altKey} ${event.shiftKey} ${event.metaKey}")
        }

        canvas.onkeydown = EventHandler { event: KeyboardEvent ->
            logKeyEvent(event)
            currentKeyboardEvent = event
            if (!event.ctrlKey) isDoubleClick = false

            addDrawable()
            event.preventDefault()
            event.stopPropagation()

        }

        canvas.ondblclick = EventHandler { event: MouseEvent ->
            logMouseEvent(event)
            event.preventDefault()
            event.stopPropagation()
            isDoubleClick = true
            wordOffset = 0
            for (d in drawables) {
                if (d.isClicked(event.offsetX, event.offsetY)) {
                    currentDrawable = d
                    currentDoubleClick = d.doubleClick(ctx, event.offsetX, event.offsetY)
                    console.log(currentDoubleClick)
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
        console.log("canvas $canvas")
        console.log("ctx $ctx")

        val h2 = H2Canvas()

        h2.txt = "Topology Filters and Selectors Example for various data layout"
        addDrawable(h2)

        val h3 = H3Canvas()
        h3.txt = "Directed Acyclic Graphs (the most common in computer sciences)"
        addDrawable(h3)

        val p1 = PCanvas()
        p1.txt =
            "DSL are AI friendly, so we want to be able to use more natural language in the future to generate our assets, but generation will be translated into those DSLs, in order to be human editable, efficiently."
        addDrawable(p1)

        val h31 = H3Canvas()
        h31.txt = "For Assemblies and bodies"
        addDrawable(h31)

        val h4 = H4Canvas()
        h4.txt = "Category"
        addDrawable(h4)

        val t = CanvasTable(3)

        t.addCell("Hello World1!").addCell("Hello World2!").addCell("Hello World3!")
        t.addCell("Hello World4!").addCell("Hello World5!").addCell("Hello World6!")

        addDrawable(t)

        val p2 = PCanvas()
        p2.txt = "Matched by feature, body name, but also by position DSL."
        p2.txt += " Matched by feature, body name, but also by position DSL."
        p2.txt += " Matched by feature, body name, but also by position DSL."
        p2.txt += " Matched by feature, body name, but also by position DSL."
        p2.txt += " Matched by feature, body name, but also by position DSL."
        p2.txt += " Matched by feature, body name, but also by position DSL."
        p2.txt += " Matched by feature, body name, but also by position DSL."
        p2.txt += " Matched by feature, body name, but also by position DSL."
        p2.txt += " Matched by feature, body name, but also by position DSL."
        p2.txt += " Matched by feature, body name, but also by position DSL."
        p2.txt += " Matched by feature, body name, but also by position DSL."
        p2.txt += " Matched by feature, body name, but also by position DSL."
        p2.txt += " Matched by feature, body name, but also by position DSL."
        p2.txt += " Matched by feature, body name, but also by position DSL."
        addDrawable(p2)
        val p3 = PCanvas()
        p3.txt = p2.txt
        addDrawable(p3)
        val p4 = PCanvas()
        p4.txt = p2.txt

        addDrawable(p4)
    }

    private fun draw() {
        CanvasText.num1 = 0
        CanvasText.num2 = 0
        posYGlobal = -dy

        console.log("draw +++ CanvasText.globalPosY = $posYGlobal")

        ctx.clearRect(0.0, 0.0, canvas.width.toDouble(), canvas.height.toDouble())

        for (text in drawables) {
            posYGlobal = text.draw(ctx, canvas.width.toDouble() - canvasInnerBorder, posYGlobal, canvasInnerBorder)
        }
        console.log("draw text done $currentText")

        if (currentText != null) {
            if (currentLine == null) currentLine = currentText!!.lines.first()

            if (currentLine != null) {
                val caretPosInLine = caretPosInCurrentText - currentLine!!.posBegin
                console.log("draw caret ${caretPosInLine + charOffset}")
                console.log(currentText)
                console.log(currentLine)
                CanvasCaret.draw(ctx, currentText!!, currentLine!!, caretPosInLine + charOffset)
                if (isDoubleClick && currentDoubleClick != null)
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

        console.log("draw --- CanvasText.globalPosY = $posYGlobal $caretPosInCurrentText $charOffset")
        divHolder.style.height = "${posYGlobal + dy}px"
    }
}