package taack.ui.base.leaf

import js.array.asList
import taack.ui.base.element.AjaxBlock
import taack.ui.base.element.Block
import web.html.HTMLAnchorElement

class AnchorHref(parent: Block, a: HTMLAnchorElement) : BaseAjaxAction(parent, a) {
    companion object {
        fun getAnchorHref(p: AjaxBlock): List<AnchorHref> {
            val elements: List<*> = p.d.querySelectorAll("a.taackMenu[href]").asList()
            return elements.map {
                AnchorHref(p.parent, it as HTMLAnchorElement)
            }
        }
        fun getAnchorHref(p: Block): List<AnchorHref> {
            return (p.d.querySelectorAll("a.taackMenu[href]").asList().map {
                AnchorHref(p, it as HTMLAnchorElement)
            })
        }
    }
}