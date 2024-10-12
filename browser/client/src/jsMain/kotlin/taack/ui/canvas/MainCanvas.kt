package taack.ui.canvas

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.CaretPosition
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.MouseEvent
import taack.ui.canvas.text.*

class MainCanvas(val canvas: HTMLCanvasElement, val ctx: CanvasRenderingContext2D) {

    companion object {
        fun addCanvas(): MainCanvas? {
            console.log("addCanvas")
            val d = document.getElementById("canvas-holder") as HTMLDivElement?
            if (d == null) return null
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
        window.onresize = {
            console.log("onresize ${document.body!!.offsetWidth}")
            canvas.width = document.body!!.offsetWidth
            canvas.height = window.innerHeight - 100
            ICanvasCharSequence.lastHeight = 0.0
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
        }

        canvas.onkeyup = { event: KeyboardEvent ->
            console.log("onkeyup ${event.code} ${event.ctrlKey} ${event.altKey} ${event.shiftKey} ${event.metaKey} ${event.key} ${event.repeat}")
        }
    }

    fun draw() {
        console.log("canvas $canvas")
        console.log("ctx $ctx")

        ctx.clearRect(0.0, 0.0, canvas.width.toDouble(), canvas.height.toDouble())

        ctx.fillStyle = "rgb(200 0 0)"
        ctx.fillRect(10.0, 10.0, 50.0, 50.0)

        ctx.fillStyle = "rgb(0.0 0.0 200 / 50.0%)"
        ctx.fillRect(30.0, 30.0, 50.0, 50.0)

        val h2 = H2Canvas()
        val h3 = H3Canvas()
        val h4 = H4Canvas()
        val p = PCanvas()

        h2.txt = "Topology Filters and Selectors Example for various data layout"
        h2.draw(ctx, canvas.width)

        h3.txt = "Directed Acyclic Graphs (the most common in computer sciences)"
        h3.draw(ctx, canvas.width)

        p.txt = "DSL are AI friendly, so we want to be able to use more natural language in the future to generate our assets, but generation will be translated into those DSLs, in order to be human editable, efficiently."
        p.draw(ctx, canvas.width)

        h3.txt = "For Assemblies and bodies"
        h3.draw(ctx, canvas.width)

        h4.txt = "Category"
        h4.draw(ctx, canvas.width)

        p.txt = "Matched by feature, body name, but also by position DSL."
        p.draw(ctx, canvas.width)



//        ctx.font = "48px sans-serif"
//        ctx.fillStyle = "rgb(0 0 0)"
//
//        console.log("txt ${ctx.measureText(txt).width}")
//
//        val txtMetrics = ctx.measureText(txt)
//        val width = txtMetrics.width
//        val height = txtMetrics.actualBoundingBoxAscent + txtMetrics.actualBoundingBoxDescent
//        val canvasWidth = canvas.width
//
//        if (width > canvasWidth) {
//            val listTxt = txt.split(" ")
//            var posX = 10.0
//            var posY = 50.0
//            for (i in 0 .. (listTxt.size - 1)) {
//                val t = listTxt[i] + (if (i < listTxt.size - 1) " " else "")
//                console.log("i $i, height $height, posX $posX, posY $posY, width $width, listTxt[i] ${listTxt[i]}")
//                if (posX + ctx.measureText(t).width > canvasWidth) {
//                    posX = 10.0
//                    posY += height
//                }
//                ctx.fillText(t, posX, posY)
//                posX += ctx.measureText(t).width
//            }
//        } else {
//            ctx.fillText(txt, 10.0, 50.0)
//        }


    }
}