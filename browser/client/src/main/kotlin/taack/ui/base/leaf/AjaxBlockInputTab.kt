package taack.ui.base.leaf

import kotlinx.browser.document
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLLabelElement
import org.w3c.dom.Node
import org.w3c.dom.asList
import taack.ui.base.BaseElement
import taack.ui.base.Helper
import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.base.element.Block
import taack.ui.base.record.RecordState

class AjaxBlockInputTab(val parent: Block, val i: HTMLInputElement) :
    BaseElement {
    companion object {
        fun getSiblingBlockInputTab(p: Block): List<AjaxBlockInputTab> {
            val elements: List<Node>?
            elements = p.d.querySelectorAll("input.taackBlockInputTab").asList()
            return elements.map {
                AjaxBlockInputTab(p, it as HTMLInputElement)
            }
        }
    }

    private val state: RecordState = RecordState()
    private val label: HTMLLabelElement

    init {
        traceIndent("AjaxBlockInputTab::init +++ id: ${i.id}")
        label = document.querySelector("label[for=${i.id}]") as HTMLLabelElement
        label.onclick = { e ->
            Helper.trace("AjaxBlockInputTab::onClick")
            val checked = parent.tabs.map {
                (it.i == i).toString()
            }
            state.addClientStateBlock(checked)
        }
        traceDeIndent("AjaxBlockInputTab::init --- id: ${i.id}")
    }
}