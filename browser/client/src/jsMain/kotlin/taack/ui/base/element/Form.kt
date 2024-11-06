package taack.ui.base.element

import js.array.asList
import taack.ui.base.BaseElement
import taack.ui.base.Helper
import taack.ui.base.leaf.*
import web.html.HTMLFormElement

class Form(val parent: AjaxBlock, val f: HTMLFormElement):
    BaseElement {
    companion object {
        fun getSiblingForm(p: AjaxBlock): List<Form> {
            val elements: List<*> = p.d.querySelectorAll("form.taackForm").asList()
            return elements.map {
                Form(p, it as HTMLFormElement)
            }
        }
    }

    private val formName = f.attributes.getNamedItem("name")?.value
    private val actions: List<FormActionButton>
    private var m2oList: List<FormActionInputM2O>
    private val overrideFields: List<FormOverrideField>
    private var m2oSelectM2OList: List<FormActionSelectM2O>
    private var m2mList: List<FormActionInputM2M>
    val errorPlaceHolders: Map<String, FormErrorInput>

    fun rescanOverridableInputs() {
        Helper.traceIndent("Form::rescanOverridableInputs +++ formName: $formName")
        m2oList = FormActionInputM2O.getSiblingFormActionInputO2M(this)
        m2mList = FormActionInputM2M.getSiblingFormActionInputM2M(this)
        m2oSelectM2OList = FormActionSelectM2O.getSiblingFormActionSelectO2M(this)
        Helper.traceDeIndent("Form::rescanOverridableInputs --- formName: $formName")
    }

    init {
        Helper.traceIndent("Form::init +++ formName: $formName")
        actions = FormActionButton.getSiblingFormAction(this)
        m2oList = FormActionInputM2O.getSiblingFormActionInputO2M(this)
        m2mList = FormActionInputM2M.getSiblingFormActionInputM2M(this)
        overrideFields = FormOverrideField.getSiblingFormOverrideField(this)
        m2oSelectM2OList = FormActionSelectM2O.getSiblingFormActionSelectO2M(this)
        errorPlaceHolders = FormErrorInput.getSiblingErrorInput(this).associateBy {
            it.fieldName
        }
        Helper.traceDeIndent("Form::init --- formName: $formName")
    }

    fun cleanUpErrors() {
        for (errorInput in errorPlaceHolders.values) {
            errorInput.d.style.display = "none"
            errorInput.d.innerHTML = ""
        }
    }

    override fun getParentBlock(): Block {
        return parent.getParentBlock()
    }
}