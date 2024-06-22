package taack.ui.base.leaf

import kotlinx.browser.window
import kotlinx.dom.addClass
import kotlinx.dom.hasClass
import kotlinx.dom.removeClass
import org.w3c.dom.*
import org.w3c.dom.events.Event
import org.w3c.fetch.RequestInit
import taack.ui.base.Helper
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.LeafElement
import taack.ui.base.element.Form
import kotlin.js.Promise

class FormActionInputM2M(private val parent: Form, private val i: HTMLInputElement) : LeafElement {
    companion object {
        fun getSiblingFormActionInputM2M(f: Form): List<FormActionInputM2M> {
            val elements: List<Node>?
            elements = f.f.querySelectorAll("input[taackAjaxFormM2MAction]").asList()
            return elements.map {
                FormActionInputM2M(f, it as HTMLInputElement)
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
        i.onclick = { e ->
            onClick(e)
        }
    }

    private fun onClick(e: Event) {
        e.preventDefault()
        trace("FormActionInputM2M::onclick")

//        val controller = i.attributes.getNamedItem("taackAjaxFormM2MController")!!.value
        val action = i.attributes.getNamedItem("taackAjaxFormM2MAction")!!.value

        val additionalParams = mutableMapOf<String, String>()
        i.attributes.getNamedItem("taackFieldInfoParams")?.value?.split(",")?.map {
            val v = parent.f[it]
            if (v is HTMLSelectElement) {
                if (v.value.isNotBlank())
                    additionalParams["ajaxParams.$it"] = v.value
//                    ajaxParams.append("ajaxParams.$it=${v.value}&")
            }
            if (v is HTMLInputElement) {
                if (v.value.isNotBlank())
                    additionalParams["ajaxParams.$it"] = v.value
//                    ajaxParams.append("ajaxParams.$it=${v.value}&")
            }
        }

//        val params = i.attributes.getNamedItem("taackAjaxFormM2MParams")?.value
        val url = BaseAjaxAction.createUrl(action, additionalParams)

        window.fetch(url.toString(), RequestInit(method = "GET")).then {
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
        trace("FormActionInputM2M::modalReturnSelect $key $value")
        val span = i.parentElement!!
        trace("AUO1 $span")
        if (span.hasClass("M2MToDuplicate")) {
            trace("AUO111")
            val span2 = span.cloneNode(true) as HTMLElement
            trace("AUO112 $span2")
            FormActionInputM2M(parent, span2.querySelector("input[taackAjaxFormM2MAction]") as HTMLInputElement)
            trace("AUO113")
            span.parentElement!!.appendChild(span2)
        }
        trace("AUO2 $span")
        span.removeClass("M2MToDuplicate")
        span.addClass("M2MParent")
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