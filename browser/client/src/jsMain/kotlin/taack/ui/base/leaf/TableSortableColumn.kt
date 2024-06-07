package taack.ui.base.leaf

import kotlinx.browser.window
import org.w3c.dom.*
import org.w3c.dom.events.MouseEvent
import org.w3c.fetch.RequestInit
import org.w3c.xhr.FormData
import taack.ui.base.Helper
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.LeafElement
import taack.ui.base.element.Table
import kotlin.js.Promise

class TableSortableColumn(private val parent: Table, private val s: HTMLSpanElement) : LeafElement {
    companion object {
        fun getSiblingSortableColumn(p: Table): List<TableSortableColumn>? {
            val elements: List<Node>?
            elements = p.t.querySelectorAll("span[sortField]").asList()
            return elements.map {
                TableSortableColumn(p, it as HTMLSpanElement)
            }
        }
    }

    private val property: String = s.attributes["sortField"]!!.value
    private val direction: String?

    init {
        val fd = FormData(parent.filter.f)
        if (property == fd.get("sort")) {
            direction = fd.get("order")
        } else {
            direction = null
        }
        trace("SortableColumn::init $property $direction")
        if (direction != null && direction != "") s.classList.add(direction)
        val a = s.childNodes[0] as HTMLAnchorElement
        a.onclick = { e ->
            onClick(e)
        }
    }

    private fun onClick(e: MouseEvent) {
        e.preventDefault()
        trace("SortableColumn::onClick")
        val f = parent.filter.f
        val fd = FormData(f)
        fd.set("sort", property)
        val dir = if (direction == null || direction == "") "desc" else if (direction == "desc") "asc" else null
        if (dir != null) fd.set("order", dir)
        else fd.delete("order")
        fd.append("isAjax", "true")
        fd.append("refresh", "true")
        trace("filterTableId = ${parent.parent.blockId}")
        fd.append("filterTableId", parent.parent.blockId)
        val button = f.querySelector("button[formaction]") as HTMLButtonElement
        window.fetch(button.formAction, RequestInit(method = "POST", body = fd)).then {
            if (it.ok) {
                it.text()
            } else {
                trace(it.statusText)
                Promise.reject(Throwable())
            }
        }.then {
            Helper.mapAjaxText(it).map { me ->
                parent.parent.updateContent(me.value)
            }
//        }.then {
//            AjaxBlock.getSiblingAjaxBlock(parent.parent.parent)
        }
    }
}