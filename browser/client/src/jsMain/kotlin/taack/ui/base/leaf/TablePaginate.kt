package taack.ui.base.leaf

import taack.ui.base.Helper
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.LeafElement
import taack.ui.base.element.Table
import web.dom.document
import web.events.EventHandler
import web.html.*
import web.uievents.MouseEvent
import kotlin.math.max
import kotlin.math.min

class TablePaginate(private val parent: Table, d: HTMLDivElement) : LeafElement {
    companion object {
        fun getSiblingTablePaginate(p: Table): TablePaginate? {
            val d = p.t.parentElement!!.querySelector("div[taackmax]")
            if (d != null) {
                try {
                    return TablePaginate(p, d as HTMLDivElement)
                } catch (e: Throwable) {
                    trace("Exception in TablePaginate")
                    trace(e.message?:"No message")
                    trace("table: $p")
                    trace("div: $d")
                }
            }
            return null
        }
    }

    private val max: Number = d.attributes.getNamedItem("taackMax")!!.value.toLong()
    private val offset: Number = d.attributes.getNamedItem("taackOffset")?.value!!.toLong()
    private val count: Number = d.attributes.getNamedItem("taackCount")!!.value.toLong()
    private val currentPage = (offset.toDouble() / max.toDouble()).toInt()
    private val numberOfPage = (count.toDouble() / max.toDouble()).toInt()
    private val ul = document.createElement("ul") as HTMLUListElement

    init {
        trace("TablePaginate1 max: $max, offset: $offset, count: $count")
        trace("TablePaginate2 currentPage: $currentPage, numberOfPage: $numberOfPage")

        val nav = document.createElement("nav")
        ul.classList.add("pagination")
        ul.classList.add("pagination-sm")
        nav.appendChild(ul)
        if (numberOfPage <= 1) {
            val f = count.toDouble() / max.toDouble()
            if (f > 1) {
                createAnchor(0)
                createAnchor(1)
            }
        } else if (numberOfPage in 2..9) {
            for (i in 0..numberOfPage) {
                createAnchor(i)
            }
        } else if (numberOfPage in 10..19) {
            createAnchor(0)
            for (i in 1..(if (currentPage < 5) 5 else 3)) {
                createAnchor(i)
            }
            appendSpan()
            if (currentPage >= 5 && currentPage <= numberOfPage - 5) {
                for (i in (currentPage - 2)..(currentPage + 2)) {
                    createAnchor(i)
                }
                appendSpan()
            }
            for (i in (numberOfPage - (if (currentPage <= numberOfPage - 5) 5 else 3))..numberOfPage) {
                createAnchor(i)
            }
            createAnchor(numberOfPage)
        } else {
            createAnchor(0)
            if (currentPage > 2) appendSpan()
            val minInterval = 20

            var i1 = numberOfPage / 4
            var i2 = numberOfPage / 2
            var i3 = 3 * numberOfPage / 4

            if (currentPage < minInterval) {
                i1 = max(currentPage, 2)
            } else if (currentPage < i1) {
                i1 = currentPage / 2
                i2 = currentPage
                i3 = (numberOfPage - currentPage) / 2
            } else if (currentPage < i2) {
                i1 = currentPage / 2
                i2 = currentPage
                i3 = 3 * currentPage / 2
            } else if (currentPage < i3) {
                i1 = currentPage / 2
                i2 = currentPage
                i3 = currentPage + (numberOfPage - currentPage) / 2
            } else if (currentPage < numberOfPage - minInterval) {
                i3 = min(currentPage - 3, numberOfPage - 3)
            }
            for (i in i1 - 1..i1 + 1) {
                createAnchor(i)
            }
            appendSpan()
            for (i in i2 - 1..i2 + 1) {
                createAnchor(i)
            }
            appendSpan()
            for (i in i3 - 1..i3 + 1) {
                createAnchor(i)
            }
            appendSpan()
            for (i in (numberOfPage - 2) until numberOfPage) {
                createAnchor(i)
            }
            createAnchor(numberOfPage)
        }
        d.appendChild(nav)
    }

    private fun appendSpan() {
        trace("appendSpan")
        val s = document.createElement("span") as HTMLSpanElement
        s.innerText = " ... "
        ul.appendChild(s)
    }

    private fun createAnchor(pageOffset: Int) {
        trace("createAnchor $pageOffset")
        val li = document.createElement("li") as HTMLLIElement
        li.classList.add("page-item")
        val a = document.createElement("a") as HTMLAnchorElement
        a.innerText = " ${pageOffset + 1} "
        a.classList.add("taackPageOffset")
        a.classList.add("page-link")
        if (pageOffset == currentPage) {
            a.style.fontWeight = "bold"
            li.classList.add("active")
        }
        a.setAttribute("taackPageOffset", pageOffset.toString())
        a.onclick = EventHandler { e ->
            onClick(e, a)
        }
        li.appendChild(a)
        ul.appendChild(li)
    }

    private fun onClick(e: MouseEvent, a: HTMLAnchorElement) {
        e.preventDefault()
        val offset = (a.attributes.getNamedItem("taackPageOffset")!!.value.toDouble() * max.toDouble()).toInt()
        Helper.filterForm(parent.filter, offset, null)
    }
}