package taack.ui.base.leaf

import js.array.asList
import taack.ui.base.Helper
import taack.ui.base.Helper.Companion.checkLogin
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.LeafElement
import taack.ui.base.element.Form
import web.cssom.ClassName
import web.dom.Node
import web.events.Event
import web.events.EventHandler
import web.html.*
import web.http.GET
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
        if (m2mClassList.contains(ClassName("M2MToDuplicate"))) input.name = ""
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
            checkLogin(xhr)
            Helper.processAjaxLink(url, xhr.responseText, parent.parent.parent, ::modalReturnSelect)
        }
        xhr.open(RequestMethod.GET, url)
        xhr.send()
    }

    private fun modalReturnSelect(idValueMap: Map<String, String>, otherField: Map<String, String>) {
        trace("FormActionInputM2M::modalReturnSelect $idValueMap $otherField")
        var currentInput = i
        idValueMap.entries.forEachIndexed { index, (key, value) ->
            trace("AUO $index $key $value")
            val m2mDiv = currentInput.parentElement!!
            trace("AUO1 $m2mDiv")
            if (m2mDiv.classList.contains(ClassName("M2MToDuplicate")) || (index + 1 < idValueMap.size && m2mDiv.nextElementSibling != null && m2mDiv.nextElementSibling!!.classList.contains(ClassName("M2MParent")))) {
                val m2mDivCloned = m2mDiv.cloneNode(true) as HTMLElement
                FormActionInputM2M(parent, m2mDivCloned.querySelector("input[taackAjaxFormM2MAction]") as HTMLInputElement)
                insertAfter(m2mDivCloned, m2mDiv)
            }
            m2mDiv.classList.remove(ClassName("M2MToDuplicate"))
            m2mDiv.classList.add(ClassName("M2MParent"))
            currentInput.value = value
            val i2 = currentInput.parentElement!!.querySelector("input[type='hidden']")!! as HTMLInputElement
            i2.name = inputName
            i2.value = key
            currentInput = currentInput.parentElement!!.nextElementSibling!!.querySelector("input[taackAjaxFormM2MAction]") as HTMLInputElement
        }
        for (field in otherField) {
            val taOrI = parent.f.querySelector("[id='${field.key}']")
            if (taOrI is HTMLInputElement) taOrI.value = field.value
            else if (taOrI is HTMLTextAreaElement) taOrI.value = field.value
        }
    }

    private fun insertAfter(newNode: Node, referenceNode: Node) {
        val parent = referenceNode.parentNode
        when {
            parent == null -> return
            parent.lastChild == referenceNode -> parent.appendChild(newNode)
            else -> parent.insertBefore(newNode, referenceNode.nextSibling)
        }
    }
}