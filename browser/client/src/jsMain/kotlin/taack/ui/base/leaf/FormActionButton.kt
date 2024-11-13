package taack.ui.base.leaf

import js.array.asList
import taack.ui.base.Helper
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.LeafElement
import taack.ui.base.element.Form
import web.dom.document
import web.events.Event
import web.events.EventHandler
import web.form.FormData
import web.html.HTMLButtonElement
import web.html.HTMLInputElement
import web.html.HTMLTextAreaElement
import web.http.RequestMethod
import web.location.location
import web.xhr.XMLHttpRequest
import kotlin.math.min

class FormActionButton(private val parent: Form, private val b: HTMLButtonElement) : LeafElement {
    companion object {
        fun getSiblingFormAction(f: Form): List<FormActionButton> {
            val elements: List<*> = f.f.querySelectorAll("button[formaction]").asList()
            return elements.map {
                FormActionButton(f, it as HTMLButtonElement)
            }
        }
    }

    init {
        trace("FormActionButton::init ${b.formAction}")
        b.onclick = EventHandler { e ->
            onClick(e)
        }
    }

    private fun modalReturnSelect(key: String, value: String, otherField: Map<String, String>) {
        trace("FormActionButton::modalReturnSelect $key $value $otherField")
        for (field in otherField) {
            val taOrI = parent.f.querySelector("#${field.key}")
            if (taOrI is HTMLInputElement) taOrI.value = field.value
            else if (taOrI is HTMLTextAreaElement) taOrI.value = field.value
        }
    }

    private fun onClick(e: Event) {
        b.disabled = true
        val innerText = b.innerText
        b.innerText = "Submitting ..."
        e.preventDefault()
        trace("FormActionButton::onclick")
        val f = parent.f
        val fd = FormData(f)
        fd.append("isAjax", "true")
        val xhr = XMLHttpRequest()
        xhr.onloadend = EventHandler {
            b.disabled = false
            b.innerText = innerText
            val t = xhr.responseText
            if (t.substring(0, min(20, t.length)).contains("<!DOCTYPE html>", false)) {
                location.href = b.formAction
                document.write(t)
                document.close()
            } else {
                Helper.processAjaxLink(t, parent)
            }
        }
        xhr.open(RequestMethod.POST, b.formAction)
        xhr.send(fd)
    }
}
