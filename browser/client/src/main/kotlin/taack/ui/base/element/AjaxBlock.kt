package taack.ui.base.element

import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import org.w3c.dom.*
import org.w3c.fetch.RequestInit
import taack.ui.base.BaseElement
import taack.ui.base.Helper
import taack.ui.base.record.RecordState
import kotlin.js.Promise

class AjaxBlock(val parent: Block, val d: HTMLDivElement) :
    BaseElement {
    companion object {
        fun getSiblingAjaxBlock(p: Block): List<AjaxBlock> {
            val divElements: List<Node>?
            divElements = p.d.querySelectorAll("div.taackAjaxBlock").asList()
            return divElements.map {
                AjaxBlock(p, it as HTMLDivElement)
            }
        }
    }

    val blockId = d.attributes.getNamedItem("ajaxBlockId")!!.value
    var filters: Map<String, Filter>
    var tables: Map<String, Table>
    var forms: List<Form>
    var shows: List<Show>
    var progressId: String = ""
    private val innerScripts = d.getElementsByTagName("script")

    init {
        Helper.traceIndent("AjaxBlock::init +++ blockId: $blockId")
        RecordState.setCurrentBlockId(blockId)
        filters = Filter.getSiblingFilterBlock(this).map { it.filterId to it }.toMap()
        tables = Table.getSiblingTable(this).map { it.tableId to it }.toMap()
        forms = Form.getSiblingForm(this)
        shows = Show.getSiblingShow(this)
        Helper.trace("innerScripts ${innerScripts.length}")
        //Ugly as Hell
        for (i in 0 until innerScripts.length) {
            val s = innerScripts.get(i) as HTMLScriptElement
            if (s.type == "module") {
                console.log("AUO detecting a module")
                document.body!!.appendChild(s)

//                val code: Any = s.innerHTML
//                js("const dataUri = 'data:text/javascript;charset=utf-8,' + encodeURIComponent(code);const module = await import(dataUri);console.log(module);const myHello = module.default;myHello();")
            } else {
                if (!s.hasAttribute("postExecute")) continue

                if (s.hasAttribute("src")) {
                    console.log("AUO Eval src: ${(innerScripts.get(i)!! as HTMLScriptElement).src}")
                    val script = document.createElement("script") as HTMLScriptElement
                    script.src = (innerScripts.get(i)!! as HTMLScriptElement).src
                    document.head?.appendChild(script)

                } else {
                    console.log("AUO Eval: ${innerScripts.get(i)!!.innerHTML}")
                    eval("(function() {" + innerScripts.get(i)!!.innerHTML + "})()")
//                    document.body?.appendChild(innerScripts.get(i)!!)
                }
            }
        }
        if (blockId.startsWith("drawProgress:")) {
            poolDrawProgress(blockId)
        }
        val clientState = RecordState.getPreviousClientState()?.get(blockId)
        Helper.trace("clientState = $clientState")
        if (clientState != null) {
            window.scrollTo(clientState[0]?.toDouble() ?: 0.0, clientState[1]?.toDouble() ?: 0.0)
            RecordState.clearClientState(blockId)
        }

        Helper.traceDeIndent("AjaxBlock::init --- blockId: $blockId")
    }

    private suspend fun onPoll() {
        Helper.trace("AjaxBlock::onPoll")

        window.fetch("/progress/drawProgress/$progressId?isAjax=true", RequestInit(method = "GET")).then {
            if (it.ok) {
                Helper.trace("AjaxBlock::it.ok")
                it.text()
            } else {
                Helper.trace("AjaxBlock::it.ok NOK")
                Helper.trace(it.statusText)
                Promise.reject(Throwable())
            }
        }.then {
            Helper.processAjaxLink(it, parent)
        }.await()

        // window.setTimeout(handler = {}, timeout = 1000)
    }

    private fun poolDrawProgress(blockId: String) {
        progressId = blockId.substring(13)
        Helper.traceIndent("poolDrawProgress::start +++")
        window.setTimeout(handler = {
            GlobalScope.launch {
                onPoll()
            }
        }, timeout = 1500)
        Helper.traceDeIndent("poolDrawProgress::start ---")
    }

    fun refresh() {
        Helper.traceIndent("AjaxBlock::refresh +++ blockId: $blockId")
        filters = Filter.getSiblingFilterBlock(this).map { it.filterId to it }.toMap()
        tables = Table.getSiblingTable(this).map { it.tableId to it }.toMap()
        forms = Form.getSiblingForm(this)
        for (i in 0 until innerScripts.length) {
            eval(innerScripts.get(i)!!.innerHTML);
        }
        parent.refresh()
        Helper.traceDeIndent("AjaxBlock::refresh --- blockId: $blockId")
    }

    override fun getParentBlock(): Block {
        return parent
    }
}