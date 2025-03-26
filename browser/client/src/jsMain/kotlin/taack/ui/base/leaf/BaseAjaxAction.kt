package taack.ui.base.leaf

import taack.ui.base.BaseElement
import taack.ui.base.Helper.Companion.processAjaxLink
import taack.ui.base.Helper.Companion.saveOrOpenBlob
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.LeafElement
import web.blob.Blob
import web.dom.document
import web.events.EventHandler
import web.history.history
import web.html.HTMLElement
import web.http.RequestMethod
import web.location.location
import web.uievents.MouseButton
import web.uievents.MouseEvent
import web.url.URL
import web.window.WindowTarget
import web.window.window
import web.xhr.XMLHttpRequest
import web.xhr.XMLHttpRequestResponseType
import kotlin.math.min

open class BaseAjaxAction(private val parent: BaseElement?, a: HTMLElement) : LeafElement {

    companion object {
        fun createUrl(isAjax: Boolean, action: String?, additionalParams: Map<String, String>? = null): URL {
            if (action != null) {
                val url = URL(action, "${location.protocol}//${location.host}")
                if (isAjax) url.searchParams.set("isAjax", "true")
                additionalParams?.forEach {
                    url.searchParams.set(it.key, it.value)
                }
                return url
            } else return URL("${location.protocol}//${location.host}")
        }
    }

    private val action: String? =
        a.attributes.getNamedItem("ajaxAction")?.value ?: a.attributes.getNamedItem("href")?.value
    private val isHref = a.hasAttribute("href")

    init {
        trace("BaseAjaxAction::init $action $isHref")
        if (!(action != null && action.contains("#"))) {
            a.onclick = EventHandler { e -> onclickBaseAjaxAction(e) }
            a.onauxclick = EventHandler { e ->
                if (e.button == MouseButton.AUXILIARY && action?.contains("isAjax=true") == false) {
                    e.preventDefault()
                    val targetUrl = createUrl(!isHref, action).toString()
                    window.open(targetUrl, WindowTarget._blank)
                }
            }
        }
        else trace("BaseAjaxAction::init no onClick added")
    }

    private fun onclickBaseAjaxAction(e: MouseEvent) {
        e.preventDefault()
        val targetUrl = createUrl(!isHref, action).toString()
        trace("BaseAjaxAction::onclickBaseAjaxAction")
        //Display load spinner
        val loader = document.getElementById("taack-load-spinner")
        loader?.classList?.remove("tck-hidden")
        val xhr = XMLHttpRequest()
        if (action?.contains("downloadBin") == true) {
            trace("Binary Action ... $action")
            xhr.responseType = XMLHttpRequestResponseType.blob
        }

        xhr.onloadend = EventHandler { ev ->
            ev.preventDefault()
            trace("BaseAjaxAction::onclickBaseAjaxAction: Load End, action: $action responseType: '${xhr.responseType}'")
            loader?.classList?.add("tck-hidden")
            if (xhr.responseType == XMLHttpRequestResponseType.blob) {
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
                if (text.substring(0, min(20, text.length)).contains(Regex(" html"))) {
                    trace("Full webpage ...|$action|${document.title}|${document.documentURI}")
                    history.pushState("{}", document.title, targetUrl)
                    trace("Setting location.href: $targetUrl")
                    location.href = targetUrl
                    document.write(text)
                    document.close()
                } else {
                    trace("BaseAjaxAction::onclickBaseAjaxAction => processAjaxLink $parent")
                    processAjaxLink(text, parent)
                }
            }
        }

        if (!action.isNullOrEmpty()) {
//            xhr.open("GET", createUrl(!isHref, action).toString())
            xhr.open(RequestMethod.GET, targetUrl)
            xhr.send()
        }
    }
}
