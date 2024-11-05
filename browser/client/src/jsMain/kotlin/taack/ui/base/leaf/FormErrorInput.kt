package taack.ui.base.leaf

import taack.ui.base.Helper.Companion.trace
import taack.ui.base.LeafElement
import taack.ui.base.element.Form
import web.html.HTMLDivElement

class FormErrorInput(private val parent: Form, val d: HTMLDivElement) : LeafElement {
    companion object {
        fun getSiblingErrorInput(p: Form): List<FormErrorInput> {
            val elements: List<HTMLDivElement> = p.f.querySelectorAll("div[taackfielderror]") as List<HTMLDivElement>
            return elements.map {
                FormErrorInput(p, it)
            }
        }
    }

    val fieldName: String = d.attributes.getNamedItem("taackFieldError")!!.value

    init {
        trace("FormErrorInput::init $fieldName")
    }
}

