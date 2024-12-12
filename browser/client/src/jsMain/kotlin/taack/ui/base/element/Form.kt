package taack.ui.base.element

import js.array.asList
import taack.ui.base.BaseElement
import taack.ui.base.Helper
import taack.ui.base.leaf.*
import taack.ui.canvas.MainCanvas
import web.dom.document
import web.file.File
import web.html.HTMLDivElement
import web.html.HTMLFormElement
import web.html.HTMLTextAreaElement

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
    val mapFileToSend: MutableMap<String, MutableList<File>> = mutableMapOf()
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

        val textareaList = document.querySelectorAll("textarea.asciidoctor")

        for (element in textareaList) {
            val textarea = element as HTMLTextAreaElement
            textarea.style.display = "none"
            val scrollContainer = document.createElement("div") as HTMLDivElement
            scrollContainer.style.height = "calc(max(30vh, 320px))"
            scrollContainer.style.border = "1px solid grey"
            scrollContainer.style.overflow = "auto"
            val largeContainer = document.createElement("div") as HTMLDivElement
            largeContainer.style.overflow = "hidden"
            val canvasContainer = document.createElement("div") as HTMLDivElement
            largeContainer.append(canvasContainer)
            scrollContainer.append(largeContainer)
            textarea.parentElement?.append(scrollContainer)
            MainCanvas(this, textarea, canvasContainer, scrollContainer)
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