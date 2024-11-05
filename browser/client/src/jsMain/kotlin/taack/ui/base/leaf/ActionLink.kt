package taack.ui.base.leaf

import taack.ui.base.element.AjaxBlock
import taack.ui.base.element.Block
import web.dom.document
import web.html.HTMLAnchorElement

class ActionLink(parent: Block, a: HTMLAnchorElement) : BaseAjaxAction(parent, a) {
    companion object {
        fun getActionLinks(p: AjaxBlock): List<ActionLink> {
            val elements: List<HTMLAnchorElement>?
            elements = p.d.querySelectorAll("a[ajaxaction]") as List<HTMLAnchorElement>
            return elements.map {
                ActionLink(p.parent, it)
            }
        }

        fun getActionLinks(p: Block): List<ActionLink> {
            val elements: List<HTMLAnchorElement>?
            elements = document.querySelectorAll("body>nav a[ajaxaction]") as List<HTMLAnchorElement>
            return (elements + p.d.querySelectorAll("div[blockId]>nav a[ajaxaction]") as List<HTMLAnchorElement>).map {
                ActionLink(p, it as HTMLAnchorElement)
            }
        }
    }
}