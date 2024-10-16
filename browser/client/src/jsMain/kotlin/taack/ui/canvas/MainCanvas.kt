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

class MainCanvas(val canvas: HTMLCanvasElement, val ctx: CanvasRenderingContext2D) {

    val texts = mutableListOf<CanvasText>()

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
        if (currentKeyboardEvent!!.code == "Backspace") {
            co = charOffset--
        } else if (currentKeyboardEvent!!.code == "Enter") {
        } else if (currentKeyboardEvent!!.code == "ArrowUp") {
            lo = lineOffset--
        } else if (currentKeyboardEvent!!.code == "ArrowDown") {
            lo = lineOffset++
        } else if (currentKeyboardEvent!!.code == "ArrowRight") {
            co = charOffset++
        } else if (currentKeyboardEvent!!.code == "ArrowLeft") {
            co = charOffset--
        } else {
            co = charOffset++
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

    companion object {
        fun addCanvas(): MainCanvas? {
            console.log("addCanvas")
            val d = document.getElementById("canvas-holder") as HTMLDivElement? ?: return null
            val canvas = document.createElement("canvas") as HTMLCanvasElement
            canvas.id = "canvas"
            canvas.width = document.body!!.offsetWidth
            canvas.height = window.innerHeight - 100
            canvas.tabIndex = 1
            d.appendChild(canvas)
            return MainCanvas(canvas, canvas.getContext("2d") as CanvasRenderingContext2D)
        }
    }

    private fun logMouseEvent(ev: MouseEvent) {
        console.log("logMouseEvent", ev)
    }

    private fun logKeyEvent(ev: KeyboardEvent) {
        console.log("logKeyEvent", ev)
    }

    init {
        window.onresize = {
            console.log("onresize ${document.body!!.offsetWidth}")
            canvas.width = document.body!!.offsetWidth
            canvas.height = window.innerHeight - 100
            CanvasText.globalPosY = 0.0
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
        CanvasText.globalPosY = 0.0
        ctx.clearRect(0.0, 0.0, canvas.width.toDouble(), canvas.height.toDouble());

        for (text in texts) {
            text.draw(ctx, canvas.width)
        }
    }
}