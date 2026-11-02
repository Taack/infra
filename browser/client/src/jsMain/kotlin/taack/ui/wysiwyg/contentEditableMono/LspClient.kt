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
 * the server cannot push over plain HTTP. Semantic tokens (full) when the server provides them.
 *
 * @param onSemanticTokens receives the classes of the tokens, see [SemanticToken], and the text they describe
 */
class LspClient(
    private val url: String,
    private val onSemanticTokens: ((List<SemanticToken>, String) -> Unit)? = null,
    private val onDiagnostics: (Array<dynamic>) -> Unit
) {
    /**
     * A semantic token, on one line, from 0. The classes are `tok-<type>` and `tok-<modifier>`.
     */
    class SemanticToken(val line: Int, val start: Int, val length: Int, val classes: String)

    private var tokenTypes: Array<String>? = null
    private var tokenModifiers: Array<String> = emptyArray()
    /** The text the server holds, once the queued messages are sent */
    private var syncedText = ""

    private val uri = "taack:$url"
    private var nextId = 1
    private var version = 0
    private val queue = ArrayDeque<Pair<String, ((dynamic) -> Unit)?>>()
    private var sending = false
    private var changeTimer: Int? = null
    private var pendingText: String? = null

    fun open(text: String) {
        val semanticTokens = json(
            "requests" to json("full" to true),
            "tokenTypes" to emptyArray<String>(),
            "tokenModifiers" to emptyArray<String>(),
            "formats" to arrayOf("relative"),
            "multilineTokenSupport" to false
        )
        request(
            "initialize",
            json("processId" to null, "rootUri" to null, "capabilities" to json("textDocument" to json("semanticTokens" to semanticTokens)))
        ) { result ->
            val legend: dynamic = result?.capabilities?.semanticTokensProvider?.legend
            if (legend != null) {
                tokenTypes = legend.tokenTypes.unsafeCast<Array<String>>()
                tokenModifiers = (legend.tokenModifiers ?: emptyArray<String>()).unsafeCast<Array<String>>()
                // Queued after the didOpen sent meanwhile
                requestSemanticTokens()
            }
        }
        notify("initialized", json())
        notify(
            "textDocument/didOpen",
            json("textDocument" to json("uri" to uri, "languageId" to "asciidoc", "version" to version, "text" to text))
        )
        syncedText = text
        requestDiagnostics()
    }

    fun change(text: String) {
        pendingText = text
        changeTimer?.let { window.clearTimeout(it) }
        changeTimer = window.setTimeout({
            flush()
            requestDiagnostics()
            requestSemanticTokens()
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
        syncedText = text
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

    private fun requestSemanticTokens() {
        // Not before initialize is answered, nor when the server has no semantic tokens
        if (onSemanticTokens == null || tokenTypes == null) return
        val text = syncedText
        request("textDocument/semanticTokens/full", json("textDocument" to json("uri" to uri))) { result ->
            val data: dynamic = result?.data
            if (data != null) onSemanticTokens.invoke(decode(data.unsafeCast<Array<Int>>(), tokenTypes!!), text)
        }
    }

    private fun decode(data: Array<Int>, types: Array<String>): List<SemanticToken> {
        val res = mutableListOf<SemanticToken>()
        var line = 0
        var start = 0
        for (i in 0 until data.size / 5) {
            val deltaLine = data[5 * i]
            if (deltaLine > 0) {
                line += deltaLine
                start = 0
            }
            start += data[5 * i + 1]
            var classes = "tok-" + (types.getOrNull(data[5 * i + 3]) ?: "unknown")
            val modifiers = data[5 * i + 4]
            for (b in tokenModifiers.indices) if ((modifiers shr b) and 1 == 1) classes += " tok-" + tokenModifiers[b]
            res.add(SemanticToken(line, start, data[5 * i + 2], classes))
        }
        return res
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
