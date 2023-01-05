package taack.ui.base.element

import org.w3c.dom.HTMLDivElement
import org.w3c.dom.Node
import org.w3c.dom.asList
import taack.ui.base.BaseElement
import taack.ui.base.Helper
import taack.ui.base.leaf.ShowActionLink
import taack.ui.base.leaf.ShowAjaxInput
import taack.ui.base.leaf.ShowAjaxSelect

class Show(val parent: AjaxBlock, val d: HTMLDivElement):
    BaseElement {
    companion object {
        fun getSiblingShow(p: AjaxBlock): List<Show> {
            val elements: List<Node>?
            elements = p.d.querySelectorAll("div.taackShow").asList()
            return elements.map {
                Show(p, it as HTMLDivElement)
            }
        }
    }

    private val actions: List<ShowActionLink>
    private val inputs: List<ShowAjaxInput>
    private val selects: List<ShowAjaxSelect>

    init {
        Helper.traceIndent("Show::init +++")
        actions = ShowActionLink.getSiblingShowActionLink(this)
        inputs = ShowAjaxInput.getSiblingShowAjaxInput(this)
        selects = ShowAjaxSelect.getSiblingShowAjaxSelect(this)
        Helper.traceDeIndent("Show::init ---")
    }
}