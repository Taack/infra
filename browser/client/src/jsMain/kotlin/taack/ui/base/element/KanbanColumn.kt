package taack.ui.base.element

import js.array.asList
import taack.ui.base.BaseElement
import taack.ui.base.Helper
import taack.ui.base.Helper.Companion.checkLogin
import taack.ui.base.Helper.Companion.processAjaxLink
import web.cssom.ClassName
import web.dom.ElementId
import web.dom.document
import web.events.EventHandler
import web.form.FormData
import web.html.HTMLButtonElement
import web.html.HTMLDivElement
import web.html.HtmlSource
import web.http.POST
import web.http.RequestMethod
import web.xhr.XMLHttpRequest
import web.location.location
import taack.ui.base.leaf.BaseAjaxAction.Companion.createUrl
import kotlin.math.min

class KanbanColumn(val parent: Kanban, val d: HTMLDivElement):
    BaseElement {
    companion object {
        fun getSiblingKanbanColumn(p: Kanban): List<KanbanColumn> {
            val elements: List<*> = p.d.querySelectorAll("div.kanban-column").asList()
            return elements.map {
                KanbanColumn(p, it as HTMLDivElement)
            }
        }
    }

    private var kanbanCards: List<KanbanCard>?
    private val dropAction = d.attributes.getNamedItem("taackDropAction")!!.value
    private val columnIndex = d.attributes.getNamedItem("kanbanColumnIndex")!!.value
    private val closeButton = if (d.getElementsByClassName(ClassName("close-btn")).length > 0) d.getElementsByClassName(ClassName("close-btn"))[0] else null

    init {
        Helper.traceIndent("KanbanColumn::init +++")
        kanbanCards = KanbanCard.getSiblingCard(this)
        if (dropAction != "") {
            d.ondragover = EventHandler { e ->
                e.preventDefault()
                if (parent.sourceColumn != null && parent.sourceColumn != this) {
                    d.classList.add(ClassName("drag-over"))
                }
            }
            d.ondragleave = EventHandler {
                d.classList.remove(ClassName("drag-over"))
            }
            d.ondrop = EventHandler { e ->
                e.preventDefault()
                if (parent.sourceColumn != null && parent.sourceColumn != this) {
                    d.classList.remove(ClassName("drag-over"))
                    d.classList.add(ClassName("kanban-loading"))
                    var fd: FormData? = getFilterFormDataOrNull(d)
                    val hasExistingFormData = (fd != null)
                    if (fd == null) {
                        fd = FormData()
                        fd.set("isAjax", "true")
                    }
                    fd.set("cardId", parent.draggedItem?.cardId ?: "")
                    val xhr = XMLHttpRequest()
                    xhr.onreadystatechange = EventHandler {
                        if (xhr.readyState == xhr.DONE) {
                            checkLogin(xhr)
                            if (hasExistingFormData) {
                                d.classList.remove(ClassName("kanban-loading"))
                                Helper.filterForm(parent.filter, null, null)
                            } else {
                                if (xhr.status == 200.toShort()) {
                                    e.preventDefault()
                                    val text = xhr.responseText
                                    if (text.substring(0, min(20, text.length)).contains(Regex(" html"))) {
                                        location.href = xhr.responseURL
                                        document.textContent = text
                                        document.close()
                                    } else {
                                        processAjaxLink(createUrl(true, dropAction), text, parent)
                                    }
                                }
                            }
                        } else if (xhr.readyState == xhr.OPENED) {
                            d.classList.remove(ClassName("kanban-loading"))
                        }
                    }
                    xhr.open(RequestMethod.POST, dropAction)
                    xhr.send(fd)
                }
            }
        }
        if (closeButton != null) {
            (closeButton as HTMLButtonElement).onclick = EventHandler { e ->
                d.style.display = "none"
                val headerStr = d.querySelector(".kanban-column-header > div")?.textContent?.trim()
                if (headerStr != null) {
                    val hiddenArea = getOrCreateHiddenColumn()
                    hiddenArea.style.display = "block"
                }
            }
        }
        d.oncontextmenu = EventHandler { e -> e.preventDefault() }
        Helper.traceDeIndent("KanbanColumn::init ---")
    }

    private fun getFilterFormDataOrNull(d: HTMLDivElement): FormData? { // If div is in a kanban with filter, return the formData of the filter
        var formData: FormData? = null
        val t = d.closest("[taackkanbanid]")
        val div = d.closest("div[ajaxblockid]") as HTMLDivElement?
        if (t != null && div != null) {
            val tId = t.getAttribute("taackkanbanid")
            val ajaxBlockId = div.getAttribute("ajaxblockid")!!
            if (t.nextElementSibling == null) { // Exclude the case where the kanban has no filter
                val filter: Filter? = getParentBlock().ajaxBlockElements[ajaxBlockId]?.filters?.get(tId + ajaxBlockId)
                if (filter != null) {
                    formData = FormData(filter.f)
                    formData.set("refresh", "true")
                    formData.set("filterTableId", filter.filterId)
                    formData.set("ajaxBlockId", filter.parent.blockId)
                }
            }
        }
        return formData
    }

    override fun getParentBlock(): Block {
        return parent.getParentBlock()
    }

    fun getOrCreateHiddenColumn(): HTMLDivElement {
        var hiddenArea = parent.d.querySelector("#kanban-hidden-columns") as HTMLDivElement?
        if (hiddenArea == null) {
            hiddenArea = document.createElement("div") as HTMLDivElement
            hiddenArea.classList.add(ClassName("kanban-column"), ClassName("col"), ClassName("m-2"))
            hiddenArea.id = ElementId("kanban-hidden-columns")
            hiddenArea.innerHTML = HtmlSource("<div class='kanban-column-header'>Hidden columns</div><div id='kanban-hidden-list'></div>")
            parent.d.appendChild(hiddenArea)
        }
        val hiddenList = hiddenArea.querySelector("#kanban-hidden-list")!!
        val columnItem = document.createElement("div") as HTMLDivElement
        val headerStr = d.querySelector(".kanban-column-header > div")?.textContent?.trim()
        val count: Int = kanbanCards?.size ?: 0
        columnItem.classList.add(ClassName("kanban-hidden-column"))
        columnItem.innerHTML = HtmlSource("$headerStr<span>$count</span>")
        columnItem.onclick = EventHandler {
            val column = parent.d.querySelector("[kanbanColumnIndex=${columnIndex}]") as HTMLDivElement?
            if (column != null) {
                column.style.display = "block"
                columnItem.remove()
                if (hiddenList.children.length == 0) {
                    hiddenArea.remove()
                }
            }
        }
        hiddenList.appendChild(columnItem)
        return hiddenArea
    }
}