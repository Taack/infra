package taack.ui.base.element

import org.w3c.dom.HTMLFormElement
import org.w3c.dom.Node
import org.w3c.dom.asList
import taack.ui.base.BaseElement
import taack.ui.base.Helper
import taack.ui.base.leaf.FilterActionButton

class Filter(val parent: AjaxBlock, val f: HTMLFormElement):
    BaseElement {
    companion object {
        fun getSiblingFilterBlock(p: AjaxBlock): List<Filter> {
            val elements: List<Node>?
            elements = p.d.querySelectorAll("form[taackfilterid]").asList()
            return elements.map {
                Filter(p, it as HTMLFormElement)
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