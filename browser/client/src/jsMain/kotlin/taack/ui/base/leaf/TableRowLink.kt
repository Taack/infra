package taack.ui.base.leaf

import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.Node
import org.w3c.dom.asList
import taack.ui.base.element.TableRow

class TableRowLink(private val parent: TableRow, a: HTMLAnchorElement) : BaseAjaxAction(parent, a) {
    companion object {
        fun getSiblingTableRowLink(p: TableRow): List<TableRowLink> {
            val elements: List<Node>?
            elements = p.r.querySelectorAll("a.taackAjaxRowLink").asList()
            return elements.map {
                TableRowLink(p, it as HTMLAnchorElement)
            }
        }
    }
}

