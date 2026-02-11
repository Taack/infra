package taack.ui.base.element

import js.array.asList
import kotlinx.browser.window
import taack.ui.base.BaseElement
import taack.ui.base.Helper
import web.events.EventHandler
import web.html.HTMLDivElement

class KanbanCard(val parent: KanbanColumn, val d: HTMLDivElement):
    BaseElement {
    companion object {
        fun getSiblingCard(p: KanbanColumn): List<KanbanCard> {
            val elements: List<*> = p.d.querySelectorAll("div.kanban-card").asList()
            return elements.map {
                KanbanCard(p, it as HTMLDivElement)
            }
        }
    }

    val cardId = d.attributes.getNamedItem("cardid")!!.value

    init {
        Helper.traceIndent("KanbanCard::init +++ cardId: $cardId")
        if (cardId != "") {
            d.ondragstart = EventHandler {
                parent.parent.draggedItem = this
                parent.parent.sourceColumn = this.parent
                window.setTimeout({
                    d.style.display = "none"
                }, 0)
            }

            d.ondragend = EventHandler {
                window.setTimeout({
                    d.style.display = "block"
                    parent.parent.draggedItem = null
                    parent.parent.sourceColumn = null
                }, 0)
            }
        }
        Helper.traceDeIndent("KanbanCard::init --- cardId: $cardId")
    }

    override fun getParentBlock(): Block {
        return parent.getParentBlock()
    }
}