package taack.ui.base.leaf

import js.array.asList
import taack.ui.base.Helper
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.LeafElement
import taack.ui.base.element.Table
import web.events.EventHandler
import web.form.FormData
import web.html.HTMLAnchorElement
import web.html.HTMLSpanElement
import web.uievents.MouseEvent

class TableSortableColumn(private val parent: Table, s: HTMLSpanElement) : LeafElement {
    companion object {
        fun getSiblingSortableColumn(p: Table): List<TableSortableColumn> {
            val elements: List<*> = p.t.querySelectorAll("span[sortField]").asList()
            return elements.map {
                TableSortableColumn(p, it as HTMLSpanElement)
            }
        }
    }

    private val property: String = s.attributes.getNamedItem("sortField")!!.value
    private val direction: String

    init {
        val fd = FormData(parent.filter.f)
        if (property == fd["sort"]) {
            var d: String = if (fd["order"] != null) fd["order"]!!.toString() else "neutral"
            if (d.trim().isEmpty()) {
                d = "neutral"
            }
            direction = d.trim()
        } else {
            direction = "neutral"
        }
        trace("SortableColumn::init $property $direction")
        s.classList.add(direction)
        s.onclick = EventHandler{ e ->
            onClick(e)
        }
    }

    private fun onClick(e: MouseEvent) {
        e.preventDefault()
        trace("SortableColumn::onClick")
        val dir = when (direction) {
            "neutral" -> "desc"
            "desc" -> "asc"
            "asc" -> if (property == parent.initialSortField) "desc" else "neutral"
            else -> null
        }
        Helper.filterForm(parent.filter, null, property, dir)
    }
}