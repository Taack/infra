package taack.ui.base.leaf

import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.Node
import org.w3c.dom.asList
import org.w3c.dom.events.Event
import org.w3c.xhr.XMLHttpRequest
import taack.ui.base.Helper.Companion.processAjaxLink
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.LeafElement
import taack.ui.base.element.Show

class ShowActionLink(private val parent: Show, private val a: HTMLAnchorElement) : LeafElement {
    companion object {
        fun getSiblingShowActionLink(p: Show): List<ShowActionLink> {
            val elements: List<Node>?
            elements = p.d.querySelectorAll("a.taackShowAction").asList()
            return elements.map {
                ShowActionLink(p, it as HTMLAnchorElement)
            }
        }
    }

    init {
        trace("ShowActionLink::init")
        a.addEventListener("click", { e -> onclick(e) })
    }

    private val ajaxUrl: String = a.attributes.getNamedItem("taackShowActionLink")!!.value

//    private fun processAjaxLink(text: String) {
//        val abs = "__ajaxBlockStart__"
//        val m = "closeLastModal:"
//
//        if (text.startsWith(m)) {
//            val pos = text.indexOf(':', m.length)
//            val id = text.substring(m.length, pos)
//            val value = text.substring(pos + 1)
//            trace("ShowActionLink::closing Modal")
//            parent.parent.parent.modal.close()
//        } else if (text.startsWith(abs)) {
//            mapAjaxText(text).map {
//                val target = parent.parent.parent.ajaxBlockElements?.get(it.key)
//                target!!.d.innerHTML = it.value
//                target.refresh()
//            }
//        } else {
//            trace("ShowActionLink::opening Modal")
//            parent.parent.parent.modal.open(text)
//        }
//    }

    private fun onclick(e: Event) {
        e.preventDefault()
        trace("ShowActionLink::onclick")
        val xhr = XMLHttpRequest()
        xhr.onloadend = { ev: Event ->
            ev.preventDefault()
            trace("ShowActionLink::onclick: Load End")
            val text = xhr.responseText
            processAjaxLink(text, parent.parent.parent)
        }
        xhr.open("GET", ajaxUrl)
        xhr.send()
    }
}

