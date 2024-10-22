package taack.ui.base.leaf

import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.Node
import org.w3c.dom.asList
import taack.ui.base.element.Show

class ShowActionLink(private val parent: Show, private val a: HTMLAnchorElement) : BaseAjaxAction(parent, a) {
    companion object {
        fun getSiblingShowActionLink(p: Show): List<ShowActionLink> {
            val elements: List<Node>?
            elements = p.d.querySelectorAll("a.taackShowAction").asList()
            return elements.map {
                ShowActionLink(p, it as HTMLAnchorElement)
            }
        }
    }
}

