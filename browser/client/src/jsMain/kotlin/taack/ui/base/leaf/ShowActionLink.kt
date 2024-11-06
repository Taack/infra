package taack.ui.base.leaf

import js.array.asList
import taack.ui.base.element.Show
import web.html.HTMLAnchorElement

class ShowActionLink(parent: Show, a: HTMLAnchorElement) : BaseAjaxAction(parent, a) {
    companion object {
        fun getSiblingShowActionLink(p: Show): List<ShowActionLink> {
            val elements: List<*> = p.d.querySelectorAll("a.taackShowAction").asList()
            return elements.map {
                ShowActionLink(p, it as HTMLAnchorElement)
            }
        }
    }
}

