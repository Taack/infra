package taack.ui.base.element

import org.w3c.dom.HTMLTableElement
import org.w3c.dom.Node
import org.w3c.dom.asList
import taack.ui.base.BaseElement
import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.base.leaf.TableGroupableColumn
import taack.ui.base.leaf.TableSortableColumn
import taack.ui.base.leaf.TablePaginate

class Table(val parent: AjaxBlock, val t: HTMLTableElement) :
    BaseElement {
    companion object {
        fun getSiblingTable(p: AjaxBlock): List<Table> {
            val elements: List<Node>?
            elements = p.d.querySelectorAll("table.taackTable").asList()
            return elements.map {
                Table(p, it as HTMLTableElement)
            }
        }
    }

    private val tableSortableColumns: List<TableSortableColumn>?
    private val tableGroupableColumns: List<TableGroupableColumn>?
    val rows: List<TableRow>
    val tableId = t.attributes.getNamedItem("taackTableId")!!.value
    val filter: Filter
    private val paginate: TablePaginate?

    init {
        traceIndent("Table::init +++ tableId: $tableId")
        val f = parent.filters[tableId]
        filter = f!!
        tableSortableColumns = TableSortableColumn.getSiblingSortableColumn(this)
        tableGroupableColumns = TableGroupableColumn.getSiblingGroupableColumn(this)
        if (tableGroupableColumns != null && tableGroupableColumns.isNotEmpty()) {
            t.classList.remove("pure-table-striped")
            t.classList.add("pure-table-bordered")
        }
        rows = TableRow.getSiblingRows(this)
        paginate = TablePaginate.getSiblingTablePaginate(this)
        traceDeIndent("Table::init --- tableId: $tableId")
    }

    override fun getParentBlock(): Block {
        return parent.getParentBlock()
    }
}