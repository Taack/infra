package taack.ui.base.leaf

import taack.ui.base.Helper
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.LeafElement
import taack.ui.base.element.Table
import web.cssom.ClassName
import web.dom.document
import web.events.EventHandler
import web.history.history
import web.html.*
import web.location.location
import web.url.URL
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

    private val max: Int = d.attributes.getNamedItem("taackMax")!!.value.toInt()
    private val offset: Int = d.attributes.getNamedItem("taackOffset")?.value?.toInt() ?: 0
    private val count: Int = d.attributes.getNamedItem("taackCount")!!.value.toInt()
    private val currentPage = offset / max
    private val numberOfPage = count / max - (if (count % max == 0) 1 else 0)
    private val ul = document.createElement("ul") as HTMLUListElement
    private val n = 5 // number of items in start/end section (If large of pages)

    init {
        trace("TablePaginate1 max: $max, offset: $offset, count: $count")
        trace("TablePaginate2 currentPage: $currentPage, numberOfPage: $numberOfPage")

        val nav = document.createElement("nav")
        ul.classList.add(ClassName("pagination"))
        ul.classList.add(ClassName("pagination-sm"))
        nav.appendChild(ul)
        if (numberOfPage > 0) {
            if (numberOfPage < n + (n * 2 - 1) + n) {
                for (i in 0..numberOfPage) {
                    appendAnchor(i)
                }
            } else {
                val startSectionStart = 0
                val startSectionEnd = n - 1
                val endSectionStart = numberOfPage - n + 1
                val endSectionEnd = numberOfPage
                val middleSectionCenter = min(endSectionStart - 1 - (n - 1), max(currentPage, startSectionEnd + 1 + (n - 1)))
                val middleSectionStart = middleSectionCenter - (n - 1)
                val middleSectionEnd = middleSectionCenter + (n - 1)

                // start section
                for (i in startSectionStart .. startSectionEnd) {
                    appendAnchor(i)
                }

                // left omitted section (Could have a link to redirect to the median)
                appendOmittedSection(if (middleSectionStart - startSectionEnd > 1) (middleSectionStart + startSectionEnd) / 2 else null)

                // middle section
                for (i in middleSectionStart .. middleSectionEnd) {
                    appendAnchor(i)
                }

                // right omitted section (Could have a link to redirect to the median)
                appendOmittedSection(if (endSectionStart - middleSectionEnd > 1) (endSectionStart + middleSectionEnd) / 2 else null)

                // right section
                for (i in endSectionStart .. endSectionEnd) {
                    appendAnchor(i)
                }
            }
        }
        d.appendChild(nav)
    }

    private fun appendAnchor(pageOffset: Int) {
        trace("createAnchor $pageOffset")
        val li = document.createElement("li") as HTMLLIElement
        li.classList.add(ClassName("page-item"))
        if (pageOffset == currentPage) {
            li.style.fontWeight = "bold"
            li.classList.add(ClassName("active"))
        }
        li.appendChild(createAnchor(pageOffset))
        ul.appendChild(li)
    }

    private fun createAnchor(pageOffset: Int): HTMLAnchorElement {
        val a = document.createElement("a") as HTMLAnchorElement
        a.innerText = " ${pageOffset + 1} "
        a.classList.add(ClassName("taackPageOffset"))
        a.classList.add(ClassName("page-link"))
        a.setAttribute("taackPageOffset", pageOffset.toString())
        a.onclick = EventHandler { e ->
            e.preventDefault()
            val offset = (a.attributes.getNamedItem("taackPageOffset")!!.value.toDouble() * max.toDouble()).toInt()
            Helper.filterForm(parent.filter, offset, null)
            val offsetUrl = URL(location.href)
            offsetUrl.searchParams.set("offset", offset.toString())
            history.pushState(null, "", offsetUrl)
        }
        return a
    }

    private fun appendOmittedSection(medianPageOffset: Int?) {
        trace("appendSpan")
        val s: HTMLSpanElement
        if (medianPageOffset != null) {
            s = document.createElement("span") as HTMLSpanElement
            s.style.display = "flex"
            s.style.justifyContent = "space-evenly"
            s.appendChild(createOmittedSpan())
            s.appendChild(createAnchor(medianPageOffset))
            s.appendChild(createOmittedSpan())
        } else {
            s = createOmittedSpan()
        }
        ul.appendChild(s)
    }

    private fun createOmittedSpan(): HTMLSpanElement {
        val s = document.createElement("span") as HTMLSpanElement
        s.innerText = " ... "
        return s
    }
}