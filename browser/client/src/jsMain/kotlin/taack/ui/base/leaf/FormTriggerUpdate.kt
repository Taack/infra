package taack.ui.base.leaf

import js.array.asList
import taack.ui.base.Helper
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.LeafElement
import taack.ui.base.element.Form
import web.events.Event
import web.events.EventHandler
import web.form.FormData
import web.html.HTMLInputElement
import web.html.HTMLSelectElement
import web.html.HTMLTextAreaElement
import web.http.RequestMethod
import web.xhr.XMLHttpRequest

class FormTriggerUpdate(private val parent: Form, private val inputElement: HTMLInputElement) : LeafElement {
    companion object {
        fun getSiblingFormTriggerUpdate(f: Form): List<FormTriggerUpdate> {
            val elements: List<*> = f.f.querySelectorAll("input[type=hidden][name=__triggerUpdate__]").asList()
            return elements.map {
                FormTriggerUpdate(f, it as HTMLInputElement)
            }
        }
    }

    init {
        trace("FormTriggerUpdate::init ${inputElement.formAction} ${inputElement.value}")

        (parent.f.querySelector("select[name=${inputElement.value}]") as HTMLSelectElement).onchange = EventHandler { e ->
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
//        fd.append("isAjax", "true")
        fd["refresh"] = "true"

        parent.mapFileToSend.forEach { inputKey ->
            inputKey.value.forEach { fileValue ->
                fd.append(inputKey.key, fileValue)
            }
        }
        val xhr = XMLHttpRequest()
        xhr.onloadend = EventHandler {
            val t = xhr.responseText
            println(t)
            parent.parent.d.innerHTML = t
            parent.parent.refresh()
//            Helper.processAjaxLink(t, parent, ::modalReturnSelect)
        }
        xhr.open(RequestMethod.POST, BaseAjaxAction.lastUrlClicked!!)
        xhr.send(fd)
    }
}
