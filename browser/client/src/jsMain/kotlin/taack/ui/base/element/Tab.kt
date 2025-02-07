package taack.ui.base.element

import js.array.asList
import taack.ui.base.BaseElement
import taack.ui.base.Helper
import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.base.leaf.*
import web.html.HTMLUListElement

class Tab(val parent: Block, val d: HTMLUListElement) :
    BaseElement {
    companion object {
        fun getSiblingTab(p: Block): List<Tab> {
            val elements: List<*> = p.d.querySelectorAll("ul[role='tablist']").asList()
            return elements.map {
                Tab(p, it as HTMLUListElement)
            }
        }

    }

    private val tabs: List<TabButton>?

    init {
        traceIndent("Tab::init +++")
        tabs = TabButton.getSiblingTabButton(this)
        traceDeIndent("Tab::init ---")
    }

    override fun getParentBlock(): Block {
        return this.parent
    }

    override fun toString(): String {
        return "Tab{}"
    }

    fun updateContent(newContent: String) {
        Helper.trace("Block::updateContent ...")
        d.children[0].innerHTML = newContent
        AjaxBlock.getSiblingAjaxBlock(this.parent)
    }
}
