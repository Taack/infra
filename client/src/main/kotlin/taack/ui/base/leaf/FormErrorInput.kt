package taack.ui.base.leaf

import org.w3c.dom.HTMLDivElement
import org.w3c.dom.Node
import org.w3c.dom.asList
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.LeafElement
import taack.ui.base.element.Form

class FormErrorInput(private val parent: Form, val d: HTMLDivElement) : LeafElement {
    companion object {
        fun getSiblingErrorInput(p: Form): List<FormErrorInput> {
            val elements: List<Node>?
            elements = p.f.querySelectorAll("div.taackFieldError").asList()
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

