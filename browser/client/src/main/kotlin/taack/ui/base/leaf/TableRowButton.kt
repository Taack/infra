package taack.ui.base.leaf

import kotlinx.browser.window
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.Node
import org.w3c.dom.asList
import org.w3c.dom.events.Event
import org.w3c.fetch.RequestInit
import org.w3c.xhr.FormData
import taack.ui.base.Helper.Companion.processAjaxLink
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.LeafElement
import taack.ui.base.element.AjaxBlock
import taack.ui.base.element.TableRow
import kotlin.js.Promise

class TableRowButton(private val parent: TableRow, private val b: HTMLButtonElement) : LeafElement {
    companion object {
        fun getSiblingTableRowButton(p: TableRow): List<TableRowButton> {
            val elements: List<Node>?
            elements = p.r.querySelectorAll("button.taackAjaxRowButton").asList()
            return elements.map {
                TableRowButton(p, it as HTMLButtonElement)
            }
        }
    }

    init {
        trace("TableRowButton::init")
        b.addEventListener("click", { e -> onclick(e) })
    }

//    private fun processAjaxLink(text: String) {
//        val abs = "__ajaxBlockStart__"
//        val m = "closeLastModal:"
//
//        if (text.startsWith(m)) {
//            val pos = text.indexOf(':', m.length)
//            val id = text.substring(m.length, pos)
//            val value = text.substring(pos + 1)
//            trace("TableRowButton::closing Modal")
//            parent.parent.parent.parent.modal.close()
//        } else if (text.startsWith(abs)) {
//            mapAjaxText(text).map {
//                val target = parent.parent.parent.parent.ajaxBlockElements?.get(it.key)
//                target!!.d.innerHTML = it.value
////                target.refresh()
//            }
//        } else {
//            trace("TableRowButton::opening Modal")
//            parent.parent.parent.parent.modal.open(text)
////            Block.getSiblingBlock(parent.parent.parent.parent.modal)
//        }
//    }

    private fun onclick(e: Event) {
        e.preventDefault()
        trace("TableRowButton::onclick")
        val f = b.form!! //parentElement!!.parentElement!!.
        val fd = FormData(f)
        fd.append("isAjax", "true")
        window.fetch(f.action, RequestInit(method = "POST", body = fd)).then {
            if (it.ok) {
                it.text()
            } else {
                trace(it.statusText)
                Promise.reject(Throwable())
            }
        }.then {
            processAjaxLink(it, parent.parent.parent.parent)
//            mapAjaxText(it).map { me ->
//                parent.parent.d.innerHTML = me.value
//            }
        }.then {
            AjaxBlock.getSiblingAjaxBlock(parent.parent.parent.parent)
        }

    }
}

