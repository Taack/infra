package taack.ui.base.leaf

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLElement
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

open class BaseAjaxAction(private val parent: BaseElement, a: HTMLElement) : LeafElement {

    companion object {
        fun createUrl(isAjax: Boolean, action: String?, additionalParams: Map<String, String>? = null): URL {
            if (action != null) {
                val url = URL(action, "${window.location.protocol}//${window.location.host}")
                if (isAjax) url.searchParams.set("isAjax", "true")
                additionalParams?.forEach {
                    url.searchParams.set(it.key, it.value)
                }
                return url
            } else return URL("${window.location.protocol}//${window.location.host}")
        }
    }

    private val action: String? =
        a.attributes.getNamedItem("ajaxAction")?.value ?: a.attributes.getNamedItem("href")?.value
    private val isHref = a.hasAttribute("href")

    init {
        trace("BaseAjaxAction::init $action $isHref")
        if (!(action != null && action.contains("#")))
            a.onclick = { e -> onclickBaseAjaxAction(e) }
        else trace("BaseAjaxAction::init no onClick added")
    }

    private fun onclickBaseAjaxAction(e: MouseEvent) {
        e.preventDefault()
        val targetUrl = createUrl(!isHref, action).toString()
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
                    trace("Full webpage ...|$action|${document.title}|${document.domain}|${document.documentURI}")
                    window.history.pushState("{}", window.document.title, targetUrl)
                    trace("Setting location.href: $targetUrl")
                    window.location.href = targetUrl
                    window.document.clear()
                    window.document.write(text)
                    window.document.close()
                } else {
                    trace("BaseAjaxAction::onclickBaseAjaxAction => processAjaxLink $parent")
                    processAjaxLink(text, parent)
                }
            }
        }

        if (!action.isNullOrEmpty()) {
//            xhr.open("GET", createUrl(!isHref, action).toString())
            xhr.open("GET", targetUrl)
            xhr.send()
        }
    }
}
