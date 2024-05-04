package taack.ui.base.leaf

import kotlinx.browser.window
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.Node
import org.w3c.dom.asList
import org.w3c.dom.events.Event
import org.w3c.xhr.XMLHttpRequest
import taack.ui.base.Helper.Companion.processAjaxLink
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.LeafElement
import taack.ui.base.element.TableRow

class TableRowLink(private val parent: TableRow, private val a: HTMLAnchorElement) : LeafElement {
    companion object {
        fun getSiblingTableRowLink(p: TableRow): List<TableRowLink> {
            val elements: List<Node>?
            elements = p.r.querySelectorAll("a.taackAjaxRowLink").asList()
            return elements.map {
                TableRowLink(p, it as HTMLAnchorElement)
            }
        }
    }

    init {
        trace("TableRowLink::init")
        a.addEventListener("click", { e -> onclick(e) })
    }

    private val action: String? = a.attributes.getNamedItem("ajaxAction")?.value

    private fun onclick(e: Event) {
        e.preventDefault()
        trace("TableRowLink::onclick")
        val xhr = XMLHttpRequest()
        xhr.onloadend = { ev: Event ->
            ev.preventDefault()
            trace("TableRowLink::onclick: Load End")
            val text = xhr.responseText
            trace("|$text|")
            if (!text.startsWith("__")) {
                trace("AUO response identified like full page ...")
                window.document.write(text)
                window.history.pushState("", "Intranet ", action)
            } else {
                processAjaxLink(text, parent.parent.parent.parent)
            }
        }
        if (action != null) {
            xhr.open("GET", AjaxLink.createUrl(action).toString())
            xhr.send()
        }
    }
}

