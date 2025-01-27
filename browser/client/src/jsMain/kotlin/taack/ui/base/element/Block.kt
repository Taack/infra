package taack.ui.base.element

import taack.ui.base.BaseElement
import taack.ui.base.Helper
import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.base.leaf.ActionLink
import taack.ui.base.leaf.AjaxBlockInputTab
import taack.ui.base.leaf.AnchorHref
import taack.ui.base.leaf.TabButton
import web.dom.document
import web.html.HTMLDivElement

class Block(val parent: Modal?, val d: HTMLDivElement) :
    BaseElement {
    companion object {
        var href: String? = null
        fun getSiblingBlock(p: Modal?): Block? {
            val div = p?.dModalBody ?: document.querySelector("div[blockId]") as HTMLDivElement?
            return if (div != null) Block(p, div) else null
        }
    }

    val ajaxBlockElements = mutableMapOf<String, AjaxBlock>()
    val modal: Modal
    private var tabs: List<AjaxBlockInputTab>
    private var tabButtons: List<TabButton>
    val blockId: String
    private var modalNumber = 0

    init {
        val tmpBlockId = d.attributes.getNamedItem("blockId")?.value
        traceIndent("Block::init +++ ${d.id}, ${tmpBlockId}.")
        if (tmpBlockId != null && tmpBlockId != "") {
            blockId = tmpBlockId
            ActionLink.getActionLinks(this)
            AnchorHref.getAnchorHref(this)

        } else {
            blockId = "modal${modalNumber++}"
        }
        tabs = AjaxBlockInputTab.getSiblingBlockInputTab(this)
        tabButtons = TabButton.getSiblingTabButton(this)
        AjaxBlock.getSiblingAjaxBlock(this)
        modal = Modal.buildModal(this)

//        AjaxBlock(this, d)
        traceDeIndent("Block::init --- ${d.id}")
    }

    override fun getParentBlock(): Block {
        return this
    }

    override fun toString(): String {
        return "Block{ajaxBlockElements: $ajaxBlockElements, parent: ${parent}}"
    }

    fun updateContent(newContent: String) {
        Helper.trace("Block::updateContent ...")
        d.children[0].innerHTML = newContent
        AjaxBlock.getSiblingAjaxBlock(this)
    }
}
