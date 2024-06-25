package taack.ui.base.leaf

import kotlinx.browser.document
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.Node
import org.w3c.dom.asList
import taack.ui.base.element.AjaxBlock
import taack.ui.base.element.Block

class AnchorHref(parent: Block, a: HTMLAnchorElement) : BaseAjaxAction(parent, a) {
    companion object {
        fun getAnchorHref(p: AjaxBlock): List<AnchorHref> {
            val elements: List<Node>?
            elements = p.d.querySelectorAll("a.taackMenu[href]").asList()
            return elements.map {
                AnchorHref(p.parent, it as HTMLAnchorElement)
            }
        }
        fun getAnchorHref(p: Block): List<AnchorHref> {
            val elements: List<Node>?
            elements = document.querySelectorAll("body>nav a.taackMenu[href]").asList()
            return (elements + p.d.querySelectorAll("a.taackMenu[href]").asList()).map {
                AnchorHref(p, it as HTMLAnchorElement)
            }
        }
    }
}