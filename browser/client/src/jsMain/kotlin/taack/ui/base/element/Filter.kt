package taack.ui.base.element

import taack.ui.base.BaseElement
import taack.ui.base.Helper
import taack.ui.base.leaf.FilterActionButton
import web.html.HTMLFormElement

class Filter(val parent: AjaxBlock, val f: HTMLFormElement):
    BaseElement {
    companion object {
        fun getSiblingFilterBlock(p: AjaxBlock): List<Filter> {
            val elements: List<HTMLFormElement>?
            elements = p.d.querySelectorAll("form[taackfilterid]") as List<HTMLFormElement>?
            return elements!!.map {
                Filter(p, it)
            }
        }
    }

    val filterId = f.attributes.getNamedItem("taackFilterId")!!.value
    private val actions: List<FilterActionButton>

    init {
        Helper.traceIndent("Filter::init +++ filterId: $filterId")
        actions = FilterActionButton.getSiblingFilterAction(this)
        Helper.traceDeIndent("Filter::init --- filterId: $filterId")
    }

    override fun getParentBlock(): Block {
        return parent.getParentBlock()
    }
}