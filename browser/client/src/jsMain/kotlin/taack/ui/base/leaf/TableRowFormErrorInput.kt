package taack.ui.base.leaf

import js.array.asList
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.LeafElement
import taack.ui.base.element.Form
import taack.ui.base.element.TableRow
import taack.ui.base.element.TableRowForm
import web.html.HTMLDivElement

class TableRowFormErrorInput(private val parent: TableRowForm, val d: HTMLDivElement) : LeafElement {
    companion object {
        fun getSiblingTableRowFormErrorInput(p: TableRowForm): List<TableRowFormErrorInput> {
            val elements: List<*> = p.parent.r.querySelectorAll("div[taackfielderror]").asList()
            return elements.map {
                TableRowFormErrorInput(p, it as HTMLDivElement)
            }
        }
    }

    val fieldName: String = d.attributes.getNamedItem("taackFieldError")!!.value

    init {
        trace("TableRowFormErrorInput::init $fieldName")
    }
}

