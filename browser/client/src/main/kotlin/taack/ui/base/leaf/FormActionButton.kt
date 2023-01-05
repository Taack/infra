package taack.ui.base.leaf

import kotlinx.browser.window
import org.w3c.dom.*
import org.w3c.dom.events.Event
import org.w3c.fetch.RequestInit
import org.w3c.xhr.FormData
import taack.ui.base.Helper
import taack.ui.base.Helper.Companion.mapAjaxErrors
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.LeafElement
import taack.ui.base.element.Block
import taack.ui.base.element.Form
import taack.ui.base.record.RecordState
import kotlin.js.Promise

class FormActionButton(private val parent: Form, private val b: HTMLButtonElement) : LeafElement {
    companion object {
        fun getSiblingFormAction(f: Form): List<FormActionButton> {
            val elements: List<Node>?
            elements = f.f.querySelectorAll("button.taackFormAction").asList()
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
        if (!fd.has("recordState")) fd.append("recordState", RecordState.dumpServerState())
        window.fetch(b.formAction, RequestInit(method = "POST", body = fd)).then {
            if (it.ok) {
                it.text()
            } else {
                trace(it.statusText)
                Promise.reject(Throwable())
            }
        }.then {
            if (it.startsWith("__redirect__")) {
                window.location.href = it.substring("__redirect__".length)
            } else if (it.startsWith("__reload__")) {
                window.location.href = (Block.href ?: "") + "${(if (Block.href!!.contains("?")) "&" else "?")}recordState=${RecordState.dumpServerState()}"
            } else if (it.startsWith("__ajaxBlockStart__")) {
                trace("__ajaxBlockStart__ ${parent.parent.parent.ajaxBlockElements}")
                Helper.mapAjaxText(it).map { me ->
                    val target = parent.parent.parent.ajaxBlockElements?.get(me.key)
                    //parent.parent.parent.ajaxBlockElements?.get(me.key)
                    target!!.d.innerHTML = me.value
                    target.refresh()
                }
            } else if (it.startsWith("closeLastModal:")) {
                val m = "closeLastModal:"
                val fi = ":__FieldInfo__:"
                val fie = ":__FieldInfoEnd__"
                val text = it
                val pos = text.indexOf(':', m.length)
                var posField = text.indexOf(fi)
                if (Helper.processingStack.isNotEmpty()) {
                    trace("Helper::process")
                    val id = text.substring(m.length, pos)
                    val value = if (posField == -1) text.substring(pos + 1) else text.substring(pos + 1, posField)
                    var otherField = emptyMap<String, String>()
                    while (posField != -1) {
                        val endFieldNameIndex = text.indexOf(':', posField + fi.length)
                        val fieldName = text.substring(posField + fi.length, endFieldNameIndex)
                        val endFieldValueIndex = text.indexOf(fie, endFieldNameIndex)
                        val fieldValue = text.substring(endFieldNameIndex + 1, endFieldValueIndex)
                        otherField = otherField.plus(Pair(fieldName, fieldValue))
                        posField = text.indexOf(fi, endFieldValueIndex)
                    }
                    if (id.isNotEmpty() && value.isNotEmpty()) {
                        val fct = Helper.processingStack.last()
                        fct(id, value, otherField)
                    } else {
                        for (field in otherField) {
                            val taOrI = parent.f.querySelector("#${field.key}")
                            if (taOrI is HTMLInputElement) taOrI.value = field.value
                            else if (taOrI is HTMLTextAreaElement) taOrI.value = field.value
                        }
                    }
                }
                trace("FormAction::closing Modal ...")
                if (parent.parent.parent.parent != null) parent.parent.parent.parent.close()
                else parent.parent.parent.modal.close()

            } else if (it.startsWith("closeLastModalAndUpdateBlock:")) {
                if (parent.parent.parent.parent != null) parent.parent.parent.parent.close()
                else parent.parent.parent.modal.close()
                val m = Helper.mapAjaxText(it.substring(29))
                m.map { me ->
                    val target = parent.parent.parent.parent?.parent?.ajaxBlockElements?.get(me.key)
                    target!!.d.innerHTML = me.value
                    target.refresh()
                }
            } else if (it.startsWith("__ErrorKeyStart__")) {
                var hasErrors = false
                parent.cleanUpErrors()
                val map = mapAjaxErrors(it).map { me ->
                    hasErrors = true
                    val d = parent.errorPlaceHolders[me.key]?.d
                    if (d != null) {
                        d.innerHTML = me.value
                        d.style.display = "block"
                    }
                }
                if (!hasErrors) {
                    trace("FormActionButton::hasNoErrors")
                } else {
                    trace("FormActionButton::hasErrors $map")
                }
            } else {
                trace("FormActionButton::opening Modal")
                Helper.processAjaxLink(it, parent.parent.parent, ::modalReturnSelect)
                parent.parent.parent.modal.open(it)
//                Block.getSiblingBlockElement(parent.parent.parent.modal)
            }
        }.then {
            b.disabled = false
            b.innerText = innerText
        }

//        }.then {
//            AjaxBlock.getSiblingAjaxBlock(parent.parent.parent)
    }
}
