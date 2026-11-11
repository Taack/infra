package taack.ui.base.leaf

import js.array.asList
import taack.ui.base.Helper
import taack.ui.base.Helper.Companion.checkLogin
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.LeafElement
import taack.ui.base.element.Form
import web.events.Event
import web.events.EventHandler
import web.html.HTMLInputElement
import web.html.HTMLSelectElement
import web.html.HTMLTextAreaElement
import web.http.GET
import web.http.RequestMethod
import web.xhr.XMLHttpRequest
import web.html.*

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
        i.attributes.getNamedItem("taackFieldInfoParams")?.value?.split(",")?.forEach { s: String ->
            val v = parent.f.elements.asList().find { it.attributes.getNamedItem("name")?.value == s }
            val collectedValues = mutableListOf<String>()
            when (v) {
                is HTMLSelectElement -> {
                    if (v.multiple) {
                        val options = v.selectedOptions.asList().map { it as HTMLOptionElement }
                        options.forEach { opt ->
                            if (opt.value.isNotBlank()) collectedValues.add(opt.value)
                        }
                    } else {
                        if (v.value.isNotBlank()) {
                            collectedValues.add(v.value)
                        }
                    }
                }
                is HTMLInputElement -> {
                    if (v.value.isNotBlank()) collectedValues.add(v.value)
                }
            }
            if (collectedValues.isNotEmpty()) {
                additionalParams["ajaxParams.$s"] = collectedValues.joinToString(",")
            }
        }

        val url = BaseAjaxAction.createUrl(true, action, additionalParams)
        // TODO: change to Post (see FilterActionButton.kt)
        val xhr = XMLHttpRequest()
        xhr.onloadend = EventHandler {
            checkLogin(xhr)
            Helper.processAjaxLink(url, xhr.responseText, parent.parent.parent, ::modalReturnSelect)
        }
        xhr.open(RequestMethod.GET, url)
        xhr.send()
    }

    private fun modalReturnSelect(idValueMap: Map<String, String>, otherField: Map<String, String>) {
        trace("FormActionInputM2O::modalReturnSelect $idValueMap $otherField")
        val key = idValueMap.keys.first()
        i.value = idValueMap[key]!!
        val i2 = i.parentElement!!.querySelector("input[type=hidden]")!! as HTMLInputElement
        i2.value = key
        for (field in otherField) {
            val taOrI = parent.f.querySelector("[id='${field.key}']")
            if (taOrI is HTMLInputElement) taOrI.value = field.value
            else if (taOrI is HTMLTextAreaElement) taOrI.value = field.value
        }
    }
}