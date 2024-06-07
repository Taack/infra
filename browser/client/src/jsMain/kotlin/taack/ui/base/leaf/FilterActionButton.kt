package taack.ui.base.leaf

import kotlinx.browser.window
import org.w3c.dom.*
import org.w3c.dom.events.Event
import org.w3c.fetch.RequestInit
import org.w3c.xhr.FormData
import taack.ui.base.Helper
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.LeafElement
import taack.ui.base.element.Block
import taack.ui.base.element.Filter
import kotlin.js.Promise

class FilterActionButton(private val parent: Filter, private val b: HTMLButtonElement?) : LeafElement {
    companion object {
        fun getSiblingFilterAction(f: Filter): List<FilterActionButton> {
            val elements: List<Node>?
            elements = f.f.querySelectorAll("button[formaction]").asList()
            return elements.map {
                FilterActionButton(f, it as HTMLButtonElement)
            }
        }
    }

    init {
        trace("FilterActionButton::init ${b?.id}")
        b?.onclick = { e ->
            onClick(e)
        }
    }

    private fun onClick(e: Event) {
        b?.disabled = true
        val innerText = b?.innerText
        b?.innerText = "Submitting ..."
        e.preventDefault()
        trace("FilterActionButton::onclick")
        val pf = parent.f
        val fd = FormData(pf)
        fd.append("isAjax", "true")
        fd.append("refresh", "true")
        fd.append("filterTableId", parent.filterId)
        fd.append("ajaxBlockId", parent.parent.blockId)
        fd.set("offset", "0")
        window.fetch(b?.formAction ?: pf.action, RequestInit(method = "POST", body = fd)).then {
            if (it.ok) {
                it.text()
            } else {
                trace(it.statusText)
                Promise.reject(Throwable())
            }
        }.then {
            if (it.startsWith("__redirect__")) {
                trace("FilterActionButton::onclick __redirect__ ${it.substring("__redirect__".length)}")
                window.location.href = it.substring("__redirect__".length)
            } else if (it.startsWith("__reload__")) {
                trace("FilterActionButton::onclick __reload__")
                window.location.href = (Block.href ?: "")
            } else if (it.startsWith("__ajaxBlockStart__")) {
                trace("FilterActionButton::onclick __ajaxBlockStart__")
                Helper.mapAjaxText(it).map { me ->
                    val target = parent.parent.parent.ajaxBlockElements?.get(me.key)
                    if (target != null) {
                        target.updateContent(me.value)
                    } else {
                        trace("FilterActionButton::onclick no target ${me.key}")
                    }
                }
            } else {
                trace("FilterActionButton::onclick other content")
                parent.parent.parent.updateContent(it)
            }
        }.then {
            b?.disabled = false
            if (innerText != null) b?.innerText = innerText
        }
    }
}