package taack.ui.base.element

import kotlinx.browser.document
import org.w3c.dom.*
import org.w3c.dom.events.Event
import taack.ui.base.BaseElement
import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.base.leaf.TableRowLink

class TableRow(val parent: Table, val r: HTMLTableRowElement) :
        BaseElement {
    companion object {
        fun getSiblingRows(p: Table): List<TableRow> {
            val elements: List<Node>?
            elements = p.t.querySelectorAll("tr.taackTableRow").asList()
            return elements.map {
                TableRow(p, it as HTMLTableRowElement)
            }
        }
    }

    private val rowLink: List<TableRowLink>
    private val rowGroup: Int? = r.attributes.getNamedItem("taackTableRowGroup")?.value?.toInt()
    private val rowGroupHasChildren: Boolean? = r.attributes.getNamedItem("taackTableRowGroupHasChildren")?.value?.toBoolean()
    private var isExpended: Boolean = false
    private val innerButt = document.createElement("button") as HTMLButtonElement

    private fun spanInnerText(): String {
        val output = StringBuilder()
//        for (i in 1..rowGroup!!) {
//            output.append("&nbsp&nbsp&nbsp&nbsp&nbsp")
//        }
        if (rowGroupHasChildren == true) {
            if (!isExpended) {
                output.append("""<b>+</b>&nbsp""")
            } else {
                output.append("""<b>-</b>&nbsp""")
            }
//        } else {
//            output.append("""&nbsp&nbsp""")
        }
        return output.toString()
    }


    init {
        traceIndent("TableRow::init +++ ${rowGroup ?: ""} ${rowGroupHasChildren ?: ""}")
        rowLink = TableRowLink.getSiblingTableRowLink(this)
        innerButt.type = "button"
        if (rowGroup != null) {
            val firstCell = r.firstElementChild!! as HTMLTableCellElement
            firstCell.classList.add("firstCellInGroup")
            firstCell.classList.add("firstCellInGroup-${rowGroup}")
            firstCell.style.paddingLeft = "${rowGroup}em !important"
            if (rowGroupHasChildren == true) {

                innerButt.innerHTML = spanInnerText()
                innerButt.onclick = {
                    onclick(it)
                }
                firstCell.insertAdjacentElement("afterbegin", innerButt)
            } else {
                val innerSpan = document.createElement("span") as HTMLSpanElement
                innerSpan.innerHTML = spanInnerText()
                firstCell.insertAdjacentElement("afterbegin", innerSpan)
            }
        }
        traceDeIndent("TableRow::init ---")
    }

    private fun expends() {
        isExpended = true
        var expends = false
        val rg = rowGroup!! + 1
        innerButt.innerHTML = spanInnerText()

        for (r in parent.rows) {
            if (expends && r.rowGroup == rg) {
                r.r.style.removeProperty("display")
            } else if (expends && r.rowGroup == rowGroup) {
                break
            }
            if (r === this) {
                expends = true
            }

        }
    }

    private fun collapse() {
        isExpended = false
        var collapse = false
        var rg = rowGroup!! + 1
        innerButt.innerHTML = spanInnerText()

        for (r in parent.rows) {
            if (collapse && r.rowGroup!! >= rg) {
                r.r.style.display = "none"
//                if (r.rowGroupHasChildren!!) r.collapse()
            } else if (collapse && r.rowGroup!! == rowGroup) {
                break
            }
            if (r === this) {
                collapse = true
            }
        }

    }

    private fun onclick(e: Event):Boolean {
        if (isExpended) collapse()
        else expends()
        return false
    }

    override fun getParentBlock(): Block {
        return parent.getParentBlock()
    }
}