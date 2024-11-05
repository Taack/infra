package taack.ui.base.leaf

import js.array.asList
import taack.ui.base.Helper
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.LeafElement
import taack.ui.base.element.Form
import web.events.Event
import web.events.EventHandler
import web.html.HTMLElement
import web.html.HTMLInputElement
import web.html.HTMLSelectElement
import web.html.HTMLTextAreaElement
import web.http.RequestMethod
import web.xhr.XMLHttpRequest
import kotlin.js.Promise

class FormActionInputM2M(private val parent: Form, private val i: HTMLInputElement) : LeafElement {
    companion object {
        fun getSiblingFormActionInputM2M(f: Form): List<FormActionInputM2M> {
            val elements: List<HTMLInputElement>?
            elements = f.f.querySelectorAll("input[taackAjaxFormM2MAction]") as List<HTMLInputElement>
            return elements.map {
                FormActionInputM2M(f, it)
            }
        }
    }

    private val inputId = i.attributes.getNamedItem("taackAjaxFormM2MInputId")!!.value
    private val input = i.parentElement!!.querySelector("#${inputId}") as HTMLInputElement
    private val inputName = input.attributes.getNamedItem("attr-name")!!.value
    private val spanClassName: String = input.parentElement!!.className
    init {

        trace("FormActionInputM2M::init $inputName $spanClassName")
        if (spanClassName == "M2MToDuplicate") input.name = ""
        i.onclick = EventHandler { e ->
            onClick(e)
        }
    }

    private fun onClick(e: Event) {
        e.preventDefault()
        trace("FormActionInputM2M::onclick")

        val action = i.attributes.getNamedItem("taackAjaxFormM2MAction")!!.value

        val additionalParams = mutableMapOf<String, String>()
        i.attributes.getNamedItem("taackFieldInfoParams")?.value?.split(",")?.map { s: String ->
            val v = parent.f.elements.asList().find { it.attributes.getNamedItem("name")?.value == s }
            if (v is HTMLSelectElement) {
                if (v.value.isNotBlank())
                    additionalParams["ajaxParams.$s"] = v.value
            }
            if (v is HTMLInputElement) {
                if (v.value.isNotBlank())
                    additionalParams["ajaxParams.$s"] = v.value
            }
        }

        val url = BaseAjaxAction.createUrl(true, action, additionalParams)

        val xhr = XMLHttpRequest()
        xhr.onloadend = EventHandler {
            Helper.processAjaxLink(xhr.responseText, parent.parent.parent, ::modalReturnSelect)
        }
    }

    private fun modalReturnSelect(key: String, value: String, otherField: Map<String, String>) {
        trace("FormActionInputM2M::modalReturnSelect $key $value")
        val span = i.parentElement!!
        trace("AUO1 $span")
        if (span.classList.contains("M2MToDuplicate")) {
            val span2 = span.cloneNode(true) as HTMLElement
            FormActionInputM2M(parent, span2.querySelector("input[taackAjaxFormM2MAction]") as HTMLInputElement)
            span.parentElement!!.appendChild(span2)
        }
        span.classList.remove("M2MToDuplicate")
        span.classList.add("M2MParent")
        i.value = value
        val i2 = i.parentElement!!.querySelector("#${inputId}")!! as HTMLInputElement
        i2.name = inputName
        i2.value = key
        for (field in otherField) {
            val taOrI = parent.f.querySelector("#${field.key}")
            if (taOrI is HTMLInputElement) taOrI.value = field.value
            else if (taOrI is HTMLTextAreaElement) taOrI.value = field.value
        }
    }
}