package taack.ui.base.leaf

import taack.ui.base.BaseElement
import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.base.element.Block
import web.dom.document
import web.events.EventHandler
import web.html.HTMLInputElement
import web.html.HTMLLabelElement

class AjaxBlockInputTab(val parent: Block, val i: HTMLInputElement) :
    BaseElement {
    companion object {
        fun getSiblingBlockInputTab(p: Block): List<AjaxBlockInputTab> {
            val elements: List<HTMLInputElement>? = p.d.querySelectorAll("input.taackBlockInputTab") as List<HTMLInputElement>?
            return elements!!.map {
                AjaxBlockInputTab(p, it)
            }
        }
    }

    private val label: HTMLLabelElement

    init {
        traceIndent("AjaxBlockInputTab::init +++ id: ${i.id}")
        label = document.querySelector("label[for=${i.id}]") as HTMLLabelElement
        label.onclick = EventHandler {
        }
        traceDeIndent("AjaxBlockInputTab::init --- id: ${i.id}")
    }

    override fun getParentBlock(): Block {
        return parent
    }
}