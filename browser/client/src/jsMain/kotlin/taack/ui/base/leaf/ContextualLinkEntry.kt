package taack.ui.base.leaf

import js.array.asList
import taack.ui.base.element.Block
import web.dom.document
import web.html.HTMLAnchorElement

class ContextualLinkEntry(a: HTMLAnchorElement) : BaseAjaxAction(null, a) {
    companion object {

        fun getDropdownMenu(parent: Block): List<ContextualLinkEntry> {
            return (document.querySelectorAll("a.dropdown-item[href]").asList().map {
                ContextualLinkEntry(it as HTMLAnchorElement)
            })
        }
    }
}