package taack.ui.base.leaf

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.*
import org.w3c.dom.events.Event
import org.w3c.fetch.RequestInit
import taack.ui.base.Helper
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.LeafElement
import taack.ui.base.element.Form
import kotlin.js.Promise

class FormActionSelectM2O(private val parent: Form, private val sel: HTMLSelectElement) : LeafElement {
    companion object {
        fun getSiblingFormActionSelectO2M(f: Form): List<FormActionSelectM2O> {
            val elements: List<Node>?
            elements = f.f.querySelectorAll("select.taackAjaxFormSelectM2O").asList()
            return elements.map {
                FormActionSelectM2O(f, it as HTMLSelectElement)
            }
        }
    }

    init {
        trace("FormActionSelectM2O::init ${sel.name}")
        sel.onmousedown = { e ->
            onClick(e)
            e.preventDefault()
        }
    }

    private val selectId = sel.attributes.getNamedItem("taackAjaxFormM2OSelectId")!!.value

    private fun onClick(e: Event) {
        e.preventDefault()
        trace("FormActionInputM2O::onclick")
        val action = sel.attributes.getNamedItem("taackAjaxFormM2OAction")!!.value
        val additionalParams = mutableMapOf<String, String>()
        sel.attributes.getNamedItem("taackFieldInfoParams")?.value?.split(",")?.map {
            val v = parent.f[it]
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
        val url = BaseAjaxAction.createUrl(action, additionalParams)
        window.fetch(url.toString(),
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
        trace("FormActionSelectM2O::modalReturnSelect $key $value")
        val opt = document.createElement("option") as HTMLOptionElement
        opt.value = key
        opt.text = value
        opt.selected = true
        sel.options[0] = opt
//        for (opt in sel.options.asList()) {
//            val o = opt as HTMLOptionElement
//            if (o.value == key) {
//                sel.selectedIndex = o.index
//                break
//            }
//        }
        for (field in otherField) {
            val taOrI = parent.f.querySelector("#${field.key}")
            if (taOrI is HTMLInputElement) taOrI.value = field.value
            else if (taOrI is HTMLTextAreaElement) taOrI.value = field.value
        }
    }
}