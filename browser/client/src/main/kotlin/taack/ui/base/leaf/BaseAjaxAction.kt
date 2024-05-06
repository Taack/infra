package taack.ui.base.leaf

import kotlinx.browser.window
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import org.w3c.xhr.BLOB
import org.w3c.xhr.XMLHttpRequest
import org.w3c.xhr.XMLHttpRequestResponseType
import taack.ui.base.BaseElement
import taack.ui.base.Helper.Companion.processAjaxLink
import taack.ui.base.Helper.Companion.saveOrOpenBlob
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.LeafElement
import taack.ui.base.record.RecordState

open class BaseAjaxAction(private val parent: BaseElement, a: HTMLAnchorElement) : LeafElement {

    companion object {
        fun createUrl(action: String, additionalParams: Map<String, String>? = null): URL {
            val url = URL(action, "${window.location.protocol}//${window.location.host}")
            url.searchParams.set("isAjax", "true")
            additionalParams?.forEach {
                url.searchParams.set(it.key, it.value)
            }
            if (!url.searchParams.has("recordState")) url.searchParams.set("recordState", RecordState.dumpServerState())
            return url
        }
    }


    init {
        trace("BaseAjaxAction::init")
        a.onclick = { e -> onclickBaseAjaxAction(e) }
    }

    private val action: String? = a.attributes.getNamedItem("ajaxAction")?.value

    private fun onclickBaseAjaxAction(e: MouseEvent) {
        e.preventDefault()
        trace("BaseAjaxAction::onclickBaseAjaxAction")
        val xhr = XMLHttpRequest()
        if (action?.contains("downloadBin") == true) {
            trace("Binary Action ... $action")
            xhr.responseType = XMLHttpRequestResponseType.BLOB
        }
        xhr.onloadend = { ev: Event ->
            ev.preventDefault()
            trace("BaseAjaxAction::onclickBaseAjaxAction: Load End, action: $action responseType: '${xhr.responseType}'")
            if (xhr.responseType == XMLHttpRequestResponseType.BLOB) {
                val contentDispo = xhr.getResponseHeader("Content-Disposition")
                if (contentDispo != null) {
                    val fileName =
                        Regex("filename[^;=\n]*=((['\"]).*?\\2|[^;\n]*)").find(contentDispo)?.groupValues?.get(1)
                    if (fileName != null) {
                        trace("saveOrOpenBlog $fileName")
                        saveOrOpenBlob(xhr.response as Blob, fileName)
                    }
                }
            } else {
                val text = xhr.responseText
                if (text.contains(Regex(".{0,4}<html"))) {
                    trace("AUO response identified like full page ...")
                    window.document.write(text)
                    window.history.pushState("", "Intranet ", action)
                } else {
                    processAjaxLink(text, parent)
                }
            }
        }
        if (action != null) {
            xhr.open("GET", createUrl(action).toString())
            xhr.send()
        }
    }
}
