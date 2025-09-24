package taack.ui.base.leaf

import taack.ui.base.BaseElement
import taack.ui.base.Helper.Companion.checkLogin
import taack.ui.base.Helper.Companion.processAjaxLink
import taack.ui.base.Helper.Companion.saveOrOpenBlob
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.LeafElement
import web.blob.Blob
import web.cssom.ClassName
import web.dom.ElementId
import web.dom.document
import web.events.EventHandler
import web.history.history
import web.html.HTMLElement
import web.http.GET
import web.http.RequestMethod
import web.location.location
import web.uievents.MouseButton
import web.uievents.MouseEvent
import web.url.URL
import web.xhr.XMLHttpRequest
import web.xhr.XMLHttpRequestResponseType
import web.xhr.blob
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

        var lastUrlClicked: URL? = null
    }

    private val action: String? =
        a.attributes.getNamedItem("ajaxAction")?.value ?: a.attributes.getNamedItem("href")?.value
    private val isHref = !a.hasAttribute("ajaxaction")

    init {
        trace("BaseAjaxAction::init $action $isHref")
        if (!(action != null && action.contains("#"))) {
            a.onclick = EventHandler { e ->
                if (e.button != MouseButton.AUXILIARY && !e.ctrlKey && !e.shiftKey && !e.metaKey) {
                    onclickBaseAjaxAction(e)
                }
            }
        }
        else trace("BaseAjaxAction::init no onClick added")
    }

    private fun onclickBaseAjaxAction(e: MouseEvent) {
        e.preventDefault()
        lastUrlClicked = createUrl(!isHref, action)
        val targetUrl = lastUrlClicked.toString()
        trace("BaseAjaxAction::onclickBaseAjaxAction")

        val xhr = XMLHttpRequest()
        if (action?.contains("downloadBin") == true) {
            trace("Binary Action ... $action")
            xhr.responseType = XMLHttpRequestResponseType.blob
        }
        val loader = document.getElementById(ElementId("taack-load-spinner"))
        xhr.onreadystatechange = EventHandler { ev ->
            if (xhr.readyState == xhr.DONE) {
                trace("BaseAjaxAction::onclickBaseAjaxAction: Load End, action: $action responseType: '${xhr.responseType}' status: '${xhr.status}'")
                checkLogin(xhr)
                if (xhr.status == 200.toShort()) {
                    ev.preventDefault()
                    loader?.classList?.add(ClassName("tck-hidden"))
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
                            trace("Full webpage ...|$action|${document.title}|${document.documentURI}|${xhr.responseURL}")
                            history.pushState("{}", document.title, xhr.responseURL)
                            location.href = xhr.responseURL
                            document.textContent = text
                            document.close()
                        } else {
                            trace("BaseAjaxAction::onclickBaseAjaxAction => processAjaxLink $parent")
                            processAjaxLink(lastUrlClicked, text, parent)
                        }
                    }
                }
            } else if (xhr.readyState == xhr.LOADING) {
                loader?.classList?.remove(ClassName("tck-hidden"))
            }
        }

        if (!action.isNullOrEmpty()) {
//            xhr.open("GET", createUrl(!isHref, action).toString())
            xhr.open(RequestMethod.GET, targetUrl)
            xhr.send()
        }
    }
}
