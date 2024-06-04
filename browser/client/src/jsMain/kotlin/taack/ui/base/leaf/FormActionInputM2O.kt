package taack.ui.base.leaf

import kotlinx.browser.window
import org.w3c.dom.*
import org.w3c.dom.events.Event
import org.w3c.fetch.RequestInit
import taack.ui.base.Helper
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.LeafElement
import taack.ui.base.element.Form
import kotlin.js.Promise

class FormActionInputM2O(private val parent: Form, private val i: HTMLInputElement) : LeafElement {
    companion object {
        fun getSiblingFormActionInputO2M(f: Form): List<FormActionInputM2O> {
            val elements: List<Node>?
            elements = f.f.querySelectorAll("input[taackAjaxFormM2OAction]").asList()
            return elements.map {
                FormActionInputM2O(f, it as HTMLInputElement)
            }
        }
    }

    init {
        trace("FormActionInputM2O::init ${i.name}")
        i.onclick = { e ->
            onClick(e)
        }
    }

    private fun onClick(e: Event) {
        e.preventDefault()
        trace("FormActionInputM2O::onclick")
        val action = i.attributes.getNamedItem("taackAjaxFormM2OAction")!!.value
        val additionalParams = mutableMapOf<String, String>()
        i.attributes.getNamedItem("taackFieldInfoParams")?.value?.split(",")?.map {
            var v = parent.f[it.replace(".id", "")]
            if (v == null) v = parent.f[it]
            if (v is HTMLSelectElement) {
                if (v.value.isNotBlank())
                    additionalParams["ajaxParams.$it"] = v.value

            }
            if (v is HTMLInputElement) {
                if (v.value.isNotBlank())
                    additionalParams["ajaxParams.$it"] = v.value
            }
        }
        val url = BaseAjaxAction.createUrl(action, additionalParams)
        // TODO: change to Post (see FilterActionButton.kt)
        window.fetch(
            url.toString(),
            RequestInit(method = "GET")
        ).then {
            if (it.ok) {
                it.text()
            } else {
                trace(it.statusText)
                Promise.reject(Throwable())
            }
        }.then {
            Helper.processAjaxLink(it, parent.parent.parent, ::modalReturnSelect)
        }
    }

    private fun modalReturnSelect(key: String, value: String, otherField: Map<String, String>) {
        trace("FormActionInputM2O::modalReturnSelect $key $value")
        i.value = value
        val i2 = i.parentElement!!.querySelector("input[type=hidden]")!! as HTMLInputElement
        i2.value = key
        for (field in otherField) {
            val taOrI = parent.f.querySelector("#${field.key}")
            if (taOrI is HTMLInputElement) taOrI.value = field.value
            else if (taOrI is HTMLTextAreaElement) taOrI.value = field.value
        }
    }
}