package taack.ui.base.leaf

import kotlinx.browser.window
import org.w3c.dom.*
import org.w3c.dom.events.MouseEvent
import org.w3c.fetch.RequestInit
import org.w3c.xhr.FormData
import taack.ui.base.Helper
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.LeafElement
import taack.ui.base.element.AjaxBlock
import taack.ui.base.element.Table
import kotlin.js.Promise

class TableGroupableColumn(private val parent: Table, private val s: HTMLSpanElement) : LeafElement {
    companion object {
        fun getSiblingGroupableColumn(p: Table): List<TableGroupableColumn>? {
            val elements: List<Node>?
            elements = p.t.querySelectorAll("span[groupField]").asList()
            return elements.map {
                TableGroupableColumn(p, it as HTMLSpanElement)
            }
        }
        var grouping = 0
    }

    private val property: String = s.attributes["groupField"]!!.value
    private val direction: String?
    private val groupCheck: HTMLInputElement = s.querySelector("input")!! as HTMLInputElement

    init {
        val fd = FormData(parent.filter.f)
        if (property == fd.get("sort")) {
            direction = fd.get("order")
        } else {
            direction = null
        }
        trace("TableGroupableColumn::init $property $direction")
        if (direction != null && direction != "") s.classList.add(direction)
        val a = s.childNodes[0] as HTMLAnchorElement
        a.onclick = { e ->
            onClick(e)
        }

        groupCheck.checked = fd.get("grouping")!!.toString().contains(property)
        groupCheck.onclick = {e ->
            onGroup(e)
        }
    }

    private fun onGroup(e: MouseEvent) {
        trace("TableGroupableColumn::onGroup")
        val f = parent.filter.f
        val fd = FormData(f)
        val g = fd.get("grouping")!! as String
        val isGrouped = g.contains(property)
        if (!isGrouped) {
            fd.set("grouping", "$g $property".trim())
        } else {
            fd.set("grouping", g.replace(property, "").trim())
        }
        fd.append("isAjax", "true")
        window.fetch("", RequestInit(method = "POST", body = fd)).then {
            if (it.ok) {
                it.text()
            } else {
                trace(it.statusText)
                Promise.reject(Throwable())
            }
        }.then {
            Helper.mapAjaxBlock(it).map { me ->
                parent.parent.d.innerHTML = me.value
            }
        }.then {
            AjaxBlock.getSiblingAjaxBlock(parent.parent.parent)
        }

    }

    private fun onClick(e: MouseEvent) {
        e.preventDefault()
        trace("TableGroupableColumn::onClick")
        val dir = if (direction == null || direction == "") "desc" else if (direction == "desc") "asc" else null
        Helper.filterForm(parent.filter, null, property, dir)
    }
}