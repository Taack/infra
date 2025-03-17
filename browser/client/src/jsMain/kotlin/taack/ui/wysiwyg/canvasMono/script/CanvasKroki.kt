package taack.ui.wysiwyg.canvasMono.script

import js.typedarrays.Uint8Array
import taack.ui.wysiwyg.canvasMono.ICanvasDrawable
import taack.ui.wysiwyg.canvasMono.item.CanvasImg
import web.encoding.btoa
import web.location.location

class CanvasKroki(txtInit: String) : CanvasScriptCommon(txtInit) {

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


    private var image: CanvasImg? = null

    override val txtScript: String
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
                    return@then chars.concatToString()
                }.then {
                    imageSrc =
                        "${location.protocol}//${location.hostname}:8000/" + srcURI + "/svg/" + btoa(
                            it
                        ).replace(Regex("\\+"), "-").replace(Regex("/+"), "_")
                    image = CanvasImg(imageSrc!!, srcURI!!, 0)
                }
            }
            return txt
        }

    private var imageSrc: String? = null

    override val result: ICanvasDrawable?
        get() = image

}

