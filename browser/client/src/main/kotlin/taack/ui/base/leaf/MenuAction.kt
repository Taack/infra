package taack.ui.base.leaf

import kotlinx.browser.document
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.Node
import org.w3c.dom.asList
import org.w3c.dom.events.Event
import org.w3c.xhr.XMLHttpRequest
import taack.ui.base.Helper
import taack.ui.base.Helper.Companion.processAjaxLink
import taack.ui.base.element.Block

class MenuAction(private val parent: Block, private val a: HTMLAnchorElement) {
    companion object {
        fun getAjaxMenu(p: Block): List<MenuAction> {
            val elements: List<Node>?
            elements = document.querySelectorAll("a.taackAjaxMenuLink").asList()
            return elements.map {
                MenuAction(p, it as HTMLAnchorElement)
            }
        }
    }

    private val action: String = a.attributes.getNamedItem("ajaxAction")!!.value

    init {
        Helper.trace("MenuAction::init")
        a.onclick = { e ->
            onclick(e)
        }
    }

    private fun onclick(e: Event) {
        Helper.trace("MenuAction::onclick")
        val xhr = XMLHttpRequest()
        xhr.onloadend = { e: Event ->
            Helper.trace("MenuAction::onclick: Load End ")
            e.preventDefault()
            val text = xhr.responseText
            processAjaxLink(text, parent)

        }
        xhr.open("GET", action)
        xhr.send()
    }
}