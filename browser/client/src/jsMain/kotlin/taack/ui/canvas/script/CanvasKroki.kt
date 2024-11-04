package taack.ui.canvas.script

import js.buffer.ArrayBuffer
import js.typedarrays.Uint8Array
import kotlinx.browser.document
import kotlinx.browser.window
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.canvas.item.CanvasImg
import taack.ui.canvas.text.CanvasLine
import taack.ui.canvas.text.CanvasText
import web.canvas.CanvasRenderingContext2D
import kotlin.js.Promise

class CanvasKroki(txtInit: String) : CanvasText(txtInit, 0) {

    private val apps: Array<String> = arrayOf(
        "BlockDiag", "BPMN", "Bytefield", "SeqDiag", "ActDiag", "NewDiag",
        "PacketDiag", "RackDiag", "c4Plantuml", "d2", "dbml", "ditaa", "erd", "excalidraw", "GraphViz", "Mermaid",
        "Nomnoml", "Pikchr", "PlantUML", "Structurizr", "Svgbob", "Symbolator", "TikZ", "WaveDrom", "WireViz"
    )
    private val srcURI: String?
        get() {
            val ret = txt.substring(0, txt.indexOfFirst { !it.isLetter() })
            if (apps.contains(ret)) {
                return ret.lowercase()
            }
            return null
        }
    override val fontWeight: String
        get() = "400"
    override val fontSize: String
        get() = "12px"
    override val fontFace: String
        get() = "Consolas,Monaco,'Andale Mono','Ubuntu Mono',monospace"
    override val fillStyle: String
        get() = "rgb(54, 54, 54)"
    override val letterSpacing: Double
        get() = 1.0
    override val lineHeight: Double
        get() = 24.0
    override val wordSpacing: Double
        get() = 0.0
    override val marginTop: Double
        get() = 0.0
    override val marginBottom: Double
        get() = 0.0

    private var image: CanvasImg? = null

    private val txtScript: String
        get() {
            if (srcURI != null) {
                val txt = txt.substring(srcURI!!.length)
                compress(txt).then {
                    val bytes = Uint8Array(it)
                    val len = bytes.length
                    val chars = CharArray(len)
                    for (i in 0 until len) {
                        chars[i] = bytes[i].toInt().toChar()
                    }
                    val txt = chars.concatToString()
                    return@then txt
                }.then {
                    imageSrc =
                        "${document.location?.protocol}//${document.location?.hostname}:8000/" + srcURI + "/svg/" + window.btoa(
                            it
                        ).replace(Regex("\\+"), "-").replace(Regex("/+"), "_")
                    image = CanvasImg(imageSrc!!, srcURI!!, 0)
                }
            }
            return txt
        }

    private var imageSrc: String? = null

    override fun computeNum(): String {
        return ""
    }

    private fun compress(str: String): Promise<ArrayBuffer> {
        trace("CanvasKroki::compress: $str")
        return js(
            """
function compress(string, encoding) {
    var byteArray = new TextEncoder('utf-8').encode(string);
    var cs = new CompressionStream(encoding);
    var writer = cs.writable.getWriter();
    writer.write(byteArray);
    writer.close();
    return new Response(cs.readable).arrayBuffer();
}
compress(str, "deflate");
"""
        )
    }

    override fun draw(ctx: CanvasRenderingContext2D, width: Double, posY: Double, posX: Double): Double {
        traceIndent("CanvasKroki::draw: $posX, $posY, $width")
        this.posXStart = posX
        this.posXEnd = width
        ctx.save()
        initCtx(ctx)
        txtPrefix = computeNum()
        val tmpTxt = txtPrefix + txtScript
        val height = lineHeight//txtMetrics.actualBoundingBoxAscent// + txtMetrics.actualBoundingBoxDescent//lineHeight
        globalPosYStart = posY
        var pX = posX
        var pY = marginTop + height
        totalHeight = pY
        var currentLetterPos = 0
        var posLetterLineBegin = 0
        lines = emptyList()
        val listTxt = tmpTxt.split("\n")
        for (i in listTxt.indices) {
            val t = listTxt[i] + (if (i < listTxt.size - 1) "\n" else "")
            currentLetterPos += t.length
            ctx.save()
            initCtx(ctx, currentLetterPos)
            pX = posX + ctx.measureText(txtPrefix).width
            lines += CanvasLine(
                posLetterLineBegin,
                currentLetterPos,
                posY + totalHeight,
                height,
                pX
            )
            pY += height
            totalHeight = pY
            posLetterLineBegin = currentLetterPos
            ctx.restore()
        }
        ctx.save()
        ctx.fillStyle = "#fff"
        ctx.fillRect(posX, posY, pX - posX, pY - posY)
        ctx.fillRect(posX, posY + marginBottom + 5, width - 40.0, pY + marginBottom + 5)
        ctx.restore()

        lines.forEach { l ->
            l.drawLine(ctx, this, null)
        }

        totalHeight += marginBottom
        val ret = posY + totalHeight
        globalPosYEnd = ret
        ctx.restore()
        traceDeIndent("CanvasKroki::draw: $globalPosYEnd")

//        return (image?.draw(ctx, width, ret, posX + width / 2 - ( image?.image?.width ?: 0)) ?: ret)
        return (image?.draw(ctx, width, ret, posX) ?: ret)
    }

    override fun dumpAsciidoc(): String {
        return image?.dumpAsciidoc() ?: ""
    }

}

