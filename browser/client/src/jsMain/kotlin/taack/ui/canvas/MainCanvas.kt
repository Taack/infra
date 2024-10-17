package taack.ui.canvas

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.MouseEvent
import taack.ui.canvas.item.CanvasCaret
import taack.ui.canvas.text.*

class MainCanvas(private val divHolder: HTMLDivElement, private val divScroll: HTMLDivElement) {
    val canvas: HTMLCanvasElement = document.createElement("canvas") as HTMLCanvasElement
    private val ctx: CanvasRenderingContext2D = canvas.getContext("2d") as CanvasRenderingContext2D
    private val texts = mutableListOf<CanvasText>()
    private val divContainer: HTMLDivElement = document.createElement("div") as HTMLDivElement
    private var dy: Double = 0.0
    private var currentText: CanvasText? = null
    private var currentLine: CanvasLine? = null
    private var currentMouseEvent: MouseEvent? = null
    private var currentKeyboardEvent: KeyboardEvent? = null
    private var charOffset: Int = 0
    private var lineOffset: Int = 0
    private var clickYPosBefore: Double = 0.0

    private fun addText() {
        var co: Int = 0
        var lo: Int = 0
        when (currentKeyboardEvent!!.code) {
            "Backspace" -> {
                co = charOffset--
            }
            "Enter" -> {
            }
            "ArrowUp" -> {
                lo = lineOffset--
            }
            "ArrowDown" -> {
                lo = lineOffset++
            }
            "ArrowRight" -> {
                co = charOffset++
            }
            "ArrowLeft" -> {
                co = charOffset--
            }
            else -> {
                co = charOffset++
            }
        }
        currentText?.drawLine(
            currentLine!!,
            ctx,
            currentKeyboardEvent!!,
            co,
            lo,
            currentMouseEvent!!.offsetX,
            currentMouseEvent!!.offsetY - clickYPosBefore
        )
        draw()
    }

    private fun addText(text: CanvasText) {
        texts.add(text)
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

        divScroll.onscroll = {
            dy = divScroll.scrollTop
            divHolder.style.transform = "translate(0px, ${dy}px)"
            console.log("Scroll $dy", it)
            draw()
            dy
        }

        window.onresize = {
            console.log("onresize ${document.body!!.offsetWidth}")
            canvas.width = document.body!!.offsetWidth
            canvas.height = window.innerHeight - 100
            CanvasText.globalPosY = -dy
            draw()
        }

        canvas.onclick = { event: MouseEvent ->
            logMouseEvent(event)
            charOffset = 0

            currentText = null
            currentMouseEvent = event
            event.preventDefault()
            event.stopPropagation()
            for (text in texts) {
                for (line in text.lines) {
                    if (event.offsetY in line.textY - line.height..line.textY) {
                        clickYPosBefore = line.textY
                        currentLine = line
                        currentText = text
                        val xCaret = line.caretXCoords(ctx, text, event.offsetX)
                        CanvasCaret.draw(ctx, xCaret, line.textY)
                        console.log(
                            "find text line ... at (${event.offsetX}, ${event.offsetY})($xCaret, ${line.textY + line.height}) = ${
                                text.txt.substring(
                                    line.posBegin,
                                    line.caretNCoords(ctx, text, event.offsetX)
                                )
                            }"
                        )
                        break
                    }
                }
            }
        }

        canvas.onmousemove = { event: MouseEvent ->
            if (event.buttons.toInt() != 0)
                console.log("onmousemove ${event.button} ${event.buttons} ${event.clientX} ${event.clientY} ${event.offsetX} ${event.offsetY} ${event.pageX} ${event.pageY} ${event.ctrlKey} ${event.altKey} ${event.shiftKey} ${event.metaKey}")
        }

        canvas.onkeydown = { event: KeyboardEvent ->
            logKeyEvent(event)
            currentKeyboardEvent = event
            addText()
            event.preventDefault()
            event.stopPropagation()
        }

        addInitialTexts()
        draw()
    }

    private fun addInitialTexts() {
        console.log("canvas $canvas")
        console.log("ctx $ctx")

        val h2 = H2Canvas()

        h2.txt = "Topology Filters and Selectors Example for various data layout"
        addText(h2)

        val h3 = H3Canvas()
        h3.txt = "Directed Acyclic Graphs (the most common in computer sciences)"
        addText(h3)

        val p1 = PCanvas()
        p1.txt =
            "DSL are AI friendly, so we want to be able to use more natural language in the future to generate our assets, but generation will be translated into those DSLs, in order to be human editable, efficiently."
        addText(p1)

        val h31 = H3Canvas()
        h31.txt = "For Assemblies and bodies"
        addText(h31)

        val h4 = H4Canvas()
        h4.txt = "Category"
        addText(h4)

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
        addText(p2)
        addText(p2)
        addText(p2)

        addText(h4)
    }

    private fun draw() {
        CanvasText.num1 = 0
        CanvasText.num2 = 0
        CanvasText.globalPosY = -dy

        console.log("draw +++ CanvasText.globalPosY = ${CanvasText.globalPosY}")

        ctx.clearRect(0.0, 0.0, canvas.width.toDouble(), canvas.height.toDouble());

        for (text in texts) {
            text.draw(ctx, canvas.width)
        }

        console.log("draw --- CanvasText.globalPosY = ${CanvasText.globalPosY}")
        divHolder.style.height = "${CanvasText.globalPosY + dy}px"
    }
}