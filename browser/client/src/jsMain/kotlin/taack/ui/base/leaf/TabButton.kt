package taack.ui.base.leaf

import js.array.asList
import taack.ui.base.BaseElement
import taack.ui.base.Helper
import taack.ui.base.Helper.Companion.mapAjaxBlock
import taack.ui.base.Helper.Companion.processAjaxLink
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.base.element.AjaxBlock
import taack.ui.base.element.Block
import web.dom.Element
import web.dom.document
import web.events.EventHandler
import web.form.FormData
import web.form.FormMethod
import web.html.*
import web.http.RequestMethod
import web.xhr.XMLHttpRequest

class TabButton(val parent: Block, val b: HTMLButtonElement) : BaseElement  {
    companion object {
        fun getSiblingTabButton(p: Block): List<TabButton> {
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
            val fd = FormData()
            fd["isAjax"] = "true"
            fd["refresh"] = "true"
            fd["tabIndex"] = b.getAttribute("id")!!.split("-").last()
            fd["tabId"] = b.getAttribute("id")!!.split("-")[1]
            val xhr = XMLHttpRequest()
            xhr.onloadend = EventHandler {
                val div: Element? = parent.d.querySelector(".tab-content")
                if (div != null) {
                    div.innerHTML = xhr.responseText
                    AjaxBlock.getSiblingAjaxBlock(parent)
                }
            }
            xhr.open(RequestMethod.POST, b.getAttribute("action")!!)
            xhr.send(fd)
        }
    }
    override fun getParentBlock(): Block {
        return parent
    }
}