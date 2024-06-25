package taack.ui.base.element

import org.w3c.dom.HTMLDivElement
import taack.ui.base.BaseElement
import taack.ui.base.Helper

class PollBlock(val parent: Block) :
    BaseElement {
    companion object {
        private const val POLL_START = "__poll__:"
        // "__poll__:$millis:$url:" + children*.output.join("\n")

        private val pattern = Regex("$POLL_START([0-9]+):(.*)")

        fun isPoll(text: String): Boolean {
            return pattern.containsMatchIn(text)
        }

        fun buildPoll(p: Block): PollBlock {
            return PollBlock(p)
        }
    }

    init {
        Helper.traceIndent("PoolBlock::init +++ blockId: ${parent.blockId}")
        Helper.traceDeIndent("PoolBlock::init --- blockId: ${parent.blockId}")
    }

    override fun getParentBlock(): Block {
        return parent
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

}