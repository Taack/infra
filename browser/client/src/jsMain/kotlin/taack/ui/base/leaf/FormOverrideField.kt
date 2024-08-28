package taack.ui.base.leaf

import org.w3c.dom.HTMLImageElement
import org.w3c.dom.Node
import org.w3c.dom.asList
import org.w3c.dom.events.Event
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.base.LeafElement
import taack.ui.base.element.Form

class FormOverrideField(private val parent: Form, private val i: HTMLImageElement) : LeafElement {
    companion object {
        fun getSiblingFormOverrideField(f: Form): List<FormOverrideField> {
            val elements: List<Node>?
            elements = f.f.querySelectorAll("img[taackonclickinnerhtml]").asList()
            return elements.map {
                FormOverrideField(f, it as HTMLImageElement)
            }
        }
    }

    init {
        trace("FormOverrideField::init ${i.id}")
        i.onclick = { e ->
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