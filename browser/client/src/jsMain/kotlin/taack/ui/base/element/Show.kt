package taack.ui.base.element


import taack.ui.base.BaseElement
import taack.ui.base.Helper
import taack.ui.base.leaf.ShowActionLink
import web.html.HTMLDivElement

class Show(val parent: AjaxBlock, val d: HTMLDivElement):
    BaseElement {
    companion object {
        fun getSiblingShow(p: AjaxBlock): List<Show> {
            val elements: List<HTMLDivElement>?
            elements = p.d.querySelectorAll("div.taackShow") as List<HTMLDivElement>?
            return elements!!.map {
                Show(p, it)
            }
        }
    }

    private val actions: List<ShowActionLink>

    init {
        Helper.traceIndent("Show::init +++")
        actions = ShowActionLink.getSiblingShowActionLink(this)
        Helper.traceDeIndent("Show::init ---")
    }

    override fun getParentBlock(): Block {
        return parent.getParentBlock()
    }
}