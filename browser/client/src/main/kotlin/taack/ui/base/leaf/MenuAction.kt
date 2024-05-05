package taack.ui.base.leaf

import kotlinx.browser.document
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.Node
import org.w3c.dom.asList
import taack.ui.base.element.Block

class MenuAction(parent: Block, a: HTMLAnchorElement) : BaseAjaxAction(parent, a) {
    companion object {
        fun getAjaxMenu(p: Block): List<MenuAction> {
            val elements: List<Node>?
            elements = document.querySelectorAll("a.taackAjaxLink").asList()
            return elements.map {
                MenuAction(p, it as HTMLAnchorElement)
            }
        }
    }
}