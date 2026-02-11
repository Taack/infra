package taack.ui.base.element

import js.array.asList
import taack.ui.base.BaseElement
import taack.ui.base.Helper
import taack.ui.base.Helper.Companion.checkLogin
import web.cssom.ClassName
import web.events.EventHandler
import web.form.FormData
import web.html.HTMLDivElement
import web.http.RequestMethod
import web.xhr.XMLHttpRequest
import web.http.POST

class KanbanColumn(val parent: Kanban, val d: HTMLDivElement):
    BaseElement {
    companion object {
        fun getSiblingKanbanColumn(p: Kanban): List<KanbanColumn> {
            val elements: List<*> = p.d.querySelectorAll("div.kanban-column").asList()
            return elements.map {
                KanbanColumn(p, it as HTMLDivElement)
            }
        }
    }

    private var kanbanCards: List<KanbanCard>?
    private val dropAction = d.attributes.getNamedItem("taackDropAction")!!.value

    init {
        Helper.traceIndent("KanbanColumn::init +++")
        kanbanCards = KanbanCard.getSiblingCard(this)
        if (dropAction != "") {
            d.ondragover = EventHandler { e ->
                e.preventDefault()
                if (parent.sourceColumn != null && parent.sourceColumn != this) {
                    d.classList.add(ClassName("drag-over"))
                }
            }
            d.ondragleave = EventHandler {
                d.classList.remove(ClassName("drag-over"))
            }
            d.ondrop = EventHandler { e ->
                e.preventDefault()
                if (parent.sourceColumn != null && parent.sourceColumn != this) {
                    d.classList.remove(ClassName("drag-over"))
                    d.classList.add(ClassName("kanban-loading"))
                    val fd = FormData()
                    fd.set("cardId", parent.draggedItem?.cardId ?: "")
                    val xhr = XMLHttpRequest()
                    xhr.onreadystatechange = EventHandler {
                        if (xhr.readyState == xhr.DONE) {
                            checkLogin(xhr)
                            d.classList.remove(ClassName("kanban-loading"))
                            Helper.filterForm(parent.filter, null, null)
                        }
                    }
                    xhr.open(RequestMethod.POST, dropAction)
                    xhr.send(fd)
                }
            }
        }
        Helper.traceDeIndent("KanbanColumn::init ---")
    }

    override fun getParentBlock(): Block {
        return parent.getParentBlock()
    }
}