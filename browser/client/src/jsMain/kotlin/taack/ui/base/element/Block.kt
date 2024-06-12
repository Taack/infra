package taack.ui.base.element

import kotlinx.browser.document
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.get
import taack.ui.base.BaseElement
import taack.ui.base.Helper
import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.base.leaf.ActionLink
import taack.ui.base.leaf.AjaxBlockInputTab
import taack.ui.base.leaf.AnchorHref

class Block(val parent: Modal?, val d: HTMLDivElement) :
    BaseElement {
    companion object {
        var href: String? = null
        fun getSiblingBlock(p: Modal?): Block? {
            val div = p?.dModalBody ?: document.querySelector("div[blockId]") as HTMLDivElement? ?: return null
            return Block(p, div)
        }
    }

    val ajaxBlockElements = mutableMapOf<String, AjaxBlock>()
    val modal: Modal
    var tabs: List<AjaxBlockInputTab>
    val blockId: String
    var modalNumber = 0

    init {
        val tmpBlockId= d.attributes.getNamedItem("blockId")?.value
        traceIndent("Block::init +++ ${d.id}, ${tmpBlockId}.")
        if (tmpBlockId != null && tmpBlockId != "") {
            blockId = tmpBlockId
            ActionLink.getActionLink(this)
            AnchorHref.getAnchorHref(this)

        } else {
            blockId = "modal${modalNumber++}"
        }
        tabs = AjaxBlockInputTab.getSiblingBlockInputTab(this)
        val abe = AjaxBlock.getSiblingAjaxBlock(this)
        modal = Modal.buildModal(this)

        AjaxBlock(this, d)
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
        if (d.children[0] != null)
            d.children[0]!!.innerHTML = newContent
        else
            Helper.trace("Block::updateContent no DIV ...")
    }
}
