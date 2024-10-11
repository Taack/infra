package taack.ui.canvas

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLDivElement

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
            d.appendChild(canvas)
            return MainCanvas(canvas, canvas.getContext("2d") as CanvasRenderingContext2D)
        }
    }

    init {
        window.onresize = {
            console.log("onresize ${document.body!!.offsetWidth}")
            canvas.width = document.body!!.offsetWidth
            canvas.height = window.innerHeight - 100
            draw()
        }
    }

    fun h2(ctx: CanvasRenderingContext2D) {
    }

    fun draw() {
        console.log("canvas $canvas")
        console.log("ctx $ctx")

        ctx.clearRect(0.0, 0.0, canvas.width.toDouble(), canvas.height.toDouble())

        ctx.fillStyle = "rgb(200 0 0)"
        ctx.fillRect(10.0, 10.0, 50.0, 50.0)

        ctx.fillStyle = "rgb(0.0 0.0 20.00.0 / 50.0%)"
        ctx.fillRect(30.0, 30.0, 50.0, 50.0)

        ctx.font = "48px sans-serif"
        ctx.fillStyle = "rgb(0 0 0)"

        val txt = "Hello world is too long, yes but Hello world is really too long"
        console.log("txt ${ctx.measureText(txt).width}")

        val txtMetrics = ctx.measureText(txt)
        val width = txtMetrics.width
        val height = txtMetrics.actualBoundingBoxAscent + txtMetrics.actualBoundingBoxDescent
        val canvasWidth = canvas.width

        if (width > canvasWidth) {
            val listTxt = txt.split(" ")
            var posX = 10.0
            var posY = 50.0
            for (i in 0 .. (listTxt.size - 1)) {
                val t = listTxt[i] + (if (i < listTxt.size - 1) " " else "")
                console.log("i $i, height $height, posX $posX, posY $posY, width $width, listTxt[i] ${listTxt[i]}")
                if (posX + ctx.measureText(t).width > canvasWidth) {
                    posX = 10.0
                    posY += height
                }
                ctx.fillText(t, posX, posY)
                posX += ctx.measureText(t).width
            }
        } else {
            ctx.fillText(txt, 10.0, 50.0)
        }
//        var txtLine = txt
//        while (ctx.measureText(txtLine).width > canvasWidth) {
//            txtLine = listTxt.dropLast(1).joinToString(" ") + "<br>" + listTxt.last()
//            listTxt = txtLine.split(" ")
//        }


    }
}