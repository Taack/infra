package taack.ui.base.leaf

import org.w3c.dom.HTMLInputElement
import org.w3c.dom.Node
import org.w3c.dom.asList
import taack.ui.base.LeafElement
import taack.ui.base.element.Form

class FormOverrideField(private val parent: Form, private val i: HTMLInputElement) : LeafElement {
    companion object {
        fun getSiblingFormActionInputO2M(f: Form): List<FormActionInputM2O> {
            val elements: List<Node>?
            elements = f.f.querySelectorAll("input.taackFormFieldOverrideM2O").asList()
            return elements.map {
                FormActionInputM2O(f, it as HTMLInputElement)
            }
        }
    }

}