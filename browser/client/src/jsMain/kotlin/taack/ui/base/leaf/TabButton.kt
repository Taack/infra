package taack.ui.base.leaf

import js.array.asList
import taack.ui.base.BaseElement
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.base.element.AjaxBlock
import taack.ui.base.element.Block
import taack.ui.base.element.Tab
import web.dom.Element
import web.dom.parsing.DOMParser
import web.dom.parsing.DOMParserSupportedType
import web.events.EventHandler
import web.form.FormData
import web.html.*
import web.http.RequestMethod
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
            val tabId = b.getAttribute("id")!!.split("-")[1]
            val fd = FormData()
            fd["isAjax"] = "true"
            fd["refresh"] = "true"
            fd["tabIndex"] = tabIndex
            fd["tabId"] = tabId
            val xhr = XMLHttpRequest()
            xhr.onloadstart = EventHandler {
                b.onclick = EventHandler { console.log("Tab already requested")}
            }
            xhr.onloadend = EventHandler {
                // Make new parser to parse xhr.responseText
                val parser = DOMParser()
                // Parse to a DOM document to allow query selection
                val responseDoc = parser.parseFromString(xhr.responseText, DOMParserSupportedType.textHtml)
                // Get the clicked tab from the response
                val tabResponseEl = responseDoc.querySelector("#tab-${tabId}-${tabIndex}-pane")
                val div: Element? = parent.parent.d.querySelector(".tab-content")
                println("div: ${div?.id}")
                if (div != null && tabResponseEl != null) {
                    // Get the clicked tab content pane and fill it with the previously parsed response's inner html
                    div.querySelector("#tab-${tabId}-${tabIndex}-pane")?.innerHTML = tabResponseEl.innerHTML
                    AjaxBlock.getSiblingAjaxBlock(parent.parent)
                }
            }
            //Show loading spinner while loading
            parent.parent.d.querySelector("#tab-${tabId}-${tabIndex}-pane")?.innerHTML = "<div class='taack-tab-load'></div>"
            xhr.open(RequestMethod.POST, b.getAttribute("action")!!)
            xhr.send(fd)
        }
    }
    override fun getParentBlock(): Block {
        return parent.parent
    }
}