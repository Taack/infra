package taack.ui.base.leaf

import kotlinx.browser.window
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.Node
import org.w3c.dom.asList
import org.w3c.dom.events.Event
import org.w3c.dom.url.URL
import org.w3c.xhr.XMLHttpRequest
import taack.ui.base.Helper.Companion.processAjaxLink
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.LeafElement
import taack.ui.base.element.AjaxBlock
import taack.ui.base.record.RecordState

class AjaxLink(private val parent: AjaxBlock, private val a: HTMLAnchorElement) : LeafElement {
    companion object {
        fun getSiblingAjaxLink(p: AjaxBlock): List<AjaxLink> {
            val elements: List<Node>?
            elements = p.d.querySelectorAll("a.taackAjaxLink").asList()
            return elements.map {
                AjaxLink(p, it as HTMLAnchorElement)
            }
        }

        fun createUrl(action: String, additionalParams: Map<String, String>? = null) : URL {
            val url = URL(action, "${window.location.protocol}//${window.location.host}")
            url.searchParams.set("isAjax", "true")
            additionalParams?.forEach {
                url.searchParams.set(it.key, it.value)
            }
            if (!url.searchParams.has("recordState")) url.searchParams.set("recordState", RecordState.dumpServerState())
            return url
        }
    }

    init {
        trace("AjaxLink::init")
        a.addEventListener("click", { e -> onclick(e) })
    }

    private val action: String? = a.attributes.getNamedItem("ajaxAction")?.value

    private fun onclick(e: Event) {
        trace("AjaxLink::onclick")
        val xhr = XMLHttpRequest()
        xhr.onloadend = { e: Event ->
            trace("AjaxLink::onclick: Load End $action")
            e.preventDefault()
            val text = xhr.responseText
            processAjaxLink(text, parent.parent)

        }
        if (action != null) {
            xhr.open("GET", createUrl(action).toString())
            xhr.send()
        }
    }
}

