package taack.ui.base.leaf

import kotlinx.browser.window
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.Node
import org.w3c.dom.asList
import org.w3c.dom.events.Event
import org.w3c.fetch.RequestInit
import org.w3c.xhr.FormData
import taack.ui.base.Helper
import taack.ui.base.Helper.Companion.processAjaxLink
import taack.ui.base.LeafElement
import taack.ui.base.element.AjaxBlock
import taack.ui.base.element.TableRow
import kotlin.js.Promise

class TableAjaxInput(private val parent: TableRow, private val i: HTMLInputElement) : LeafElement {
    companion object {
        fun getSiblingTableAjaxInput(r: TableRow): List<TableAjaxInput> {
            val elements: List<Node>?
            elements = r.r.querySelectorAll("input.taackAjaxTableInput").asList()
            return elements.map {
                TableAjaxInput(r, it as HTMLInputElement)
            }
        }

        var isLocked = false
    }

    init {
        Helper.trace("TableAjaxInput::init ${i.name}")
        i.onblur = { e ->
            e.preventDefault()
            synchronized(isLocked) {
                if (!isLocked) {
                    isLocked = true
                    onclick(e)
                }
            }
        }
    }

//    private fun processAjaxLink(text: String) {
//        val abs = "__ajaxBlockStart__"
//        val m = "closeLastModal:"
//
//        when {
//            text.startsWith(m) -> {
//                val pos = text.indexOf(':', m.length)
//                val id = text.substring(m.length, pos)
//                val value = text.substring(pos + 1)
//                Helper.trace("ShowAjaxSelect::closing Modal")
//                parent.parent.parent.parent.modal.close()
//            }
//            text.startsWith(abs) -> {
//                Helper.mapAjaxText(text).map {
//                    val target = parent.parent.parent.parent.ajaxBlockElements?.get(it.key)
//                    target!!.d.innerHTML = it.value
////                    target.refresh()
//                }
//            }
//            else -> {
//                Helper.trace("TableAjaxInput::opening Modal")
//                parent.parent.parent.parent.modal.open(text)
//            }
//        }
//    }

    private fun onclick(e: Event) {
        Helper.trace("TableAjaxInput::onclick")
        val f = i.form!! //parentElement!!.parentElement!!.

        val fd = FormData(f)
        fd.append("isAjax", "true")
        val ajaxParams = i.attributes.getNamedItem("ajaxParams")?.value
        window.fetch("${f.action}?${ajaxParams?:""}", RequestInit(method = "POST", body = fd)).then {
            if (it.ok) {
                it.text()
            } else {
                Helper.trace(it.statusText)
                Promise.reject(Throwable())
            }
        }.then {
            processAjaxLink(it, parent.parent.parent.parent)
//            mapAjaxText(it).map { me ->
//                parent.parent.d.innerHTML = me.value
//            }
        }.then {
            AjaxBlock.getSiblingAjaxBlock(parent.parent.parent.parent)
            isLocked = false
        }
    }

}