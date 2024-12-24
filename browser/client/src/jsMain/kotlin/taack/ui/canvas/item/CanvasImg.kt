package taack.ui.canvas.item

import taack.ui.base.Helper.Companion.trace
import taack.ui.canvas.ICanvasDrawable
import taack.ui.canvas.text.CanvasFigure
import taack.ui.canvas.text.CanvasLine
import taack.ui.canvas.text.CanvasText
import web.canvas.CanvasRenderingContext2D
import web.events.EventHandler
import web.html.Image
import kotlin.math.max
import kotlin.math.min

class CanvasImg(
    private val src: String, txt: String, private val initCitationNumber: Int
) : ICanvasDrawable {

    companion object {
        val srcImage = hashMapOf<String, Image>()
        val srcRatio = hashMapOf<String, Double>()
    }

    override var globalPosYStart: Double = 0.0
    override var globalPosYEnd: Double = 0.0
    override var citationNumber: Int = initCitationNumber
    val text = CanvasFigure(txt, citationNumber)

    private var ratio: Double?
        get() = srcRatio[src]
        set(value) {
            srcRatio[src] = value!!
        }
    private val image: Image = srcImage.getOrPut(src) {
        val i = Image()
        i.src = src
        i
    }

    override fun getSelectedText(posX: Double?, posY: Double?): CanvasText {
        return text
    }

    // https://developer.mozilla.org/en-US/docs/Web/API/CanvasRenderingContext2D/drawImage
    override fun draw(ctx: CanvasRenderingContext2D, width: Double, posY: Double, posX: Double): Double {
        trace("CanvasImg::draw width: $width, posY: $posY, posX: $posX")
        globalPosYStart = posY
        globalPosYEnd = posY
        if (ratio == null) {
            image.onload = EventHandler {
                val w = image.width
                val h = image.height
                ratio = if (h < 480) 1.0 else min(480.0 / h, width / w)
                trace("CanvasImg::draw.onLoad $image ${image.width}x${image.height}, r: $ratio")
            }
            image.src = src
        } else if (image.complete) {
            globalPosYEnd = ratio!! * (image.height) + globalPosYStart
            ctx.drawImage(image, posX, posY, image.width * ratio!!, image.height * ratio!!)
            globalPosYEnd = text.draw(ctx, width, globalPosYEnd, posX)
            return globalPosYEnd
        }
        return globalPosYEnd
    }

    override fun click(ctx: CanvasRenderingContext2D, posX: Double, posY: Double): Pair<CanvasLine, Int>? {
        TODO("Not yet implemented")
    }

    override fun doubleClick(ctx: CanvasRenderingContext2D, posX: Double, posY: Double): Triple<CanvasLine, Int, Int>? {
        return null
    }

    override fun reset() {
        text.reset()
        citationNumber = initCitationNumber
    }

    override fun dumpAsciidoc(): String {
        //return //if (src.length < 250)
//        return "\n${text.dumpAsciidoc()}\nimage::${src}[]"
//        else {
           return "\n" + "image::${text.txt}[]" + "\n"
//            "\n+++<img src='$src'/>+++"
//        }
    }

    override fun toString(): String {
        return "CanvasImg(text=$text, citationNumber=$citationNumber, globalPosYEnd=$globalPosYEnd, globalPosYStart=$globalPosYStart)"
    }
}