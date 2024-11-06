package taack.ui.base.leaf

import js.array.asList
import taack.ui.base.element.AjaxBlock
import taack.ui.base.element.Block
import web.dom.NodeList
import web.dom.document
import web.html.HTMLAnchorElement

class ActionLink(parent: Block, a: HTMLAnchorElement) : BaseAjaxAction(parent, a) {
    companion object {
        fun getActionLinks(p: AjaxBlock): List<ActionLink>? {
            val elements: List<*> = p.d.querySelectorAll("a[ajaxaction]").asList()
            return elements.map {
                ActionLink(p.parent, it as HTMLAnchorElement)
            }
        }

        fun getActionLinks(p: Block): List<ActionLink> {
            val elements: List<*> = document.querySelectorAll("body>nav a[ajaxaction]").asList()
            return (elements + p.d.querySelectorAll("div[blockId]>nav a[ajaxaction]").asList()).map {
                ActionLink(p, it as HTMLAnchorElement)
            }
        }
    }
}