package taack.ui.base.leaf

import js.array.asList
import taack.ui.base.BaseElement
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.base.element.AjaxBlock
import taack.ui.base.element.Block
import taack.ui.base.element.Tab
import web.cssom.ClassName
import web.dom.Element
import web.events.EventHandler
import web.form.FormData
import web.html.*
import web.http.POST
import web.http.RequestMethod
import web.parsing.DOMParser
import web.parsing.DOMParserSupportedType
import web.parsing.textHtml
import web.url.URL
import web.xhr.XMLHttpRequest

class TabButton(val parent: Tab, val b: HTMLButtonElement) : BaseElement  {
    companion object {
        fun getSiblingTabButton(p: Tab): List<TabButton> {
            val elements: List<*> = p.d.querySelectorAll("button[role='tab']:not(form button)").asList()
            return elements.map {
                TabButton(p, it as HTMLButtonElement)
            }
        }
    }

    init {
        traceIndent("TabButton::init +++ id: ${b.id}")
        b.onclick = EventHandler { e ->
            e.preventDefault()
            val tabIndex = b.getAttribute("id")!!.split("-").last()
            if (Block.href != null) {
                val url = URL(Block.href!!)
                url.searchParams.set("tabIndex", tabIndex)
                Block.href = url.toString()
            }

            val tabId = b.getAttribute("id")!!.split("-")[1]
            val div: Element? = parent.d.parentElement?.querySelector(".tab-content")
            var divTab: Element? = null
            val fd = FormData()
            fd.set("isAjax", "true")
            fd.set("refresh", "true")
            fd.set("tabIndex", tabIndex)
            fd.set("tabId", tabId)
            val xhr = XMLHttpRequest()
            xhr.onloadend = EventHandler {
                // Make new parser to parse xhr.responseText
                val parser = DOMParser()
                // Parse to a DOM document to allow query selection
                val responseDoc = parser.parseFromString(xhr.responseText, DOMParserSupportedType.textHtml)
                // Get the clicked tab from the response
                val tabResponseEl = responseDoc.querySelector("#tab-${tabId}-${tabIndex}-pane")
                if (div != null && divTab != null && tabResponseEl != null) {
                    // Get the clicked tab content pane and fill it with the previously parsed response's inner html
                    divTab!!.innerHTML = tabResponseEl.innerHTML
                    divTab!!.classList.add(ClassName("loaded"))
                    AjaxBlock.getSiblingAjaxBlock(parent.parent)
                }
            }
            if (div != null) divTab = div.querySelector("#tab-${tabId}-${tabIndex}-pane")
            // Avoid requesting twice the same tab content
            if (divTab != null && !divTab.classList.contains(ClassName("loaded"))) {
                // Show loading spinner while loading
                parent.parent.d.querySelector("#tab-${tabId}-${tabIndex}-pane")?.innerHTML = "<div class='taack-tab-load'></div>"
                xhr.open(RequestMethod.POST, b.getAttribute("action")!!)
                xhr.send(fd)
            }
        }
    }
    override fun getParentBlock(): Block {
        return parent.parent
    }
}