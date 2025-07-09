package taack.ui.base.leaf

import js.array.asList
import taack.ui.base.Helper.Companion.processAjaxLink
import taack.ui.base.Helper.Companion.saveOrOpenBlob
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.LeafElement
import taack.ui.base.element.Table
import web.blob.Blob
import web.dom.document
import web.events.Event
import web.events.EventHandler
import web.form.FormData
import web.html.HTMLButtonElement
import web.html.HTMLFormElement
import web.html.HTMLInputElement
import web.http.POST
import web.http.RequestMethod
import web.location.location
import web.xhr.XMLHttpRequest
import web.xhr.XMLHttpRequestResponseType
import web.xhr.blob
import kotlin.math.min

class TableMultiSelectButton(private val parent: Table, private val b: HTMLButtonElement): LeafElement {
    companion object {
        fun getSiblingTableMultiSelectButton(p: Table): List<TableMultiSelectButton> {
            val elements: List<*> = p.t.querySelectorAll("button[type='submit']").asList()
            return elements.map {
                TableMultiSelectButton(p, it as HTMLButtonElement)
            }
        }
    }

    init {
        trace("TableMultiSelectButton::init ${b.formAction}")
        b.onclick = EventHandler { e ->
            onClick(e)
        }
    }

    private fun onClick(e: Event) {
        val f: HTMLFormElement = b.parentElement as HTMLFormElement
        val input: HTMLInputElement? = f.querySelector("input[type='hidden']") as HTMLInputElement?
        if (input != null && input.value !== "") {
            e.preventDefault()
            trace("TableMultiSelectButton::onclick: ${b.formAction}")
            val fd = FormData(f)
            fd.append("isAjax", "true")
            val xhr = XMLHttpRequest()
            if (b.formAction.contains("downloadBin")) {
                xhr.responseType = XMLHttpRequestResponseType.blob
            }
            xhr.onloadend = EventHandler {
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
                    val t = xhr.responseText
                    if (t.substring(0, min(20, t.length)).contains("<!DOCTYPE html>", false)) {
                        location.href = b.formAction
                        document.textContent =t
                        document.close()
                    } else {
                        processAjaxLink(null, t, parent, null)
                    }
                }
            }
            xhr.open(RequestMethod.POST, b.formAction)
            xhr.send(fd)
        } else {
            e.preventDefault()
        }
    }
}
