package taack.ui.base.element

import js.array.asList
import kotlinx.browser.window
import taack.ui.base.BaseElement
import taack.ui.base.Helper
import taack.ui.base.Helper.Companion.checkLogin
import taack.ui.base.Helper.Companion.processAjaxLink
import taack.ui.base.Helper.Companion.saveOrOpenBlob
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.leaf.BaseAjaxAction.Companion.createUrl
import web.blob.Blob
import web.cssom.ClassName
import web.dom.ElementId
import web.dom.document
import web.events.EventHandler
import web.history.history
import web.html.HTMLSpanElement
import web.http.GET
import web.http.RequestMethod
import web.location.location
import web.xhr.XMLHttpRequest
import web.xhr.XMLHttpRequestResponseType
import web.xhr.blob
import kotlin.math.min

class KanbanCard(val parent: KanbanColumn, val d: HTMLSpanElement):
    BaseElement {
    companion object {
        fun getSiblingCard(p: KanbanColumn): List<KanbanCard> {
            val elements: List<*> = p.d.querySelectorAll("span.kanban-card").asList()
            return elements.map {
                KanbanCard(p, it as HTMLSpanElement)
            }
        }
    }

    val taackDbClickAction = d.attributes.getNamedItem("taackDbClickAction")!!.value
    val cardId = d.attributes.getNamedItem("cardId")!!.value

    init {
        Helper.traceIndent("KanbanCard::init +++ cardId: $cardId")
        if (cardId != "") {
            d.ondragstart = EventHandler {
                parent.parent.draggedItem = this
                parent.parent.sourceColumn = this.parent
                window.setTimeout({
                    d.style.display = "none"
                }, 0)
            }

            d.ondragend = EventHandler {
                window.setTimeout({
                    d.style.display = "block"
                    parent.parent.draggedItem = null
                    parent.parent.sourceColumn = null
                }, 0)
            }
        }
        if (taackDbClickAction != "") {
            d.ondblclick = EventHandler {
                val url = createUrl(true, taackDbClickAction)
                val loader = document.getElementById(ElementId("taack-load-spinner"))
                val xhr = XMLHttpRequest()
                if (taackDbClickAction.contains("downloadBin")) {
                    trace("Binary Action ... $taackDbClickAction")
                    xhr.responseType = XMLHttpRequestResponseType.blob
                }
                xhr.onreadystatechange = EventHandler {
                    if (xhr.readyState == xhr.DONE) {
                        checkLogin(xhr)
                        if (xhr.status == 200.toShort()) {
                            if (xhr.responseType == XMLHttpRequestResponseType.blob) {
                                val contentDispo = xhr.getResponseHeader("Content-Disposition")
                                if (contentDispo != null) {
                                    val fileName = Regex("filename[^;=\n]*=((['\"]).*?\\2|[^;\n]*)").find(contentDispo)?.groupValues?.get(1)
                                    if (fileName != null) {
                                        trace("saveOrOpenBlog $fileName")
                                        saveOrOpenBlob(xhr.response as Blob, fileName)
                                    }
                                }
                            } else {
                                val text = xhr.responseText
                                if (text.substring(0, min(20, text.length)).contains(Regex(" html"))) {
                                    history.pushState("{}", document.title, xhr.responseURL)
                                    location.href = xhr.responseURL
                                    document.textContent = text
                                    document.close()
                                } else {
                                    processAjaxLink(url, text, parent)
                                }
                            }
                        }
                        loader?.classList?.add(ClassName("tck-hidden"))
                    } else if (xhr.readyState == xhr.OPENED) {
                        loader?.classList?.remove(ClassName("tck-hidden"))
                    }
                }
                xhr.open(RequestMethod.GET, url.toString())
                xhr.send()
            }
        }
        Helper.traceDeIndent("KanbanCard::init --- cardId: $cardId")
    }

    override fun getParentBlock(): Block {
        return parent.getParentBlock()
    }
}