package taack.ui.base.leaf

import js.array.asList
import taack.ui.base.Helper
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.LeafElement
import taack.ui.base.element.Table
import web.cssom.ClassName
import web.events.EventHandler
import web.form.FormData
import web.html.HTMLButtonElement
import web.mouse.MouseEvent

class TableSortableColumn(private val parent: Table, s: HTMLButtonElement) : LeafElement {
    companion object {
        fun getSiblingSortableColumn(p: Table): List<TableSortableColumn> {
            val elements: List<*> = p.t.querySelectorAll("button[sortField]").asList()
            return elements.map {
                TableSortableColumn(p, it as HTMLButtonElement)
            }
        }
    }

    private val property: String = s.attributes.getNamedItem("sortField")!!.value
    private val direction: String

    init {
        val fd = FormData(parent.filter.f)
        if (property == fd.get("sort")) {
            var d: String = if (fd.get("order") != null) fd.get("order")!!.toString() else "neutral"
            if (d.trim().isEmpty()) {
                d = "neutral"
            }
            direction = d.trim()
        } else {
            direction = "neutral"
        }
        trace("SortableColumn::init $property $direction")
        s.classList.add(ClassName(direction))
        val ariaSort = when (direction) {
            "asc" -> "ascending"
            "desc" -> "descending"
            else -> null
        }
        if (ariaSort != null) s.closest("th")?.setAttribute("aria-sort", ariaSort)
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