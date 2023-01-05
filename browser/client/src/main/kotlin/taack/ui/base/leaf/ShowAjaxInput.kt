package taack.ui.base.leaf

import kotlinx.browser.window
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.Node
import org.w3c.dom.asList
import org.w3c.dom.events.Event
import org.w3c.fetch.RequestInit
import org.w3c.xhr.FormData
import taack.ui.base.Helper.Companion.processAjaxLink
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.LeafElement
import taack.ui.base.element.AjaxBlock
import taack.ui.base.element.Show
import kotlin.js.Promise

class ShowAjaxInput(private val parent: Show, private val i: HTMLInputElement) : LeafElement {
    companion object {
        fun getSiblingShowAjaxInput(p: Show): List<ShowAjaxInput> {
            val elements: List<Node>?
            elements = p.d.querySelectorAll("input.taackShowInput").asList()
            return elements.map {
                ShowAjaxInput(p, it as HTMLInputElement)
            }
        }
    }

    init {
        trace("ShowAjaxInput::init ${i.name}")
        i.addEventListener("change", { e -> onclick(e) })
    }

//
//    private fun processAjaxLink(text: String) {
//        val abs = "__ajaxBlockStart__"
//        val m = "closeLastModal:"
//
//        when {
//            text.startsWith(m) -> {
//                val pos = text.indexOf(':', m.length)
//                val id = text.substring(m.length, pos)
//                val value = text.substring(pos + 1)
//                trace("ShowAjaxInput::closing Modal")
//                parent.parent.parent.modal.close()
//            }
//            text.startsWith(abs) -> {
//                mapAjaxText(text).map {
//                    println("AUO321 ${it.key}:${it.key.substring(abs.length)}")
//                    println("AUO322 ${parent.parent.parent.ajaxBlockElements}")
//                    val target = parent.parent.parent.ajaxBlockElements?.get(it.key)
//                    target!!.d.innerHTML = it.value
//                    target.refresh()
//                }
//            }
//            else -> {
//                trace("ShowAjaxInput::opening Modal")
//                parent.parent.parent.modal.open(text)
//            }
//        }
//    }

    private fun onclick(e: Event) {
        e.preventDefault()
        trace("ShowAjaxInput::onclick")
        val f = i.form!!
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
            processAjaxLink(it, parent.parent.parent)
//            mapAjaxText(it).map { me ->
//                parent.parent.d.innerHTML = me.value
//            }
        }.then {
            AjaxBlock.getSiblingAjaxBlock(parent.parent.parent)
        }
    }
}

