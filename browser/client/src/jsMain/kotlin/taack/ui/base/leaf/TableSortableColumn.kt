package taack.ui.base.leaf

import org.w3c.dom.*
import org.w3c.dom.events.MouseEvent
import org.w3c.xhr.FormData
import taack.ui.base.Helper
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.LeafElement
import taack.ui.base.element.Table

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
        val dir = if (direction == null || direction == "") "desc" else if (direction == "desc") "asc" else null
        Helper.filterForm(parent.filter, null, property, dir)
    }
}