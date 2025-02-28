package taack.ui.base.leaf

import js.array.asList
import kotlinx.browser.window
import taack.ui.base.LeafElement
import taack.ui.base.element.AjaxBlock
import taack.ui.base.element.Block
import web.dom.Document
import web.dom.document
import web.dom.parsing.DOMParser
import web.dom.parsing.DOMParserSupportedType
import web.events.EventHandler
import web.html.HTMLAnchorElement
import web.html.HTMLDivElement
import web.html.HTMLElement
import web.html.HTMLSpanElement
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
                        val maxX = window.innerWidth - contextMenu.offsetWidth
                        val maxY = window.innerHeight - contextMenu.offsetHeight
                        left = "${min(e.clientX, maxX)}px"
                        top = "${min(e.clientY, maxY)}px"
                        zIndex = "9999"
                    }
                    addIdToAnchors(response, id)
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


    private fun addIdToAnchors(doc: Document, id: Long) {
        doc.querySelectorAll("a").asList().forEach { anchor ->
            val anchorElement = anchor as HTMLAnchorElement
            val currentHref = anchorElement.href
            if (currentHref.isNotEmpty()) {
                anchorElement.href = if (currentHref.contains("?")) {
                    "${currentHref}&id=$id&isAjax=true"
                } else {
                    "${currentHref}?id=$id&isAjax=true"
                }
            }
        }
    }
}