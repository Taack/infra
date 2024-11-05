package taack.ui.base.leaf

import taack.ui.base.Helper
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.LeafElement
import taack.ui.base.element.AjaxBlock
import taack.ui.base.element.Table
import web.events.EventHandler
import web.form.FormData
import web.html.HTMLAnchorElement
import web.html.HTMLInputElement
import web.html.HTMLSpanElement
import web.http.RequestMethod
import web.uievents.MouseEvent
import web.xhr.XMLHttpRequest

class TableGroupableColumn(private val parent: Table, s: HTMLSpanElement) : LeafElement {
    companion object {
        fun getSiblingGroupableColumn(p: Table): List<TableGroupableColumn> {
            val elements: List<HTMLSpanElement> = p.t.querySelectorAll("span[groupField]") as List<HTMLSpanElement>
            return elements.map {
                TableGroupableColumn(p, it)
            }
        }
        var grouping = 0
    }

    private val property: String = s.attributes.getNamedItem("groupField")!!.value
    private val direction: String?
    private val groupCheck: HTMLInputElement = s.querySelector("input")!! as HTMLInputElement

    init {
        val fd = FormData(parent.filter.f)
        if (property == fd["sort"]) {
            direction = fd["order"] as String
        } else {
            direction = null
        }
        trace("TableGroupableColumn::init $property $direction")
        if (direction != null && direction != "") s.classList.add(direction)
        val a = s.childNodes[0] as HTMLAnchorElement
        a.onclick = EventHandler { e ->
            onClick(e)
        }

        groupCheck.checked = fd.get("grouping")!!.toString().contains(property)
        groupCheck.onclick = EventHandler{
            onGroup()
        }
    }

    private fun onGroup() {
        trace("TableGroupableColumn::onGroup")
        val f = parent.filter.f
        val fd = FormData(f)
        val g = fd["grouping"]!! as String
        val isGrouped = g.contains(property)
        if (!isGrouped) {
            fd["grouping"] = "$g $property".trim()
        } else {
            fd["grouping"] = g.replace(property, "").trim()
        }
        fd.append("isAjax", "true")
        val xhr = XMLHttpRequest()
        xhr.onloadend = EventHandler {
            Helper.mapAjaxBlock(xhr.responseText).map { me ->
                parent.parent.d.innerHTML = me.value
            }
            AjaxBlock.getSiblingAjaxBlock(parent.parent.parent)

        }
        xhr.open(RequestMethod.POST, "")
        xhr.send(fd)
    }

    private fun onClick(e: MouseEvent) {
        e.preventDefault()
        trace("TableGroupableColumn::onClick")
        val dir = if (direction == null || direction == "") "desc" else if (direction == "desc") "asc" else null
        Helper.filterForm(parent.filter, null, property, dir)
    }
}