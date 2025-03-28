package taack.ui.base.leaf

import js.array.asList
import taack.ui.base.Helper
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.LeafElement
import taack.ui.base.element.Form
import web.events.Event
import web.events.EventHandler
import web.html.HTMLInputElement
import web.html.HTMLSelectElement
import web.html.HTMLTextAreaElement
import web.http.RequestMethod
import web.xhr.XMLHttpRequest

class FormActionInputM2O(private val parent: Form, private val i: HTMLInputElement) : LeafElement {
    companion object {
        fun getSiblingFormActionInputO2M(f: Form): List<FormActionInputM2O> {
            val elements: List<*> = f.f.querySelectorAll("input[taackAjaxFormM2OAction]").asList()
            return elements.map {
                FormActionInputM2O(f, it as HTMLInputElement)
            }
        }
    }

    init {
        trace("FormActionInputM2O::init ${i.name}")
        i.onclick = EventHandler{ e ->
            onClick(e)
        }
    }

    private fun onClick(e: Event) {
        e.preventDefault()
        trace("FormActionInputM2O::onclick")
        val action = i.attributes.getNamedItem("taackAjaxFormM2OAction")!!.value
        val additionalParams = mutableMapOf<String, String>()
        i.attributes.getNamedItem("taackFieldInfoParams")?.value?.split(",")?.map {
            var v = parent.f.elements.namedItem(it.replace(".id", ""))
            if (v == null) v = parent.f.elements.namedItem(it)
            if (v is HTMLSelectElement) {
                if (v.value.isNotBlank())
                    additionalParams["ajaxParams.$it"] = v.value

            }
            if (v is HTMLInputElement) {
                if (v.value.isNotBlank())
                    additionalParams["ajaxParams.$it"] = v.value
            }
        }
        val url = BaseAjaxAction.createUrl(true, action, additionalParams)
        // TODO: change to Post (see FilterActionButton.kt)
        val xhr = XMLHttpRequest()
        xhr.onloadend = EventHandler {
            Helper.processAjaxLink(xhr.responseText, parent.parent.parent, ::modalReturnSelect)
        }
        xhr.open(RequestMethod.GET, url)
        xhr.send()
    }

    private fun modalReturnSelect(key: String, value: String, otherField: Map<String, String>) {
        trace("FormActionInputM2O::modalReturnSelect $key $value $otherField")
        i.value = value
        val i2 = i.parentElement!!.querySelector("input[type=hidden]")!! as HTMLInputElement
        i2.value = key
        for (field in otherField) {
            val taOrI = parent.f.querySelector("[id='${field.key}']")
            if (taOrI is HTMLInputElement) taOrI.value = field.value
            else if (taOrI is HTMLTextAreaElement) taOrI.value = field.value
        }
    }
}