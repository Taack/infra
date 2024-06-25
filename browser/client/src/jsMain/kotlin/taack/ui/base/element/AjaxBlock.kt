package taack.ui.base.element

import org.w3c.dom.HTMLDivElement
import org.w3c.dom.Node
import org.w3c.dom.asList
import org.w3c.dom.get
import taack.ui.base.BaseElement
import taack.ui.base.Helper
import taack.ui.base.leaf.ActionLink
import taack.ui.base.leaf.AnchorHref

class AjaxBlock(val parent: Block, val d: HTMLDivElement) :
    BaseElement {
    companion object {
        fun getSiblingAjaxBlock(p: Block): List<AjaxBlock> {
            val divElements: List<Node>?
            divElements = p.d.querySelectorAll("div[ajaxBlockId]").asList()
//            return (divElements + p.d).map {
            return divElements.map {
                AjaxBlock(p, it as HTMLDivElement)
            }
        }
    }
    val ajaxBlockId =  d.attributes.getNamedItem("ajaxBlockId")!!.value
    val blockId = ajaxBlockId ?: parent.blockId
    var filters: Map<String, Filter> = mutableMapOf()
    var tables: Map<String, Table> = mutableMapOf()
    var forms: List<Form> = mutableListOf()
    var shows: List<Show> = mutableListOf()
    private val innerScripts = d.getElementsByTagName("script")

    init {
        Helper.traceIndent("AjaxBlock::init +++ blockId: $blockId")
        refresh()

        parent.ajaxBlockElements.put(blockId, this)

        Helper.traceDeIndent("AjaxBlock::init --- blockId: $blockId")
    }

//    private suspend fun onPoll() {
//        Helper.trace("AjaxBlock::onPoll")
//
//        window.fetch("/progress/drawProgress/$progressId?isAjax=true", RequestInit(method = "GET")).then {
//            if (it.ok) {
//                Helper.trace("AjaxBlock::it.ok")
//                it.text()
//            } else {
//                Helper.trace("AjaxBlock::it.ok N//    private suspend fun onPoll() {
//        Helper.trace("AjaxBlock::onPoll")
//
//        window.fetch("/progress/drawProgress/$progressId?isAjax=true", RequestInit(method = "GET")).then {
//            if (it.ok) {
//                Helper.trace("AjaxBlock::it.ok")
//                it.text()
//            } else {
//                Helper.trace("AjaxBlock::it.ok NOK")
//                Helper.trace(it.statusText)
//                Promise.reject(Throwable())
//            }
//        }.then {
//            Helper.processAjaxLink(it, parent)
//        }.await()
//
//        // window.setTimeout(handler = {}, timeout = 1000)
//    }
//
//    private fun poolDrawProgress(blockId: String) {
//        progressId = blockId.substring(13)
//        Helper.traceIndent("poolDrawProgress::start +++ progressId: $progressId")
//        window.setTimeout(handler = {
//            GlobalScope.launch {
//                onPoll()
//            }
//        }, timeout = 800)
//        Helper.traceDeIndent("poolDrawProgress::start ---")
//    }
//                Helper.trace(it.statusText)
//                Promise.reject(Throwable())
//            }
//        }.then {
//            Helper.processAjaxLink(it, parent)
//        }.await()
//
//        // window.setTimeout(handler = {}, timeout = 1000)
//    }
//
//    private fun poolDrawProgress(blockId: String) {
//        progressId = blockId.substring(13)
//        Helper.traceIndent("poolDrawProgress::start +++ progressId: $progressId")
//        window.setTimeout(handler = {
//            GlobalScope.launch {
//                onPoll()
//            }
//        }, timeout = 800)
//        Helper.traceDeIndent("poolDrawProgress::start ---")
//    }

    fun refresh() {
        Helper.traceIndent("AjaxBlock::refresh +++ blockId: $blockId")
        filters = Filter.getSiblingFilterBlock(this).map { it.filterId + blockId to it }.toMap()
        tables = Table.getSiblingTable(this).map { it.tableId + blockId to it }.toMap()
        forms = Form.getSiblingForm(this)
        shows = Show.getSiblingShow(this)
        for (i in 0 until innerScripts.length) {
            eval(innerScripts.get(i)!!.innerHTML);
        }
        ActionLink.getActionLinks(this)
        AnchorHref.getAnchorHref(this)
        Helper.traceDeIndent("AjaxBlock::refresh --- ")
    }

    fun updateContent(newContent: String) {
        Helper.trace("AjaxBlock::updateContent ... ${d.className}")
        d.innerHTML = newContent
        refresh()
    }
    
    override fun getParentBlock(): Block {
        return parent
    }
}