package taack.ui.base.leaf

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
            val elements: List<HTMLSpanElement>?
            elements = p.t.querySelectorAll("span[sortField]") as List<HTMLSpanElement>
            return elements.map {
                TableSortableColumn(p, it)
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
        val a = s.childNodes[0] as HTMLAnchorElement
        a.onclick = EventHandler{ e ->
            onClick(e)
        }
    }

    private fun onClick(e: MouseEvent) {
        e.preventDefault()
        trace("SortableColumn::onClick")
        val dir = if (direction == "neutral") "desc" else if (direction == "desc") "asc" else if (direction == "asc") "neutral" else null
        Helper.filterForm(parent.filter, null, property, dir)
    }
}