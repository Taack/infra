package taack.ui.wysiwyg.script

import js.typedarrays.Uint8Array
import taack.ui.wysiwyg.canvasStyled.ICanvasDrawable
import taack.ui.wysiwyg.canvasStyled.item.CanvasImg
import taack.ui.wysiwyg.canvasStyled.table.CanvasTable
import web.encoding.btoa
import web.events.EventHandler
import web.http.POST
import web.http.RequestMethod
import web.xhr.XMLHttpRequest

class CanvasTql(txtInit: String) : CanvasScriptCommon(txtInit) {

    private val apps: Array<String> = arrayOf(
        "tqlTable", "tqlDiagram")

    private val srcURI: String?
        get() {
            val ret = txt.substring(0, txt.indexOfFirst { !it.isLetter() })
            if (apps.contains(ret)) {
                return ret.lowercase()
            }
            return null
        }



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
                    val script = btoa(it).replace(Regex("\\+"), "-").replace(Regex("/+"), "_")
                    val imageSrc = "/taackEditor/$srcURI?script=$script"
                    if (srcURI!!.contains("Table")) {
                        val xhr = XMLHttpRequest()
                        xhr.onloadend = EventHandler { e ->
                            result = CanvasTable.createTableFromAsciidoc(e.target.responseText)
                        }
                        xhr.open(RequestMethod.POST, imageSrc, false)
                        xhr.send()
                    }else {
                        result = CanvasImg(imageSrc, srcURI!!, 0)
                    }
                }
            }
            return txt
        }

    override var result: ICanvasDrawable? = null

}

