package taack.ui.base.element

import js.array.asList
import taack.ui.base.BaseElement
import taack.ui.base.Helper
import taack.ui.base.leaf.TableRowFormButton
import taack.ui.base.leaf.TableRowFormErrorInput
import web.html.HTMLFormElement

class TableRowForm(val parent: TableRow, val f: HTMLFormElement):
    BaseElement {
    companion object {
        fun getSiblingForm(p: TableRow): List<TableRowForm> {
            val elements: List<*> = p.r.querySelectorAll("form.taackTableInlineForm").asList()
            return elements.map {
                TableRowForm(p, it as HTMLFormElement)
            }
        }
    }

    private val formName = f.attributes.getNamedItem("name")?.value
    val errorInputs = TableRowFormErrorInput.getSiblingTableRowFormErrorInput(this).associateBy {
        it.fieldName
    }

    init {
        Helper.traceIndent("TableRowForm::init +++ formName: $formName")
        TableRowFormButton.getSiblingTableRowFormButton(this)

        Helper.traceDeIndent("TableRowForm::init --- formName: $formName")
    }

    fun cleanUpErrors() {
        for (errorInput in errorInputs.values) {
            errorInput.d.style.display = "none"
            errorInput.d.innerHTML = ""
        }
    }

    override fun getParentBlock(): Block {
        return parent.getParentBlock()
    }
}