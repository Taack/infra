package taack.ui.base.leaf

import js.array.asList
import taack.ui.base.Helper
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.LeafElement
import taack.ui.base.element.Form
import web.dom.document
import web.events.Event
import web.events.EventHandler
import web.html.HTMLInputElement
import web.html.HTMLOptionElement
import web.html.HTMLSelectElement
import web.html.HTMLTextAreaElement
import web.http.RequestMethod
import web.xhr.XMLHttpRequest

class FormActionSelectM2O(private val parent: Form, private val sel: HTMLSelectElement) : LeafElement {
    companion object {
        fun getSiblingFormActionSelectO2M(f: Form): List<FormActionSelectM2O> {
            val elements: List<*> = f.f.querySelectorAll("select.taackAjaxFormSelectM2O").asList()
            return elements.map {
                FormActionSelectM2O(f, it as HTMLSelectElement)
            }
        }
    }

    init {
        trace("FormActionSelectM2O::init ${sel.name}")
        sel.onmousedown = EventHandler { e ->
            onClick(e)
            e.preventDefault()
        }
    }

    private fun onClick(e: Event) {
        e.preventDefault()
        trace("FormActionInputM2O::onclick")
        val action = sel.attributes.getNamedItem("taackAjaxFormM2OAction")!!.value
        val additionalParams = mutableMapOf<String, String>()
        sel.attributes.getNamedItem("taackFieldInfoParams")?.value?.split(",")?.map {
            val v = parent.f.elements.namedItem(it)
            if (v is HTMLSelectElement) {
                if (v.value.isNotBlank())
                    additionalParams["ajaxParams.$it"] = v.value
            }
            if (v is HTMLInputElement) {
                if (v.value.isNotBlank())
                    additionalParams["ajaxParams.$it"] = v.value
            }
        }
        // TODO: change to Post (see FilterActionButton.kt)
        val url = BaseAjaxAction.createUrl(true, action, additionalParams)
        val xhr = XMLHttpRequest()
        xhr.onloadend = EventHandler {
            Helper.processAjaxLink(xhr.responseText, parent.parent.parent, ::modalReturnSelect)
        }
        xhr.open(RequestMethod.GET, url)
        xhr.send()
    }

    private fun modalReturnSelect(key: String, value: String, otherField: Map<String, String>) {
        trace("FormActionSelectM2O::modalReturnSelect $key $value $otherField")
        val opt = document.createElement("option") as HTMLOptionElement
        opt.value = key
        opt.text = value
        opt.selected = true
        sel.options.add(opt, 0)
        for (field in otherField) {
            val taOrI = parent.f.querySelector("#${field.key}")
            if (taOrI is HTMLInputElement) taOrI.value = field.value
            else if (taOrI is HTMLTextAreaElement) taOrI.value = field.value
        }
    }
}