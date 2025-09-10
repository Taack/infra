package taack.ui.base.element

import js.array.asList
import kotlinx.browser.document
import taack.ui.base.BaseElement
import taack.ui.base.Helper
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.base.leaf.*
import web.events.EventHandler
import web.html.HTMLTableElement
import web.xhr.XMLHttpRequest
import kotlin.math.min

class Table(val parent: AjaxBlock, val t: HTMLTableElement) :
    BaseElement {
    companion object {
        fun getSiblingTable(p: AjaxBlock): List<Table> {
            val elements: List<*> = p.d.querySelectorAll("table[taackTableId]").asList()
            return elements.map {
                Table(p, it as HTMLTableElement)
            }
        }
    }

    private val tableSortableColumns: List<TableSortableColumn>?
    private val tableGroupableColumns: List<TableGroupableColumn>?
    val rows: List<TableRow>
    val tableId = t.attributes.getNamedItem("taackTableId")!!.value
    val initialSortField = t.attributes.getNamedItem("initialSortField")?.value ?: ""
    val filter: Filter
    private val paginate: TablePaginate?
    private val dropAction: String?
    val tableSelectCheckboxes: Pair<TableSelectCheckbox, List<TableSelectCheckbox>>?
    private val tableMultiSelectButtons: List<TableMultiSelectButton>?

    init {
        traceIndent("Table::init +++ tableId: $tableId")
        dropAction = t.attributes.getNamedItem("taackDropAction")?.value
        if (dropAction != null) {

            t.ondragover = EventHandler { e ->
                e.preventDefault()
            }

            t.ondrop = EventHandler { e ->
                trace("Drag something on the table")
                Helper.ondrop(e, dropAction, { xhr: XMLHttpRequest ->
                    val t = xhr.responseText
                    if (t.substring(0, min(20, t.length)).contains("<!DOCTYPE html>", false)) {
                        document.write(t)
                        document.close()
                    } else {
                        Helper.processAjaxLink(null, t, parent)
                    }

                })
            }
        }
        val f = parent.filters[tableId + parent.blockId]
        filter = f!!
        tableSortableColumns = TableSortableColumn.getSiblingSortableColumn(this)
        tableGroupableColumns = TableGroupableColumn.getSiblingGroupableColumn(this)
        rows = TableRow.getSiblingRows(this)
        paginate = TablePaginate.getSiblingTablePaginate(this)
        tableSelectCheckboxes = TableSelectCheckbox.getSiblingTableSelectCheckbox(this)
        tableMultiSelectButtons = TableMultiSelectButton.getSiblingTableMultiSelectButton(this)
        traceDeIndent("Table::init --- tableId: $tableId")
    }

    override fun getParentBlock(): Block {
        return parent.getParentBlock()
    }
}