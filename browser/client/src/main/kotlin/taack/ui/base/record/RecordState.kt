package taack.ui.base.record

import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.w3c.xhr.FormData
import taack.ui.base.Helper.Companion.trace

external fun decodeURIComponent(encodedURI: String): String
external  fun encodeURIComponent(encodedURI: String): String

class RecordState() {
    companion object {
        private var currentBlockId: String = "null"
        private var currentBlockDivId: String = "null"
        val serverState: MutableMap<String, MutableMap<String, String>> = mutableMapOf()
        var clientState: MutableMap<String, List<String?>> = mutableMapOf()

        fun restoreClientState() {
            trace("RecordState::restoreClientState ${document.cookie}")
            val previousState = getPreviousClientState()
            if (previousState != null) {
                trace("RecordState::restoreClientState previousState=${previousState.entries}")
                clientState.putAll(previousState)
            }
        }

        fun restoreServerState(recordStateString: String) {
            trace("RecordState::restoreServerState recordStateString=$recordStateString")
            if (recordStateString.isNotEmpty()) {

                serverState.clear()
                try {
                    val recordData: String? = try {
                        decodeURIComponent(window.atob(recordStateString))
                    } catch (e: Throwable) {
                        trace("RecordState::restoreServerState catch1 ${e.message}")
                        null
                    }
                    if (!recordData.isNullOrEmpty()) {
                        trace("RecordState::restoreServerState recordData=$recordData")
                        serverState.putAll(Json.decodeFromString(recordData))
                    }
                } catch (e: Exception) {
                    trace("RecordState::restoreServerState catch2 ${e.message}")
                }
            }
        }

        fun dumpServerState(): String {
            val clientJson = Json.encodeToString(clientState)
            trace("RecordState::dumpServerState ${document.cookie} => $clientJson")
            document.cookie = "clientState=${clientJson}; path=/; max-age=400"
            val toSend = Json.encodeToString(serverState)
            return window.btoa(encodeURIComponent(toSend))
        }

        fun clearServerState() {
            serverState.clear()
        }

        fun clearClientState(blockId: String) {
            clientState.remove(blockId)
            document.cookie = "clientState=${Json.encodeToString(clientState)}; path=/; max-age=400"
        }

        fun setCurrentBlockId(blockId: String) {
            trace("RecordState::setCurrentBlock $blockId")
            currentBlockId = blockId
        }

        fun setCurrentBlockDivId(blockId: String) {
            trace("RecordState::setCurrentBlockDivId $blockId")
            currentBlockDivId = blockId
        }

        fun getPreviousClientState(): Map<String, List<String?>>? {
//            val decodedCookie = decodeURIComponent(document.cookie)
            val c = document.cookie.trim()
            if (c.startsWith("clientState=")) {
                return Json.decodeFromString(c.substring("clientState=".length))
            }
            return null

        }
    }

    private val blockId = currentBlockId
    private val blockDivId = currentBlockDivId

    private fun addServerState(key: String, value: String) {
        trace("RecordState::addServerState key=$key, value=$value")
        if (!serverState.containsKey(blockId)) {
            serverState[blockId] = mutableMapOf()
        }
        serverState[blockId]!![key] = value
    }

    fun addServerState(form: FormData) {
        val it = form.asDynamic().keys()
        var nit = it.next()
        while (nit.done == false) {
            val v = form.get(nit.value as String) as String
            if (v.isNotEmpty()) {
                addServerState(nit.value as String, v)
            }
            nit = it.next()
        }
        trace("RecordState::addServerState $serverState")
    }

    fun addClientStateAjaxBlock() {
        val d = document.querySelector("div[ajaxblockid=$blockId]")?.closest("div.taackModal")
        clientState[blockId] = listOf(
            window.scrollX.toString(),
            window.scrollY.toString(),
            d?.scrollTop?.toString(),
            d?.scrollLeft?.toString()
        )
        trace("RecordState::addClientStateAjaxBlock $clientState block=$blockId")
    }

    fun addClientStateBlock(stats: List<String>) {
        clientState[blockDivId] = stats
        trace("RecordState::addClientStateBlock $blockDivId $stats")
    }
}