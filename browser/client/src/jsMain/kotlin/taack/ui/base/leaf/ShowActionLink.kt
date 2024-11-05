package taack.ui.base.leaf

import taack.ui.base.element.Show
import web.html.HTMLAnchorElement

class ShowActionLink(parent: Show, a: HTMLAnchorElement) : BaseAjaxAction(parent, a) {
    companion object {
        fun getSiblingShowActionLink(p: Show): List<ShowActionLink> {
            val elements: List<HTMLAnchorElement>?
            elements = p.d.querySelectorAll("a.taackShowAction") as List<HTMLAnchorElement>
            return elements.map {
                ShowActionLink(p, it)
            }
        }
    }
}

