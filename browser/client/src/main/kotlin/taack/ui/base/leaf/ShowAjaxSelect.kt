package taack.ui.base.leaf

import kotlinx.browser.window
import org.w3c.dom.*
import org.w3c.dom.events.Event
import org.w3c.fetch.RequestInit
import org.w3c.xhr.FormData
import taack.ui.base.Helper.Companion.mapAjaxText
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.LeafElement
import taack.ui.base.element.AjaxBlock
import taack.ui.base.element.Show
import kotlin.js.Promise

class ShowAjaxSelect(private val parent: Show, private val s: HTMLSelectElement) : LeafElement {
    companion object {
        fun getSiblingShowAjaxSelect(p: Show): List<ShowAjaxSelect> {
            val elements: List<Node>?
            elements = p.d.querySelectorAll("select.taackShowInput").asList()
            return elements.map {
                ShowAjaxSelect(p, it as HTMLSelectElement)
            }
        }
    }

    init {
        trace("ShowAjaxSelect::init ${s.name}")
        s.addEventListener("change", { e -> onclick(e) })
    }


    private fun processAjaxLink(text: String) {
        val abs = "__ajaxBlockStart__"
        val m = "closeLastModal:"

        when {
            text.startsWith(m) -> {
                val pos = text.indexOf(':', m.length)
                val id = text.substring(m.length, pos)
                val value = text.substring(pos + 1)
                trace("ShowAjaxSelect::closing Modal")
                parent.parent.parent.modal.close()
            }
            text.startsWith(abs) -> {
                mapAjaxText(text).map {
                    val target = parent.parent.parent.ajaxBlockElements?.get(it.key)
                    target!!.d.innerHTML = it.value
                    target.refresh()
                }
            }
            else -> {
                trace("ShowAjaxSelect::opening Modal")
                parent.parent.parent.modal.open(text)
            }
        }
    }

    private fun onclick(e: Event) {
        e.preventDefault()
        trace("ShowAjaxSelect::onclick")
        val f = s.form!!
        val fd = FormData(f)
        fd.append("isAjax", "true")
        window.fetch("", RequestInit(method = "POST", body = fd)).then {
            if (it.ok) {
                it.text()
            } else {
                trace(it.statusText)
                Promise.reject(Throwable())
            }
        }.then {
            processAjaxLink(it)
//            mapAjaxText(it).map { me ->
//                parent.parent.d.innerHTML = me.value
//            }
        }.then {
            AjaxBlock.getSiblingAjaxBlock(parent.parent.parent)
        }
    }
}

