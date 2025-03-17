package taack.ui.wysiwyg.canvasStyled.item

import taack.ui.base.Helper.Companion.trace
import taack.ui.wysiwyg.canvasStyled.ICanvasDrawable
import taack.ui.wysiwyg.canvasStyled.text.CanvasFigure
import taack.ui.wysiwyg.canvasStyled.text.CanvasLine
import taack.ui.wysiwyg.canvasStyled.text.CanvasText
import web.canvas.CanvasRenderingContext2D
import web.encoding.btoa
import web.html.Image

class CanvasLink(
    txt: String, fileName: String, private val initCitationNumber: Int
) : ICanvasDrawable {

    companion object {
        private val _image: Image
            get() {
                val i = Image(120, 120)

                val svg = """
                    <?xml version="1.0" encoding="utf-8"?><!-- Uploaded to: SVG Repo, www.svgrepo.com, Generator: SVG Repo Mixer Tools -->
                    <svg width="20px" height="20px" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path opacity="0.5" d="M3 15C3 17.8284 3 19.2426 3.87868 20.1213C4.75736 21 6.17157 21 9 21H15C17.8284 21 19.2426 21 20.1213 20.1213C21 19.2426 21 17.8284 21 15" stroke="#1C274C" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M12 3V16M12 16L16 11.625M12 16L8 11.625" stroke="#1C274C" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                """.trimIndent()

                i.src ="data:image/svg+xml;base64," + btoa(svg)
                return i
            }
        private val image = _image
    }

    override var globalPosYStart: Double = 0.0
    override var globalPosYEnd: Double = 0.0
    override var citationNumber: Int = initCitationNumber
    val text = CanvasFigure(txt, citationNumber)
    override fun getSelectedText(posX: Double?, posY: Double?): CanvasText {
        return text
    }

    // https://developer.mozilla.org/en-US/docs/Web/API/CanvasRenderingContext2D/drawImage
    override fun draw(ctx: CanvasRenderingContext2D, width: Double, posY: Double, posX: Double): Double {
        trace("CanvasLink::draw width: $width, posY: $posY, posX: $posX")
        globalPosYStart = posY
        globalPosYEnd = posY

        if (image.complete) {
            globalPosYEnd = (image.height) + globalPosYStart
            ctx.drawImage(image, posX, posY, image.width.toDouble(), image.height.toDouble())
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
        return "\nlink:${text.txt}[${text.txt},download]\n"
//        return "\nlink:${text.txt}[${text.dumpAsciidoc()},download]\n"
//            "\n+++<img src='$src'/>+++"
//        }
    }
}