package taack.ui.base.element

import js.array.asList
import taack.ui.base.BaseElement
import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import web.events.EventHandler
import web.html.HTMLTableCellElement
import web.html.HTMLTableRowElement

class TableRow(val parent: Table, private val r: HTMLTableRowElement) :
    BaseElement {
    companion object {
        fun getSiblingRows(p: Table): List<TableRow> {
            val elements: List<*> = p.t.querySelectorAll("tr[taacktag]").asList()
            return elements.map {
                TableRow(p, it as HTMLTableRowElement)
            }
        }
    }

    private val rowGroup: Int? = r.attributes.getNamedItem("taackTableRowGroup")?.value?.toInt()
    private val rowGroupHasChildren: Boolean? = r.attributes.getNamedItem("taackTableRowGroupHasChildren")?.value?.toBoolean()

    init {
        traceIndent("TableRow::init +++ ${rowGroup ?: ""} ${rowGroupHasChildren ?: ""}")
        if (rowGroup != null && rowGroupHasChildren == true) {
            (r.querySelector(".firstCellInGroup")!! as HTMLTableCellElement).onclick = EventHandler { e ->
                if (e.target is HTMLTableCellElement) {
                    val offsetX = e.clientX - (e.target as HTMLTableCellElement).getBoundingClientRect().left
                    if (offsetX in (5.0 + 20.0 * rowGroup)..(15.0 + 20.0 * rowGroup)) {
                        onclick()
                    }
                }
            }
        }
        traceDeIndent("TableRow::init ---")
    }

    private fun expends() {
        r.setAttribute("taackTableRowIsExpended", "true")
        var expends = false
        val rg = rowGroup!! + 1

        for (r in parent.rows) {
            if (expends && r.rowGroup == rg) {
                r.r.style.removeProperty("display")
            } else if (expends && r.rowGroup == rowGroup) {
                break
            }
            if (r === this) {
                expends = true
            }
        }
    }

    private fun collapse() {
        r.setAttribute("taackTableRowIsExpended", "false")
        var collapse = false
        val rg = rowGroup!! + 1

        for (r in parent.rows) {
            if (collapse && r.rowGroup!! >= rg) {
                r.r.style.display = "none"
                if (rowGroupHasChildren == true) {
                    r.r.setAttribute("taackTableRowIsExpended", "false")
                }
            } else if (collapse && r.rowGroup!! == rowGroup) {
                break
            }
            if (r === this) {
                collapse = true
            }
        }
    }

    private fun onclick(): Boolean {
        if (r.attributes.getNamedItem("taackTableRowIsExpended")?.value?.toBoolean() == true) {
            collapse()
        } else {
            expends()
        }
        return false
    }

    override fun getParentBlock(): Block {
        return parent.getParentBlock()
    }
}