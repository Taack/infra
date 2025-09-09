package taack.ui.base.element

import js.array.asList
import kotlinx.browser.document
import taack.ui.base.BaseElement
import taack.ui.base.Helper
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import web.events.EventHandler
import web.html.HTMLTableCellElement
import web.html.HTMLTableRowElement
import web.xhr.XMLHttpRequest
import kotlin.math.min

class TableRowCell(val parent: TableRow, private val c: HTMLTableCellElement) :
    BaseElement {
    override fun getParentBlock(): Block {
        return parent.getParentBlock()
    }

    companion object {
        fun getSiblingRowCells(p: TableRow): List<TableRowCell> {
            val elements: List<*> = p.r.querySelectorAll("td[taackDropAction]").asList()
            return elements.map {
                TableRowCell(p, it as HTMLTableCellElement)
            }
        }
    }

    private val dropAction = c.attributes.getNamedItem("taackDropAction")?.value

    init {
        if (dropAction != null) {
            c.ondragover = EventHandler { e ->
                e.preventDefault()
            }

            c.ondrop = EventHandler { e ->
                trace("Drag something on the cell")
                Helper.ondrop(e, dropAction, { xhr: XMLHttpRequest ->
                    val t = xhr.responseText
                    if (t.substring(0, min(20, t.length)).contains("<!DOCTYPE html>", false)) {
                        document.write(t)
                        document.close()
                    } else {
                        Helper.processAjaxLink(null, t, parent)
                    }

                })
            }
        }
    }
}