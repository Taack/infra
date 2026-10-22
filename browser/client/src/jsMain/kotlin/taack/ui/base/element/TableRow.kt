package taack.ui.base.element

import js.array.asList
import kotlinx.browser.document
import taack.ui.base.BaseElement
import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.base.leaf.TableRowFormErrorInput
import web.cssom.ClassName
import web.events.EventHandler
import web.html.ButtonType
import web.html.HTMLButtonElement
import web.html.HTMLTableCellElement
import web.html.HTMLTableRowElement
import web.html.button

class TableRow(val parent: Table, val r: HTMLTableRowElement) :
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
    private val cells = TableRowCell.getSiblingRowCells(this)
    private val forms = TableRowForm.getSiblingForm(this)
    private val expander: HTMLButtonElement? = if (rowGroup != null && rowGroupHasChildren == true) expanderButton() else null

    init {
        traceIndent("TableRow::init +++ ${rowGroup ?: ""} ${rowGroupHasChildren ?: ""}")
        expander?.onclick = EventHandler { onclick() }
        traceDeIndent("TableRow::init ---")
    }

    private fun expanderButton(): HTMLButtonElement? {
        val cell = r.querySelector(".firstCellInGroup") as HTMLTableCellElement? ?: return null

        (cell.querySelector("button.taackTreeExpander") as HTMLButtonElement?)?.let { return it }
        val button = document.createElement("button") as HTMLButtonElement
        button.type = ButtonType.button
        button.classList.add(ClassName("taackTreeExpander"))
        button.setAttribute("aria-label", cell.textContent?.trim() ?: "")
        button.setAttribute("aria-expanded", isExpanded().toString())
        cell.prepend(button)
        return button
    }

    private fun isExpanded(): Boolean = r.attributes.getNamedItem("taackTableRowIsExpended")?.value?.toBoolean() == true

    private fun setExpanded(expanded: Boolean) {
        r.setAttribute("taackTableRowIsExpended", expanded.toString())
        expander?.setAttribute("aria-expanded", expanded.toString())
    }

    private fun expends() {
        setExpanded(true)
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
        setExpanded(false)
        var collapse = false
        val rg = rowGroup!! + 1

        for (r in parent.rows) {
            if (collapse && r.rowGroup!! >= rg) {
                r.r.style.display = "none"
                if (r.rowGroupHasChildren == true) {
                    r.setExpanded(false)
                }
            } else if (collapse && r.rowGroup!! == rowGroup) {
                break
            }
            if (r === this) {
                collapse = true
            }
        }
    }

    private fun onclick() {
        if (isExpanded()) {
            collapse()
        } else {
            expends()
        }
    }

    override fun getParentBlock(): Block {
        return parent.getParentBlock()
    }
}