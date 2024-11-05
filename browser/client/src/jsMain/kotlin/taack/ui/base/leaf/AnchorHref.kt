package taack.ui.base.leaf

import taack.ui.base.element.AjaxBlock
import taack.ui.base.element.Block
import web.html.HTMLAnchorElement

class AnchorHref(parent: Block, a: HTMLAnchorElement) : BaseAjaxAction(parent, a) {
    companion object {
        fun getAnchorHref(p: AjaxBlock): List<AnchorHref> {
            val elements: List<HTMLAnchorElement>?
            elements = p.d.querySelectorAll("a.taackMenu[href]") as List<HTMLAnchorElement>?
            return elements!!.map {
                AnchorHref(p.parent, it as HTMLAnchorElement)
            }
        }
        fun getAnchorHref(p: Block): List<AnchorHref> {
            return (p.d.querySelectorAll("a.taackMenu[href]") as List<HTMLAnchorElement>?)!!.map {
                AnchorHref(p, it as HTMLAnchorElement)
            }
        }
    }
}