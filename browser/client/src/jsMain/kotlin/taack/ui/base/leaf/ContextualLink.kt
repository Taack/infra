package taack.ui.base.leaf

import js.array.asList
import js.uri.encodeURIComponent
import kotlinx.browser.window
import taack.ui.base.LeafElement
import taack.ui.base.element.AjaxBlock
import taack.ui.base.element.Block
import taack.ui.base.element.Filter
import web.dom.Document
import web.dom.document
import web.dom.parsing.DOMParser
import web.dom.parsing.DOMParserSupportedType
import web.events.EventHandler
import web.form.FormData
import web.html.*
import web.http.RequestMethod
import web.xhr.XMLHttpRequest
import kotlin.math.min

class ContextualLink(private val parent: Block, a: HTMLSpanElement, className: String, fieldName: String, id: Long) : LeafElement {
    companion object {
        fun getContextualLinks(p: AjaxBlock): List<ContextualLink>? {
            val elements: List<*> = p.d.querySelectorAll("span[taackcontextualmenu]").asList()
            if (elements.isEmpty()) return emptyList()
            return elements.map {
                (it as HTMLSpanElement).getAttribute("taackcontextualmenu")?.let { attribute ->
                    val (className, fieldName, idValue) = attribute.split(";")
                    ContextualLink(p.parent, it, className, fieldName, idValue.toLong())
                }!!
            }
        }

        fun getContextualLinks(p: Block): List<ContextualLink> {
            val elements: List<*> = document.querySelectorAll("body>nav span[taackcontextualmenu]").asList()
            if (elements.isEmpty()) return emptyList()
            return (elements + p.d.querySelectorAll("div[blockId]>nav span[taackcontextualmenu]").asList()).map {
                (it as HTMLSpanElement).getAttribute("taackcontextualmenu")?.let { attribute ->
                    val (className, fieldName, idValue) = attribute.split(";")
                    ContextualLink(p, it, className, fieldName, idValue.toLong())
                }!!
            }
        }
    }

    private var activeContextMenu: HTMLDivElement? = null
    private val contextCloseCallback = EventHandler {
        closeContextMenu()
    }
    private fun closeContextMenu() {
        console.log("close")
        activeContextMenu?.let { document.body.removeChild(it) }
        (document.asDynamic()).removeEventListener("click", contextCloseCallback)
        activeContextMenu = null
    }

    private fun buildCopyDropdown(a: HTMLElement): HTMLElement? {
        val dropdownItem = document.createElement("li")
        var hasCopy = false
        dropdownItem.textContent = "Copy"
        dropdownItem.className = "context-has-dropdown"
        val nestedUl = document.createElement("ul")
        nestedUl.className = "custom-context-menu nested-dropdown"
        if (!a.textContent.isNullOrEmpty()) {
            val copy = document.createElement("li")
            copy.textContent = "Copy text"
            copy.onclick = EventHandler { _ ->
                a.textContent?.let { text ->
                    window.navigator.clipboard.writeText(text)
                }
            }
            nestedUl.append(copy)
            hasCopy = true
        }
        val id = a.getAttribute("ajaxaction")?.split('/')?.last()?.replace("?isAjax=true", "")
        if (id?.all { it.isDigit() } == true) {
            val copyId = document.createElement("li")
            copyId.textContent = "Copy ID"
            copyId.onclick = EventHandler { _ ->
                window.navigator.clipboard.writeText(id)
            }
            nestedUl.append(copyId)
            hasCopy = true
        }
        if (!hasCopy) return null
        dropdownItem.append(nestedUl)
        dropdownItem.onmouseenter = EventHandler {
            val rect = dropdownItem.getBoundingClientRect()
            val potentialRightEdge = rect.right + nestedUl.offsetWidth
            if (potentialRightEdge > window.innerWidth) {
                nestedUl.classList.add("flip-left")
            } else {
                nestedUl.classList.remove("flip-left")
            }
        }
        return dropdownItem
    }

    init {
        a.oncontextmenu = EventHandler { e ->
            e.preventDefault() //Block default context menu
            closeContextMenu() // Close context if already opened
            val xhr = XMLHttpRequest()
            xhr.open(RequestMethod.GET, "/taackContextMenu/index?className=$className&fieldName=$fieldName&id=$id", true)
            xhr.onload = EventHandler {
                if (xhr.status == 200.toShort()) {
                    val parser = DOMParser()
                    val response = parser.parseFromString(xhr.responseText, DOMParserSupportedType.textHtml)
                    buildCopyDropdown(a)?.let { response.querySelector("ul")?.append(it) }
                    val contextMenu = document.createElement("div") as HTMLDivElement
                    contextMenu.className = "custom-context-menu"
                    contextMenu.style.apply {
                        position = "absolute"
                        document.body.appendChild(contextMenu)
                        val maxX = window.innerWidth + window.scrollX - contextMenu.offsetWidth
                        val maxY = window.innerHeight + window.scrollY - contextMenu.offsetHeight
                        left = "${min(e.pageX, maxX)}px"
                        top = "${min(e.pageY, maxY)}px"
                        zIndex = "9999"
                    }
                    addIdToAnchors(response, id, getFilterFormDataOrNull(a))
                    contextMenu.append(response.body)
                    activeContextMenu = contextMenu
                    document.body.appendChild(activeContextMenu!!)
                    (document.asDynamic()).addEventListener("click", contextCloseCallback)
                    activeContextMenu?.onmouseleave = EventHandler {
                        closeContextMenu()
                    }
                    ContextualLinkEntry.getDropdownMenu(parent)
                }
            }
            xhr.send()
        }
    }


    private fun addIdToAnchors(doc: Document, id: Long, formData: FormData?) {
        doc.querySelectorAll("a").asList().forEach { anchor ->
            val anchorElement = anchor as HTMLAnchorElement
            val currentHref = anchorElement.href
            if (currentHref.isNotEmpty()) {
                anchorElement.href = if (currentHref.contains("?")) {
                    "${currentHref}&id=$id&isAjax=true"
                } else {
                    "${currentHref}?id=$id&isAjax=true"
                }
                formData?.forEach { value, key ->
                    anchorElement.href = "${anchorElement.href}&$key=${encodeURIComponent(value.toString())}"
                }
            }
        }
    }

    private fun getFilterFormDataOrNull(span: HTMLSpanElement): FormData? { // If span is in a table with filter, return the formData of the filter
        var formData: FormData? = null
        val t = span.closest("table[taacktableid]") as HTMLTableElement?
        val div = span.closest("div[ajaxblockid]") as HTMLDivElement?
        if (t != null && div != null) {
            val tId = t.getAttribute("taacktableid")!!
            val ajaxBlockId = div.getAttribute("ajaxblockid")!!
            if (t.nextElementSibling == null) { // Exclude the case where the table has no filter
                val filter: Filter? = parent.ajaxBlockElements[ajaxBlockId]?.filters?.get(tId + ajaxBlockId)
                if (filter != null) {
                    formData = FormData(filter.f)
                    formData["offset"] = "0"
                    formData["refresh"] = "true"
                    formData["filterTableId"] = filter.filterId
                    formData["ajaxBlockId"] = filter.parent.blockId
                }
            }
        }
        return formData
    }
}