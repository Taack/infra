package taack.ui.canvas

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.MouseEvent
import taack.ui.canvas.text.*

class MainCanvas(val canvas: HTMLCanvasElement, val ctx: CanvasRenderingContext2D) {

    val texts = mutableListOf<ICanvasText>()

    var currentText: ICanvasText? = null
    var currentMouseEvent: MouseEvent? = null
    var currentKeyboardEvent: KeyboardEvent? = null
    var consecutiveCharSequence: Int = 0
    var clickYPosBefore: Double = 0.0

    fun addText() {
        currentText?.drawText(currentKeyboardEvent!!.key, consecutiveCharSequence++, currentMouseEvent!!.x, currentMouseEvent!!.y - clickYPosBefore)
        draw()
    }

    private fun addText(text: ICanvasText) {
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

    init {
        addInitialTexts()
        window.onresize = {
            console.log("onresize ${document.body!!.offsetWidth}")
            canvas.width = document.body!!.offsetWidth
            canvas.height = window.innerHeight - 100
            ICanvasText.lastHeight = 0.0
            draw()
        }

//        canvas.onclick = { event: MouseEvent ->
//            console.log("onclick ${event.button} ${event.buttons} ${event.clientX} ${event.clientY} ${event.offsetX} ${event.offsetY} ${event.pageX} ${event.pageY} ${event.ctrlKey} ${event.altKey} ${event.shiftKey} ${event.metaKey}")
//        }

        canvas.onmouseup = { event: MouseEvent ->
            console.log("onmouseup ${event.button} ${event.buttons} ${event.clientX} ${event.clientY} ${event.offsetX} ${event.offsetY} ${event.pageX} ${event.pageY} ${event.ctrlKey} ${event.altKey} ${event.shiftKey} ${event.metaKey}")
        }

        canvas.onmousedown = { event: MouseEvent ->
            console.log("onmousedown ${event.button} ${event.buttons} ${event.clientX} ${event.clientY} ${event.offsetX} ${event.offsetY} ${event.pageX} ${event.pageY} ${event.ctrlKey} ${event.altKey} ${event.shiftKey} ${event.metaKey}")
        }

        canvas.ondblclick = { event: MouseEvent ->
            console.log("ondblclick ${event.button} ${event.buttons} ${event.clientX} ${event.clientY} ${event.offsetX} ${event.offsetY} ${event.pageX} ${event.pageY} ${event.ctrlKey} ${event.altKey} ${event.shiftKey} ${event.metaKey}")

        }

        canvas.onclick = { event: MouseEvent ->
            console.log("onclick ${event.button} ${event.buttons} ${event.clientX} ${event.clientY} ${event.offsetX} ${event.offsetY} ${event.pageX} ${event.pageY} ${event.ctrlKey} ${event.altKey} ${event.shiftKey} ${event.metaKey}")

            var h = 0.0
            currentText = null
            currentMouseEvent = event
            event.preventDefault()
            event.stopPropagation()
            for (text in texts) {
                var hOld = h
                h += text.totalHeight
                if (event.offsetY > hOld) {
                    if (event.offsetY < h) {
                        clickYPosBefore = hOld
                        currentText = text
                        break
                    }
                }
            }
        }

        canvas.onmousemove = { event: MouseEvent ->
            if (event.buttons.toInt() != 0)
                console.log("onmousemove ${event.button} ${event.buttons} ${event.clientX} ${event.clientY} ${event.offsetX} ${event.offsetY} ${event.pageX} ${event.pageY} ${event.ctrlKey} ${event.altKey} ${event.shiftKey} ${event.metaKey}")
        }

        canvas.onmouseout = { event: MouseEvent ->
            console.log("onmouseout ${event.button} ${event.buttons} ${event.clientX} ${event.clientY} ${event.offsetX} ${event.offsetY} ${event.pageX} ${event.pageY} ${event.ctrlKey} ${event.altKey} ${event.shiftKey} ${event.metaKey}")

        }

        canvas.onmouseover = { event: MouseEvent ->
            console.log("onmouseover ${event.button} ${event.buttons} ${event.clientX} ${event.clientY} ${event.offsetX} ${event.offsetY} ${event.pageX} ${event.pageY} ${event.ctrlKey} ${event.altKey} ${event.shiftKey} ${event.metaKey}")

        }

        canvas.onkeydown = { event: KeyboardEvent ->
            console.log("onkeydown ${event.code} ${event.ctrlKey} ${event.altKey} ${event.shiftKey} ${event.metaKey} ${event.key} ${event.repeat}")
            currentKeyboardEvent = event
            addText()
            event.preventDefault()
            event.stopPropagation()
        }

        canvas.onkeyup = { event: KeyboardEvent ->
            console.log("onkeyup ${event.code} ${event.ctrlKey} ${event.altKey} ${event.shiftKey} ${event.metaKey} ${event.key} ${event.repeat}")
            event.preventDefault()
            event.stopPropagation()
        }
    }

    fun addInitialTexts() {
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
        addText(h4)
        draw()
    }

    fun draw() {
        ICanvasText.num1 = 0
        ICanvasText.num2 = 0

        println(texts)
        for (text in texts) {
            println("text: $text")

            text.draw(ctx, canvas.width)
        }
        println(texts)
    }
}