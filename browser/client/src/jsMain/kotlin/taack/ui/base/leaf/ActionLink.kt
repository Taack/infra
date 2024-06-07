package taack.ui.base.leaf

import kotlinx.browser.document
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.Node
import org.w3c.dom.asList
import taack.ui.base.element.AjaxBlock
import taack.ui.base.element.Block

class ActionLink(parent: Block, a: HTMLAnchorElement) : BaseAjaxAction(parent, a) {
    companion object {
        fun getActionLink(p: AjaxBlock): List<ActionLink> {
            val elements: List<Node>?
            elements = p.d.querySelectorAll("a[ajaxaction]").asList()
            return elements.map {
                ActionLink(p.parent, it as HTMLAnchorElement)
            }
        }
    }
}