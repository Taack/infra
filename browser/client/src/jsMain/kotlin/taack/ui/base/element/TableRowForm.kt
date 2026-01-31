package taack.ui.base.element

import js.array.asList
import taack.ui.base.BaseElement
import taack.ui.base.Helper
import taack.ui.base.leaf.TableRowFormButton
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

    init {
        Helper.traceIndent("TableRowForm::init +++ formName: $formName")
        TableRowFormButton.getSiblingTableRowFormButton(this)
        Helper.traceDeIndent("TableRowForm::init --- formName: $formName")
    }

    override fun getParentBlock(): Block {
        return parent.getParentBlock()
    }
}