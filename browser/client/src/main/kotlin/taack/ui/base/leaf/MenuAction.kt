package taack.ui.base.leaf

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.Node
import org.w3c.dom.asList
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import org.w3c.files.Blob
import org.w3c.xhr.XMLHttpRequest
import taack.ui.base.Helper
import taack.ui.base.Helper.Companion.processAjaxLink
import taack.ui.base.element.Block
import kotlin.js.Promise

class MenuAction(private val parent: Block, private val a: HTMLAnchorElement) {
    companion object {
        fun getAjaxMenu(p: Block): List<MenuAction> {
            val elements: List<Node>?
            elements = document.querySelectorAll("a.taackAjaxMenuLink").asList()
            return elements.map {
                println("AUO333 getAjaxMenu $p $it")
                MenuAction(p, it as HTMLAnchorElement)
            }
        }
    }

    private val action: String = a.attributes.getNamedItem("ajaxAction")!!.value

    init {
        Helper.trace("MenuAction::init +++")
        a.onclick = { me ->
            onclickMenu(me)
        }
        Helper.trace("MenuAction::init ---")
    }

    private fun onclickMenu(mouseEvent: MouseEvent) {
        Helper.trace("MenuAction::onclick")
        val xhr = XMLHttpRequest()
        xhr.onloadend = { e: Event ->
            Helper.trace("MenuAction::onclick: Load End ")
            e.preventDefault()
            val text = xhr.responseText
            if (!text.startsWith("__")) {
                if (xhr.getResponseHeader("Content-disposition")?.contains("attachment") == true) {
                    var contentDispo = xhr.getResponseHeader("Content-Disposition")
                    var fileName = Regex("filename[^;=\\n]*=((['\"]).*?\\2|[^;\\n]*)").find(contentDispo!!)
                    if (fileName != null)
                        Helper.saveOrOpenBlob(xhr.response as Blob, fileName.value)
                } else {
                    window.document.write(text)
                    window.history.pushState("", "Intranet ", action)
                }
            } else {
                processAjaxLink(text, parent)
            }
        }
        xhr.open("GET", action)
        xhr.send()
    }
}