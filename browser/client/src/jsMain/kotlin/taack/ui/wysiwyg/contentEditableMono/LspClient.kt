package taack.ui.wysiwyg.contentEditableMono

import kotlinx.browser.window
import web.events.EventHandler
import web.http.POST
import web.http.RequestMethod
import web.xhr.XMLHttpRequest
import kotlin.js.JSON
import kotlin.js.json

/**
 * Minimal LSP client over HTTP: one JSON-RPC message per POST.
 *
 * Messages are sent one after the other, so the server always processes a didChange before
 * the request that follows it. Full text synchronization and pull diagnostics only, since
 * the server cannot push over plain HTTP.
 */
class LspClient(
    private val url: String,
    private val onDiagnostics: (Array<dynamic>) -> Unit
) {
    private val uri = "taack:$url"
    private var nextId = 1
    private var version = 0
    private val queue = ArrayDeque<Pair<String, ((dynamic) -> Unit)?>>()
    private var sending = false
    private var changeTimer: Int? = null
    private var pendingText: String? = null

    fun open(text: String) {
        request("initialize", json("processId" to null, "rootUri" to null, "capabilities" to json())) {}
        notify("initialized", json())
        notify(
            "textDocument/didOpen",
            json("textDocument" to json("uri" to uri, "languageId" to "asciidoc", "version" to version, "text" to text))
        )
        requestDiagnostics()
    }

    fun change(text: String) {
        pendingText = text
        changeTimer?.let { window.clearTimeout(it) }
        changeTimer = window.setTimeout({
            flush()
            requestDiagnostics()
        }, 300)
    }

    fun completion(line: Int, character: Int, onItems: (Array<dynamic>) -> Unit) {
        flush()
        request(
            "textDocument/completion",
            json("textDocument" to json("uri" to uri), "position" to json("line" to line, "character" to character))
        ) { result ->
            val items: dynamic = if (result == null) emptyArray<dynamic>() else if (result is Array<*>) result else result.items
            onItems(items.unsafeCast<Array<dynamic>>())
        }
    }

    private fun flush() {
        changeTimer?.let { window.clearTimeout(it) }
        changeTimer = null
        val text = pendingText ?: return
        pendingText = null
        notify(
            "textDocument/didChange",
            json(
                "textDocument" to json("uri" to uri, "version" to ++version),
                "contentChanges" to arrayOf(json("text" to text))
            )
        )
    }

    private fun requestDiagnostics() {
        request("textDocument/diagnostic", json("textDocument" to json("uri" to uri))) { result ->
            val items: dynamic = result?.items
            onDiagnostics(if (items == null) emptyArray() else items.unsafeCast<Array<dynamic>>())
        }
    }

    private fun request(method: String, params: dynamic, onResult: (dynamic) -> Unit) {
        send(JSON.stringify(json("jsonrpc" to "2.0", "id" to nextId++, "method" to method, "params" to params)), onResult)
    }

    private fun notify(method: String, params: dynamic) {
        send(JSON.stringify(json("jsonrpc" to "2.0", "method" to method, "params" to params)), null)
    }

    private fun send(body: String, onResult: ((dynamic) -> Unit)?) {
        queue.addLast(Pair(body, onResult))
        if (!sending) sendNext()
    }

    private fun sendNext() {
        val next = queue.removeFirstOrNull()
        if (next == null) {
            sending = false
            return
        }
        sending = true
        val xhr = XMLHttpRequest()
        xhr.onloadend = EventHandler {
            val onResult = next.second
            if (onResult != null && xhr.status.toInt() == 200) {
                try {
                    val message: dynamic = JSON.parse(xhr.responseText)
                    if (message.error == null) onResult(message.result)
                    else console.warn("LSP error", message.error)
                } catch (e: Throwable) {
                    console.warn("LSP invalid response", e)
                }
            }
            sendNext()
        }
        xhr.open(RequestMethod.POST, url)
        xhr.setRequestHeader("Content-Type", "application/json")
        xhr.send(next.first)
    }
}
