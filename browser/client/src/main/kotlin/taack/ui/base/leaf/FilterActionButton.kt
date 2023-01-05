package taack.ui.base.leaf

import kotlinx.browser.window
import org.w3c.dom.*
import org.w3c.dom.events.Event
import org.w3c.fetch.RequestInit
import org.w3c.xhr.FormData
import taack.ui.base.Helper
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.LeafElement
import taack.ui.base.element.Block
import taack.ui.base.element.Filter
import taack.ui.base.record.RecordState
import kotlin.js.Promise

class FilterActionButton(private val parent: Filter, private val b: HTMLButtonElement?) : LeafElement {
    companion object {
        fun getSiblingFilterAction(f: Filter): List<FilterActionButton> {
            val elements: List<Node>?
            elements = f.f.querySelectorAll("button.taackFilterAction").asList()
            return elements.map {
                FilterActionButton(f, it as HTMLButtonElement)
            }
        }
    }

    private val state: RecordState = RecordState()

    init {
        trace("FilterActionButton::init ${b?.id}")
        b?.onclick = { e ->
            onClick(e)
        }
    }

    private fun onClick(e: Event) {
        b?.disabled = true
        val innerText = b?.innerText
        b?.innerText = "Submitting ..."
        e.preventDefault()
        trace("FilterActionButton::onclick")
        val pf = parent.f
        val fd = FormData(pf)
        fd.append("isAjax", "true")
        fd.append("refresh", "true")
        fd.append("filterTableId", parent.parent.blockId)
        fd.set("offset", "0")
        state.addClientStateAjaxBlock()
        state.addServerState(fd)
        window.fetch(b?.formAction ?: pf.action, RequestInit(method = "POST", body = fd)).then {
            if (it.ok) {
                val t = it.text()
                t
            } else {
                trace(it.statusText)
                Promise.reject(Throwable())
            }
        }.then {
//            mapAjaxText(it).map { me ->
//                val target = parent.parent//.parent.ajaxBlockElements?.get(me.key)
//                target.d.innerHTML = me.value
//            }
            if (it.startsWith("__redirect__")) {
                window.location.href = it.substring("__redirect__".length)
            } else if (it.startsWith("__reload__")) {
                window.location.href = (Block.href ?: "") + "?recordState=${RecordState.dumpServerState()}"
            } else if (it.startsWith("__ajaxBlockStart__")) {
                trace("__ajaxBlockStart__")
                Helper.mapAjaxText(it).map { me ->
                    val target = parent.parent.parent.ajaxBlockElements?.get(me.key)
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
                trace("FilterAction::closing Modal ...")
                if (parent.parent.parent.parent != null) parent.parent.parent.parent.close()
                else parent.parent.parent.modal.close()

            } else {
                trace("FilterActionButton::opening Modal")
                Helper.processAjaxLink(it, parent.parent.parent)
                parent.parent.parent.modal.open(it + "test")
            }
        }.then {
            b?.disabled = false
            if (innerText != null) b?.innerText = innerText
//            AjaxBlock.getSiblingAjaxBlock(parent.parent.parent)
        }
    }
}