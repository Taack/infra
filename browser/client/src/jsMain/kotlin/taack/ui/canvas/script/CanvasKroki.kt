package taack.ui.canvas.script

import js.buffer.ArrayBuffer
import js.typedarrays.Uint8Array
import kotlinx.browser.window
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.canvas.item.CanvasImg
import taack.ui.canvas.text.CanvasLine
import taack.ui.canvas.text.CanvasText
import web.canvas.CanvasRenderingContext2D
import web.compression.CompressionFormat
import web.compression.CompressionStream
import web.encoding.TextEncoder
import kotlin.js.Promise

class CanvasKroki(txtInit: String) : CanvasText(txtInit, 0) {

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

    val txtScript: String
        get() {
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
                imageSrc = "https://kroki.io/plantuml/svg/" + window.btoa(it)
                image = CanvasImg(imageSrc!!, "coucou", 0)
            }
            return txt
        }

    private var imageSrc: String? = null

    override fun computeNum(): String {
        return ""
    }

    suspend fun utf8ToB64(): String {
        val ba = TextEncoder().encode(txt)
        val cs = CompressionStream(CompressionFormat.deflate)
        val w = cs.writable.getWriter()
        var res = ""
        console.log("AUOAUOAUO0")
//            GlobalScope.launch {
        console.log("AUOAUOAUO1")
        w.write(ba)
        w.close()
        res = cs.readable.getReader().read().toString()
        console.log("AUOAUOAUO2")
//            }
        console.log("AUOAUOAUO3")
        return res
    }


    fun compress(str: String): Promise<ArrayBuffer> {
        trace("CanvasKroki::compress: $str")
        return js("""
function compress(string, encoding) {
    var byteArray = new TextEncoder().encode(string);
    var cs = new CompressionStream(encoding);
    var writer = cs.writable.getWriter();
    writer.write(byteArray);
    writer.close();
    return new Response(cs.readable).arrayBuffer();
}
compress(str, "deflate");
""")
    }

    override fun draw(ctx: CanvasRenderingContext2D, width: Double, posY: Double, posX: Double): Double {
        traceIndent("CanvasKroki::draw: $posX, $posY, $width")
        this.posXStart = posX
        this.posXEnd = width
        ctx.save()
        initCtx(ctx)
        txtPrefix = computeNum()
        val tmpTxt = txtPrefix + txtScript
        //val txtMetrics = ctx.measureText(tmpTxt.ifEmpty { "|" })
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
        image?.draw(ctx, posX + 100.0, posY, width)
        return ret
    }

}

