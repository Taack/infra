package taack.ui.canvas

import kotlinx.browser.document
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement

class MainCanvas {
    fun draw() {
        console.log("HELAUO")
        val canvas = document.getElementById("canvas") as HTMLCanvasElement?
        console.log("canvas $canvas")
        val ctx = canvas?.getContext("2d") as CanvasRenderingContext2D?
        console.log("ctx $ctx")
        if (ctx == null || canvas == null) return
        ctx.fillStyle = "rgb(200 0 0)"
        ctx.fillRect(10.0, 10.0, 50.0, 50.0)

        ctx.fillStyle = "rgb(0.0 0.0 20.00.0 / 50.0%)"
        ctx.fillRect(30.0, 30.0, 50.0, 50.0)

        ctx.font = "48px sans-serif"
        ctx.fillStyle = "rgb(0 0 0)"

        val txt = "Hello world is too long"
        console.log("txt ${ctx.measureText(txt).width}")

        val txtMetrics = ctx.measureText(txt)
        val width = txtMetrics.width
        val height = txtMetrics.actualBoundingBoxAscent + txtMetrics.actualBoundingBoxDescent
        val canvasWidth = canvas.width

        if (width > canvasWidth) {
            val listTxt = txt.split(" ")
            var posX = 10.0
            var posY = 50.0
            for (i in 0 .. listTxt.size) {
                val t = listTxt[i] + (if (i < listTxt.size) " " else "")
                console.log("i $i, height $height, posX $posX, posY $posY, width $width, listTxt[i] ${listTxt[i]}")
                if (posX + ctx.measureText(t).width > canvasWidth) {
                    posX = 10.0
                    posY += height
                }
                ctx.fillText(t, posX, posY)
                posX += ctx.measureText(t).width
            }
        }
//        var txtLine = txt
//        while (ctx.measureText(txtLine).width > canvasWidth) {
//            txtLine = listTxt.dropLast(1).joinToString(" ") + "<br>" + listTxt.last()
//            listTxt = txtLine.split(" ")
//        }

        ctx.fillText(txt, 10.0, 50.0)

    }
}