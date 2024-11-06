package taack.ui.base.leaf

import js.array.asList
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.base.LeafElement
import taack.ui.base.element.Form
import web.events.Event
import web.events.EventHandler
import web.html.HTMLImageElement

class FormOverrideField(private val parent: Form, private val i: HTMLImageElement) : LeafElement {
    companion object {
        fun getSiblingFormOverrideField(f: Form): List<FormOverrideField> {
            val elements: List<*> = f.f.querySelectorAll("img[taackonclickinnerhtml]").asList()
            return elements.map {
                FormOverrideField(f, it as HTMLImageElement)
            }
        }
    }

    init {
        trace("FormOverrideField::init ${i.id}")
        i.onclick = EventHandler { e ->
            onClick(e)
        }
    }

    private fun onClick(e: Event) {
        e.preventDefault()
        traceIndent("FormActionInputM2O::onclick +++")
        val action = i.attributes.getNamedItem("taackOnclickInnerHTML")!!.value
        i.parentElement?.innerHTML = action
        parent.rescanOverridableInputs()
        traceDeIndent("FormActionInputM2O::onclick ---")
    }

}