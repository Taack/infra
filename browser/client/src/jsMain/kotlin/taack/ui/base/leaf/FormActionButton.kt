package taack.ui.base.leaf

import kotlinx.browser.window
import org.w3c.dom.*
import org.w3c.dom.events.Event
import org.w3c.fetch.RequestInit
import org.w3c.xhr.FormData
import taack.ui.base.Helper
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.LeafElement
import taack.ui.base.element.Form
import kotlin.js.Promise

class FormActionButton(private val parent: Form, private val b: HTMLButtonElement) : LeafElement {
    companion object {
        fun getSiblingFormAction(f: Form): List<FormActionButton> {
            val elements: List<Node>?
            elements = f.f.querySelectorAll("button[formaction]").asList()
            return elements.map {
                FormActionButton(f, it as HTMLButtonElement)
            }
        }
    }

    init {
        trace("FormActionButton::init ${b.formAction}")
        b.onclick = { e ->
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
        window.fetch(b.formAction, RequestInit(method = "POST", body = fd)).then {
            if (it.ok) {
                it.text()
            } else {
                trace(it.statusText)
                Promise.reject(Throwable())
            }
        }.then {
            Helper.processAjaxLink(it, parent)
        }.then {
            b.disabled = false
            b.innerText = innerText
        }

//        }.then {
//            AjaxBlock.getSiblingAjaxBlock(parent.parent.parent)
    }
}
