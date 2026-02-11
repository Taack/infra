package taack.ui.base.element

import js.array.asList
import taack.ui.base.BaseElement
import taack.ui.base.Helper
import web.html.HTMLDivElement

class Kanban(val parent: AjaxBlock, val d: HTMLDivElement):
    BaseElement {
    companion object {
        fun getSiblingKanban(p: AjaxBlock): List<Kanban> {
            val elements: List<*> = p.d.querySelectorAll("div[taackkanbanid]").asList()
            return elements.map {
                Kanban(p, it as HTMLDivElement)
            }
        }
    }

    private val kanbanColumns: List<KanbanColumn>?
    val kanbanId = d.attributes.getNamedItem("taackKanbanid")!!.value
    val filter: Filter
    var draggedItem: KanbanCard? = null
    var sourceColumn: KanbanColumn? = null

    init {
        Helper.traceIndent("Kanban::init +++ kanbanId: $kanbanId")
        kanbanColumns = KanbanColumn.getSiblingKanbanColumn(this)
        filter = parent.filters[kanbanId + parent.blockId]!!
        Helper.traceDeIndent("Kanban::init --- kanbanId: $kanbanId")
    }

    override fun getParentBlock(): Block {
        return parent.getParentBlock()
    }
}