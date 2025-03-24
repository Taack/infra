package taack.ui.base.element

import js.array.asList
import taack.ui.base.BaseElement
import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.base.leaf.TableGroupableColumn
import taack.ui.base.leaf.TableSortableColumn
import taack.ui.base.leaf.TablePaginate
import taack.ui.base.leaf.TableSelectCheckbox
import web.html.HTMLTableElement

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
    val tableSelectCheckboxes: Pair<TableSelectCheckbox, List<TableSelectCheckbox>>?

    init {
        traceIndent("Table::init +++ tableId: $tableId")
        val f = parent.filters[tableId + parent.blockId]
        filter = f!!
        tableSortableColumns = TableSortableColumn.getSiblingSortableColumn(this)
        tableGroupableColumns = TableGroupableColumn.getSiblingGroupableColumn(this)
        rows = TableRow.getSiblingRows(this)
        paginate = TablePaginate.getSiblingTablePaginate(this)
        tableSelectCheckboxes = TableSelectCheckbox.getSiblingTableSelectCheckbox(this)
        traceDeIndent("Table::init --- tableId: $tableId")
    }

    override fun getParentBlock(): Block {
        return parent.getParentBlock()
    }
}