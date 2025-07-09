package taack.ui.base.element

import js.array.asList
import taack.ui.base.BaseElement
import taack.ui.base.Helper
import taack.ui.base.leaf.ActionLink
import taack.ui.base.leaf.AnchorHref
import taack.ui.base.leaf.ContextualLink
import taack.ui.diagram.Diagram
import web.events.EventHandler
import web.html.HTMLDivElement
import web.http.GET
import web.http.RequestMethod
import web.timers.TimerHandler
import web.timers.setTimeout
import web.xhr.XMLHttpRequest
import kotlin.time.Duration

class AjaxBlock(val parent: Block, val d: HTMLDivElement) :
    BaseElement {
    companion object {
        fun getSiblingAjaxBlock(p: Block): List<AjaxBlock> {
            val divElements: List<*> = p.d.querySelectorAll("div[ajaxBlockId]").asList()
            return divElements.map {
                AjaxBlock(p, it as HTMLDivElement)
            }
        }
    }

    private val ajaxBlockId = d.attributes.getNamedItem("ajaxBlockId")!!.value
    val blockId = ajaxBlockId
    var filters: Map<String, Filter> = mutableMapOf()
    private var tables: Map<String, Table> = mutableMapOf()
    private var forms: List<Form> = mutableListOf()
    private var shows: List<Show> = mutableListOf()
    private var diagrams: List<Diagram> = mutableListOf()
    private var progressId: String = ""
    private val onPoll: TimerHandler = { onPoll() }

    private val innerScripts = d.getElementsByTagName("script")

    init {
        Helper.traceIndent("AjaxBlock::init +++ blockId: $blockId")
        refresh()
        parent.ajaxBlockElements[blockId] = this
        Helper.traceDeIndent("AjaxBlock::init --- blockId: $blockId")
    }


    private fun onPoll() {
        Helper.trace("AjaxBlock::onPoll")
        val xhr = XMLHttpRequest()
        xhr.onloadend = EventHandler {
            Helper.processAjaxLink(null, xhr.responseText, parent)
        }
        xhr.open(RequestMethod.GET,"/progress/drawProgress/$progressId?isAjax=true&refresh=true", true)
        xhr.send()
    }

    private fun poolDrawProgress(blockId: String) {
        progressId = blockId.substring(13)
        Helper.traceIndent("poolDrawProgress::start +++ progressId: $progressId")
        setTimeout(Duration.parse("1s"), onPoll)
        Helper.traceDeIndent("poolDrawProgress::start ---")
    }

    fun refresh() {
        Helper.traceIndent("AjaxBlock::refresh +++ blockId: $blockId")
        if (blockId.startsWith("drawProgress=")) {
            poolDrawProgress(blockId)
        }
        filters = Filter.getSiblingFilterBlock(this).associateBy { it.filterId + blockId }
        tables = Table.getSiblingTable(this).associateBy { it.tableId + blockId }
        forms = Form.getSiblingForm(this)
        shows = Show.getSiblingShow(this)
        diagrams = Diagram.getSiblingDiagram(this)
        for (i in 0 until innerScripts.length) {
            eval(innerScripts[i].innerHTML)
        }
        ActionLink.getActionLinks(this)
        AnchorHref.getAnchorHref(this)
        ContextualLink.getContextualLinks(this)
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