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

class FormActionInputM2M(private val parent: Form, private val i: HTMLInputElement) : LeafElement {
    companion object {
        fun getSiblingFormActionInputM2M(f: Form): List<FormActionInputM2M> {
            val elements: List<*> = f.f.querySelectorAll("input[taackAjaxFormM2MAction]").asList()
            return elements.map {
                FormActionInputM2M(f, it as HTMLInputElement)
            }
        }
    }

    private val inputId = i.attributes.getNamedItem("taackAjaxFormM2MInputId")!!.value
    private val input = i.parentElement!!.querySelector("#${inputId}") as HTMLInputElement
    private val inputName = input.attributes.getNamedItem("attr-name")!!.value
    private val m2mClassList = input.parentElement!!.classList
    init {

        trace("FormActionInputM2M::init $inputName $m2mClassList")
        if (m2mClassList.contains("M2MToDuplicate")) input.name = ""
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

        val xhr = XMLHttpRequest()
        val url = BaseAjaxAction.createUrl(true, action, additionalParams)

        xhr.onloadend = EventHandler {
            Helper.processAjaxLink(url, xhr.responseText, parent.parent.parent, ::modalReturnSelect)
        }
        xhr.open(RequestMethod.GET, url)
        xhr.send()
    }

    private fun modalReturnSelect(key: String, value: String, otherField: Map<String, String>) {
        trace("FormActionInputM2M::modalReturnSelect $key $value")
        val m2mDiv = i.parentElement!!
        trace("AUO1 $m2mDiv")
        if (m2mDiv.classList.contains("M2MToDuplicate")) {
            val m2mDivCloned = m2mDiv.cloneNode(true) as HTMLElement
            FormActionInputM2M(parent, m2mDivCloned.querySelector("input[taackAjaxFormM2MAction]") as HTMLInputElement)
            m2mDiv.parentElement!!.appendChild(m2mDivCloned)
        }
        m2mDiv.classList.remove("M2MToDuplicate")
        m2mDiv.classList.add("M2MParent")
        i.value = value
        val i2 = i.parentElement!!.querySelector("#${inputId}")!! as HTMLInputElement
        i2.name = inputName
        i2.value = key
        for (field in otherField) {
            val taOrI = parent.f.querySelector("[id='${field.key}']")
            if (taOrI is HTMLInputElement) taOrI.value = field.value
            else if (taOrI is HTMLTextAreaElement) taOrI.value = field.value
        }
    }
}