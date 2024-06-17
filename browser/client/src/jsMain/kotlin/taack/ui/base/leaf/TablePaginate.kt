package taack.ui.base.leaf

import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.dom.addClass
import org.w3c.dom.*
import org.w3c.dom.events.MouseEvent
import org.w3c.fetch.RequestInit
import org.w3c.xhr.FormData
import taack.ui.base.Helper
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.LeafElement
import taack.ui.base.element.AjaxBlock
import taack.ui.base.element.Table
import kotlin.js.Promise
import kotlin.math.max
import kotlin.math.min

class TablePaginate(private val parent: Table, private val d: HTMLDivElement) : LeafElement {
    companion object {
        fun getSiblingTablePaginate(p: Table): TablePaginate? {
            val d = p.t.parentElement!!.querySelector("div.taackTablePaginate")
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

    private val max: Number = d.attributes["taackMax"]!!.value.toLong()
    private val offset: Number = d.attributes["taackOffset"]!!.value.toLong()
    private val count: Number = d.attributes["taackCount"]!!.value.toLong()
    private val currentPage = (offset.toDouble() / max.toDouble()).toInt()
    private val numberOfPage = (count.toDouble() / max.toDouble()).toInt()
    private val ul = document.createElement("ul") as HTMLUListElement

    init {
        trace("TablePaginate1 max: $max, offset: $offset, count: $count")
        trace("TablePaginate2 currentPage: $currentPage, numberOfPage: $numberOfPage")

        val nav = document.createElement("nav") as HTMLElement
        ul.addClass("pagination")
        ul.addClass("pagination-sm")
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
            appendSpan()
            val minInterval = 20

            var i1 = numberOfPage / 4
            var i2 = numberOfPage / 2
            var i3 = 3 * numberOfPage / 4

            if (currentPage < minInterval) {
                i1 = max(currentPage, 3)
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
            for (i in i1 - 2..i1 + 2) {
                createAnchor(i)
            }
            appendSpan()
            for (i in i2 - 2..i2 + 2) {
                createAnchor(i)
            }
            appendSpan()
            for (i in i3 - 2..i3 + 2) {
                createAnchor(i)
            }
            appendSpan()
            for (i in (numberOfPage - 5) until numberOfPage) {
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
        li.addClass("page-item")
        val a = document.createElement("a") as HTMLAnchorElement
        a.innerText = " ${pageOffset + 1} "
        a.addClass("taackPageOffset")
        a.addClass("page-link")
        if (pageOffset == currentPage) {
            a.style.fontWeight = "bold"
            li.addClass("active")
        }
        a.setAttribute("taackPageOffset", pageOffset.toString())
        a.onclick = {
            onClick(it, a)
        }
        li.appendChild(a)
        ul.appendChild(li)
    }

    private fun onClick(e: MouseEvent, a: HTMLAnchorElement) {
        e.preventDefault()
        trace("SortableColumn::onClick")
        val f = parent.filter.f
        val fd = FormData(f)
        fd.append("isAjax", "true")
        val offset = (a.attributes["taackPageOffset"]!!.value.toDouble() * max.toDouble()).toLong().toString()
        fd.set("offset", offset)
        fd.append("refresh", "true")
        fd.append("filterTableId", parent.parent.blockId)
        val b = f.querySelector("button#filter") as HTMLButtonElement?
        window.fetch(b?.formAction ?: f.action, RequestInit(method = "POST", body = fd)).then {
            if (it.ok) {
                it.text()
            } else {
                trace(it.statusText)
                Promise.reject(Throwable())
            }
        }.then {
            Helper.mapAjaxBlock(it).map { me ->
                val target = parent.parent.parent.ajaxBlockElements?.get(me.key)
                target!!.d.innerHTML = me.value
            }
        }.then {
            AjaxBlock.getSiblingAjaxBlock(parent.parent.parent)
        }
    }
}