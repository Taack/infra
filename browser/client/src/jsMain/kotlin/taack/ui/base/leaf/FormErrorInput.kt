package taack.ui.base.leaf

import js.array.asList
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.LeafElement
import taack.ui.base.element.Form
import web.html.HTMLDivElement

class FormErrorInput(private val parent: Form, val d: HTMLDivElement) : LeafElement {
    companion object {
        fun getSiblingErrorInput(p: Form): List<FormErrorInput> {
            val elements: List<*> = p.f.querySelectorAll("div[taackfielderror]").asList()
            return elements.map {
                FormErrorInput(p, it as HTMLDivElement)
            }
        }
    }

    val fieldName: String = d.attributes.getNamedItem("taackFieldError")!!.value

    init {
        trace("FormErrorInput::init $fieldName")
    }
}

