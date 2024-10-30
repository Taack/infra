package taack.ui.canvas.item

import taack.ui.base.Helper.Companion.trace
import taack.ui.canvas.ICanvasDrawable
import taack.ui.canvas.text.CanvasFigure
import taack.ui.canvas.text.CanvasLine
import taack.ui.canvas.text.CanvasText
import web.canvas.CanvasRenderingContext2D
import web.events.EventHandler
import web.html.Image
import kotlin.math.min

class CanvasImg(
    txt: String, private val initCitationNumber: Int
) : ICanvasDrawable {

    override var globalPosYStart: Double = 0.0
    override var globalPosYEnd: Double = 0.0
    override var citationNumber: Int = initCitationNumber
    val text = CanvasFigure(txt, citationNumber)

    override fun getSelectedText(posX: Double?, posY: Double?): CanvasText? {
        return text
    }
  // https://developer.mozilla.org/en-US/docs/Web/API/CanvasRenderingContext2D/drawImage
    override fun draw(ctx: CanvasRenderingContext2D, width: Double, posY: Double, posX: Double): Double {
        trace("CanvasImg::draw width: $width, posY: $posY, posX: $posX")
      globalPosYStart = posY
        val image = Image()
//        image.style.maxHeight = "320px"
//        image.style.maxWidth = "512px"
        image.onload = EventHandler {
            val w = image.width
            val h = image.height
            val r = min(320.0 / h, width / w)
            trace("CanvasImg::draw.onLoad $image ${image.width}x${image.height}, r: $r")
            ctx.drawImage(image, posX, posY, w * r, h * r)
        }
//        image.src = "https://mdn.github.io/shared-assets/images/examples/rhino.jpg";
        image.src =
            "https://upload.wikimedia.org/wikipedia/commons/thumb/3/37/Oryctolagus_cuniculus_Tasmania_2.jpg/479px-Oryctolagus_cuniculus_Tasmania_2.jpg";

      globalPosYEnd = 320.0 + globalPosYStart
      globalPosYEnd = text.draw(ctx, width, globalPosYEnd, posX)
        return globalPosYEnd
    }

    override fun click(ctx: CanvasRenderingContext2D, posX: Double, posY: Double): Pair<CanvasLine, Int>? {
        TODO("Not yet implemented")
    }

    override fun doubleClick(ctx: CanvasRenderingContext2D, posX: Double, posY: Double): Triple<CanvasLine, Int, Int>? {
        TODO("Not yet implemented")
    }

    override fun getContextualMenuEntries(dblClick: Triple<CanvasLine, Int, Int>): List<MenuEntry> {
        return emptyList()
    }

    override fun reset() {
        text.reset()
        citationNumber = initCitationNumber
    }
}