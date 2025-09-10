package taack.ui.base.leaf

import js.array.asList
import taack.ui.base.Helper
import taack.ui.base.Helper.Companion.checkLogin
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.LeafElement
import taack.ui.base.element.Form
import web.events.Event
import web.events.EventHandler
import web.form.FormData
import web.html.HTMLInputElement
import web.html.HTMLSelectElement
import web.html.HTMLTextAreaElement
import web.http.POST
import web.http.RequestMethod
import web.url.URL
import web.xhr.XMLHttpRequest

class FormTriggerUpdate(private val parent: Form, private val inputElement: HTMLInputElement, private val currentURL: URL?) : LeafElement {
    companion object {
        fun getSiblingFormTriggerUpdate(f: Form): List<FormTriggerUpdate> {
            val elements: List<*> = f.f.querySelectorAll("input[type=hidden][name=__triggerUpdate__]").asList()
            return elements.map {
                FormTriggerUpdate(f, it as HTMLInputElement, BaseAjaxAction.lastUrlClicked)
            }
        }
    }

    init {
        trace("FormTriggerUpdate::init ${inputElement.formAction} ${inputElement.value} ${currentURL} ${currentURL?.searchParams?.has("originController")}")

        (parent.f.querySelector("select[name=${inputElement.value}]") as HTMLSelectElement?)?.onchange = EventHandler { e ->
            onChange(e)
        }
        (parent.f.querySelector("input[name=${inputElement.value}]") as HTMLInputElement?)?.onchange = EventHandler { e ->
            onChange(e)
        }
    }

    private fun modalReturnSelect(key: String, value: String, otherField: Map<String, String>) {
        trace("FormTriggerUpdate::modalReturnSelect $key $value $otherField")
        for (field in otherField) {
            val taOrI = parent.f.querySelector("#${field.key}")
            if (taOrI is HTMLInputElement) taOrI.value = field.value
            else if (taOrI is HTMLTextAreaElement) taOrI.value = field.value
        }
    }

    private fun onChange(e: Event) {
        inputElement.disabled = true
        val innerText = inputElement.innerText
        inputElement.innerText = "Submitting ..."
        e.preventDefault()
        trace("FormTriggerUpdate::onclick: ${inputElement.formAction}")
        val f = parent.f
        val fd = FormData(f)
        fd.set("isAjax", "true")
        fd.set("refresh", "true")

        parent.mapFileToSend.forEach { inputKey ->
            inputKey.value.forEach { fileValue ->
                fd.append(inputKey.key, fileValue)
            }
        }
        val xhr = XMLHttpRequest()
        xhr.onloadend = EventHandler {
            checkLogin(xhr)
            val t = xhr.responseText
            parent.parent.d.innerHTML = t
            parent.parent.refresh()
//            Helper.processAjaxLink(t, parent, ::modalReturnSelect)
        }
        val targetUrl = Helper.urlStack.last()
        targetUrl.searchParams.delete("isAjax")
        targetUrl.searchParams.delete("refresh")
        xhr.open(RequestMethod.POST, targetUrl)
        xhr.send(fd)
    }
}
