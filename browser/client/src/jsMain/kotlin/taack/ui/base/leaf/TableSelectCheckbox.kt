package taack.ui.base.leaf

import js.array.asList
import taack.ui.base.LeafElement
import taack.ui.base.element.Table
import web.dom.document
import web.events.EventHandler
import web.html.*


class TableSelectCheckbox(private val parent: Table, private val i: HTMLInputElement) : LeafElement {
    companion object {
        fun getSiblingTableSelectCheckbox(p: Table): Pair<TableSelectCheckbox, List<TableSelectCheckbox>>? {
            val elements: List<*> = p.t.querySelectorAll("input[paramsKey]").asList()
            val inputToSelectAllRows = elements.find { (it as HTMLInputElement).attributes.getNamedItem("selectAll")?.value == "true" }
            if (inputToSelectAllRows != null) {
                val inputsToSelectCurrentRow = elements.filter { (it as HTMLInputElement).attributes.getNamedItem("selectAll")?.value != "true" }
                return Pair(TableSelectCheckbox(p, inputToSelectAllRows as HTMLInputElement), inputsToSelectCurrentRow.map { TableSelectCheckbox(p, it as HTMLInputElement) })
            } else {
                return null
            }
        }
    }

    private val paramsKey: String = i.attributes.getNamedItem("paramsKey")!!.value
    private val isSelectAll: Boolean = i.attributes.getNamedItem("selectAll")?.value == "true"
    private val isDisabled: Boolean = i.attributes.getNamedItem("disabled") != null

    init {
        i.onclick = EventHandler {
            if (isSelectAll) {
                parent.tableSelectCheckboxes?.second?.forEach {
                    if (!it.isDisabled) {
                        it.i.checked = i.checked
                        it.changeValue()
                    }
                }
            } else {
                if (!isDisabled) {
                    changeValue()
                }
            }
        }
    }

    private fun changeValue() {
        val inputs = document.querySelectorAll("input[type='hidden'][name='${paramsKey}']").asList().unsafeCast<List<HTMLInputElement>>()
        if (inputs.isNotEmpty()) {
            val stockedValues: MutableList<String> = inputs.first().value.split(",").filter { it.isNotEmpty() }.toMutableList()
            val index: Int = stockedValues.indexOf(i.value)
            if (i.checked) {
                if (index == -1) {
                    stockedValues.add(i.value)
                }
            } else {
                if (index > -1) {
                    stockedValues.removeAt(index)
                    parent.tableSelectCheckboxes?.first?.i?.checked = false
                }
            }
            inputs.forEach {
                it.value = stockedValues.joinToString(",")
            }
        }
    }
}