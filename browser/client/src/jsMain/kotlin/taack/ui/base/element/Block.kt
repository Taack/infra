package taack.ui.base.element

import kotlinx.browser.document
import org.w3c.dom.HTMLDivElement
import taack.ui.base.BaseElement
import taack.ui.base.Helper
import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.base.leaf.AjaxBlockInputTab
import taack.ui.base.leaf.ActionLink
import taack.ui.base.record.RecordState

class Block(val parent: Modal?, val d: HTMLDivElement) :
    BaseElement {
    companion object {
        var href: String? = null
        fun getSiblingBlock(p: Modal?): Block? {
            val div = p?.d3 ?: document.querySelector("div[blockId]") as HTMLDivElement? ?: return null
            return Block(p, div)
        }
    }

    val ajaxBlockElements: Map<String, AjaxBlock>?
    val modal: Modal
    var tabs: List<AjaxBlockInputTab>
    val blockId: String
    var modalNumber = 0

    init {
        traceIndent("Block::init +++ ${d.id}")
        val tmpBlockId= d.attributes.getNamedItem("blockId")?.value
        if (tmpBlockId != null && tmpBlockId != "") {
            blockId = tmpBlockId
        } else {
            blockId = "modal${modalNumber++}"
        }
        tabs = AjaxBlockInputTab.getSiblingBlockInputTab(this)
        val abe = AjaxBlock.getSiblingAjaxBlock(this)
        ajaxBlockElements = abe.map {
            it.blockId to it
        }.toMap()
        modal = Modal.buildModal(this)
        if (parent == null) {
            ActionLink.getAjaxMenu(this)
        }

        if (tmpBlockId != null) {
            val clientBlockState = RecordState.getPreviousClientState()?.get(blockId)
            Helper.trace("clientBlockState = $clientBlockState for ${d.id}")
            if (tabs.isNotEmpty() && tabs.size == clientBlockState?.size) {
                Helper.trace("restoring active tab ...")
                val indexChecked = clientBlockState.indexOf("true")
                if (indexChecked != -1) tabs[indexChecked].i.checked = true
                else Helper.trace("indexChecked == -1 ...")
            } else {
                Helper.trace("tabs.size != clientBlockState.size ...")
                RecordState.clearClientState(blockId)
            }
        } else {
            Helper.trace("Block::init in modal ...")
            AjaxBlock(this, d)
        }

        traceDeIndent("Block::init --- ${d.id}")
    }

    override fun getParentBlock(): Block {
        return this
    }

    override fun toString(): String {
        return "Block{ajaxBlockElements: $ajaxBlockElements, parent: ${parent}}"
    }

    fun refresh() {
        ActionLink.getAjaxMenu(this)
    }

}
