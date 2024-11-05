package taack.ui.base.leaf

import taack.ui.base.Helper
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.LeafElement
import taack.ui.base.element.Filter
import web.events.Event
import web.events.EventHandler
import web.html.HTMLButtonElement

class FilterActionButton(private val parent: Filter, private val b: HTMLButtonElement?) : LeafElement {
    companion object {
        fun getSiblingFilterAction(f: Filter): List<FilterActionButton> {
            val elements: List<HTMLButtonElement>?
            elements = f.f.querySelectorAll("button[formaction]") as List<HTMLButtonElement>?
            return elements!!.map {
                FilterActionButton(f, it as HTMLButtonElement)
            }
        }
    }

    init {
        trace("FilterActionButton::init ${b?.id}")
        b?.onclick = EventHandler { e ->
            onClick(e)
        }
    }

    private fun onClick(e: Event) {
        e.preventDefault()
        Helper.filterForm(parent, 0, null, null, b)
    }
}