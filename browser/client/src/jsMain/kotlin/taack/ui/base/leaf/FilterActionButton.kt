package taack.ui.base.leaf

import org.w3c.dom.*
import org.w3c.dom.events.Event
import taack.ui.base.Helper
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.LeafElement
import taack.ui.base.element.Filter

class FilterActionButton(private val parent: Filter, private val b: HTMLButtonElement?) : LeafElement {
    companion object {
        fun getSiblingFilterAction(f: Filter): List<FilterActionButton> {
            val elements: List<Node>?
            elements = f.f.querySelectorAll("button[formaction]").asList()
            return elements.map {
                FilterActionButton(f, it as HTMLButtonElement)
            }
        }
    }

    init {
        trace("FilterActionButton::init ${b?.id}")
        b?.onclick = { e ->
            onClick(e)
        }
    }

    private fun onClick(e: Event) {
        e.preventDefault()
        Helper.filterForm(parent, 0, null, null, b)
    }
}