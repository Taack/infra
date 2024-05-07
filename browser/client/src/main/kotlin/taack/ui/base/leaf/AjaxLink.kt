package taack.ui.base.leaf

import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.Node
import org.w3c.dom.asList
import taack.ui.base.element.AjaxBlock

class AjaxLink(parent: AjaxBlock, a: HTMLAnchorElement) : BaseAjaxAction(parent, a) {
    companion object {
        fun getSiblingAjaxLink(p: AjaxBlock): List<AjaxLink> {
            val elements: List<Node>?
            elements = p.d.querySelectorAll("a.taackAjaxLink").asList()
            return elements.map {
                AjaxLink(p, it as HTMLAnchorElement)
            }
        }
    }
}