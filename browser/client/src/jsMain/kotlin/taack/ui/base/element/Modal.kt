package taack.ui.base.element

import kotlinx.browser.document
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import taack.ui.base.BaseElement
import taack.ui.base.Helper
import taack.ui.base.Helper.Companion.RELOAD
import taack.ui.base.Helper.Companion.processAjaxLink
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import web.cssom.ClassName
import web.dom.ElementId
import web.events.EventHandler
import web.events.EventType
import web.events.addEventListener
import web.html.ButtonType
import web.html.HTMLButtonElement
import web.html.HTMLDivElement
import web.html.button
import web.uievents.MouseEvent
import web.window.window

class Modal(val parent: Block, htmlContent: String) : BaseElement {
    companion object {
        fun buildModal(p: Block, htmlContent: String): Modal {
            return Modal(p, htmlContent)
        }

        var id: Int = 0

        private const val SVG_MINIMIZE = """<svg width="16" height="16" fill="currentColor" viewBox="0 0 16 16"><path d="M0 8a1 1 0 0 1 1-1h14a1 1 0 1 1 0 2H1a1 1 0 0 1-1-1z"/></svg>"""
        private const val SVG_MAXIMIZE = """<svg width="16" height="16" fill="currentColor" viewBox="0 0 16 16"><path d="M1.5 1a.5.5 0 0 0-.5.5v4a.5.5 0 0 1-1 0v-4A1.5 1.5 0 0 1 1.5 0h4a.5.5 0 0 1 0 1h-4zM10 .5a.5.5 0 0 1 .5-.5h4A1.5 1.5 0 0 1 16 1.5v4a.5.5 0 0 1-1 0v-4a.5.5 0 0 0-.5-.5h-4a.5.5 0 0 1-.5-.5zM.5 10a.5.5 0 0 1 .5.5v4a.5.5 0 0 0 .5.5h4a.5.5 0 0 1 0 1h-4A1.5 1.5 0 0 1 0 14.5v-4a.5.5 0 0 1 .5-.5zm15 0a.5.5 0 0 1 .5.5v4a1.5 1.5 0 0 1-1.5 1.5h-4a.5.5 0 0 1 0-1h4a.5.5 0 0 0 .5-.5v-4a.5.5 0 0 1 .5-.5z"/></svg>"""
        private const val SVG_RESTORE = """<svg width="16" height="16" fill="currentColor" viewBox="0 0 16 16"><path d="M5.5 0a.5.5 0 0 1 .5.5v4A1.5 1.5 0 0 1 4.5 6h-4a.5.5 0 0 1 0-1h4a.5.5 0 0 0 .5-.5v-4a.5.5 0 0 1 .5-.5zm5 0a.5.5 0 0 1 .5.5v4a.5.5 0 0 0 .5.5h4a.5.5 0 0 1 0 1h-4A1.5 1.5 0 0 1 10 4.5v-4a.5.5 0 0 1 .5-.5zM0 10.5a.5.5 0 0 1 .5-.5h4A1.5 1.5 0 0 1 6 11.5v4a.5.5 0 0 1-1 0v-4a.5.5 0 0 0-.5-.5h-4a.5.5 0 0 1-.5-.5zm10 1a1.5 1.5 0 0 1 1.5-1.5h4a.5.5 0 0 1 0 1h-4a.5.5 0 0 0-.5.5v4a.5.5 0 0 1-1 0v-4z"/></svg>"""
        private const val SVG_CLOSE = """<svg width="16" height="16" fill="currentColor" viewBox="0 0 16 16"><path d="M14 3.41L12.59 2 8 6.59 3.41 2 2 3.41 6.59 8 2 12.59 3.41 14 8 9.41 12.59 14 14 12.59 9.41 8z"/></svg>"""
    }

    private var dModal: HTMLDivElement = document.createElement("div") as HTMLDivElement
    private var dModalDialog: HTMLDivElement
    var dModalBody: HTMLDivElement
    private val dModalContent = document.createElement("div") as HTMLDivElement
    private val closeButton: HTMLButtonElement
    private val escModalCallback = { event: Event ->
        if (event is KeyboardEvent) {
            val e = event
            if (e.key == "Escape" && dModal.closest("div.modal.tck-hidden") == null && dModalContent.querySelector("div.modal") == null) {
                e.preventDefault()
                close()
            }
        }
    }

    private val mId = id++

    init {
        traceIndent("Modal::open $mId")
        parent.modals.addLast(this)
        dModal.classList.add(ClassName("modal"))
        dModal.style.display = "block"
        dModalDialog = document.createElement("div") as HTMLDivElement
        dModalDialog.classList.add(ClassName("modal-dialog"), ClassName("modal-xl"), ClassName("modal-dialog-scrollable"), ClassName("modal-dialog-centered"))
        dModalBody = document.createElement("div") as HTMLDivElement
        dModalBody.classList.add(ClassName("modal-body"), ClassName("overflow-y-auto"))
        dModalBody.innerHTML = htmlContent
        dModalContent.classList.add(ClassName("modal-content"))
        dModalContent.classList.add(ClassName("taackModal"))
        val minimizeButton = document.createElement("button") as HTMLButtonElement
        minimizeButton.type = ButtonType.button
        minimizeButton.classList.add(ClassName("btn"), ClassName("btn-taack-minimize"))
        minimizeButton.innerHTML = SVG_MINIMIZE
        minimizeButton.onclick = EventHandler { e ->
            e.preventDefault()
            minimize()
        }
        minimizeButton.onmousedown = EventHandler { e ->
            e.stopPropagation()
        }
        val fullscreenButton = document.createElement("button") as HTMLButtonElement
        fullscreenButton.type = ButtonType.button
        fullscreenButton.classList.add(ClassName("btn"), ClassName("btn-taack-fullscreen"))
        fullscreenButton.innerHTML = SVG_MAXIMIZE
        fullscreenButton.onclick = EventHandler { e ->
            e.preventDefault()
            trace("Modal::fullscreen $mId")
            dModalContent.removeAttribute("style")
            if (dModalDialog.classList.contains(ClassName("modal-fullscreen"))) {
                dModalDialog.classList.remove(ClassName("modal-fullscreen"))
                fullscreenButton.innerHTML = SVG_MAXIMIZE
            } else {
                dModalDialog.classList.add(ClassName("modal-fullscreen"))
                fullscreenButton.innerHTML = SVG_RESTORE
            }
        }
        fullscreenButton.onmousedown = EventHandler { e ->
            e.stopPropagation()
        }
        closeButton = document.createElement("button") as HTMLButtonElement
        closeButton.type = ButtonType.button
        closeButton.classList.add(ClassName("btn"), ClassName("btn-taack-close"))
        closeButton.innerHTML = SVG_CLOSE
        closeButton.onclick = EventHandler { e ->
            e.stopPropagation()
            e.preventDefault()
            close()
        }
        closeButton.onmousedown = EventHandler { e ->
            e.stopPropagation()
        }
        val dModalHeader = document.createElement("div") as HTMLDivElement
        dModalHeader.classList.add(ClassName("modal-header"))
        dModalHeader.appendChild(minimizeButton)
        dModalHeader.appendChild(fullscreenButton)
        dModalHeader.appendChild(closeButton)
        dModalHeader.onmouseover = EventHandler {
            dModalHeader.style.cursor = "move"
        }
        dModalContent.appendChild(dModalHeader)
        dModalContent.appendChild(dModalBody)
        dModalDialog.appendChild(dModalContent)
        dModal.appendChild(dModalDialog)
        parent.d.parentElement?.appendChild(dModal)
        val modalBackdrop = document.createElement("div") as HTMLDivElement
        modalBackdrop.id = ElementId("modal-backdrop-$mId")
        modalBackdrop.classList.add(ClassName("modal-backdrop"), ClassName("fade"), ClassName("show"))
        parent.d.parentElement?.appendChild(modalBackdrop)

        document.body!!.classList.add("modal-open")
        document.addEventListener("keydown", escModalCallback)
        enableModalDraggable(dModalHeader)
        Block.getSiblingBlock(this)
    }

    fun reloadPageWhenCloseModal() {
        closeButton.addEventListener(EventType("click"), fun(_) {
            processAjaxLink(null, RELOAD, parent)
        })
    }

    fun removeLastUrlWhenCloseModal() {
        closeButton.addEventListener(EventType("click"), fun(_) {
            if (Helper.urlStack.isNotEmpty()) Helper.urlStack.removeLast()
        })
    }

    fun close() {
        traceDeIndent("Modal::close $mId")
        parent.modals.remove(this)
        dModal.remove()
        document.removeEventListener("keydown", escModalCallback)
        document.body!!.classList.remove("modal-open")
        document.getElementById("modal-backdrop-$mId")?.remove()
    }

    private fun minimize() {
        var rootModal = this
        while (rootModal.getParentBlock().parent != null) {
            rootModal = rootModal.getParentBlock().parent!!
        }
        rootModal.dModal.classList.add(ClassName("tck-hidden"))
        document.getElementById("modal-backdrop-${rootModal.mId}")?.classList?.add("tck-hidden")
        document.body!!.classList.remove("modal-open")

        val minimizeItem = document.createElement("button") as HTMLButtonElement
        minimizeItem.id = ElementId("modal-minimize-item-$mId")
        minimizeItem.classList.add(ClassName("btn"), ClassName("btn-primary"))
        minimizeItem.innerHTML = dModalBody.querySelector("ul[taacktag='LABEL']")?.textContent ?: "Modal $mId"
        minimizeItem.onclick = EventHandler { e ->
            e.preventDefault()
            rootModal.dModal.classList.remove(ClassName("tck-hidden"))
            document.getElementById("modal-backdrop-${rootModal.mId}")?.classList?.remove("tck-hidden")
            minimizeItem.remove()
            document.body!!.classList.add("modal-open")
        }
        document.getElementById("taack-modal-minimize-items")!!.append(minimizeItem)
    }

    private fun enableModalDraggable(header: HTMLDivElement) {
        var isDragging = false
        var startX = 0
        var startY = 0
        var maxLeft = 0.0
        var maxRight = 0.0
        var maxTop = 0.0
        var maxBottom = 0.0
        var initialOffsetLeft = 0.0
        var initialOffsetTop = 0.0
        header.addEventListener(EventType("mousedown"), EventHandler { e: MouseEvent ->
            if (!dModalDialog.classList.contains(ClassName("modal-fullscreen"))) {
                isDragging = true
                startX = e.clientX
                startY = e.clientY
                maxLeft = dModalContent.getBoundingClientRect().left
                maxRight = window.innerWidth - dModalContent.getBoundingClientRect().right
                maxTop = dModalContent.getBoundingClientRect().top
                maxBottom = window.innerHeight - dModalContent.getBoundingClientRect().bottom
                initialOffsetLeft = dModalContent.offsetLeft.toDouble()
                initialOffsetTop = dModalContent.offsetTop.toDouble()
                web.dom.document.body.style.userSelect = "none"
            }
        })
        web.dom.document.addEventListener(EventType("mousemove"), EventHandler { e: MouseEvent ->
            if (!isDragging) return@EventHandler
            val dx = e.clientX - startX
            val dy = e.clientY - startY

            dModalContent.style.position = "absolute"
            dModalContent.style.left = "${initialOffsetLeft + dx}px"
            dModalContent.style.top = "${initialOffsetTop + dy}px"
        })
        web.dom.document.addEventListener(EventType("mouseup"), EventHandler { e: MouseEvent ->
            if (isDragging) {
                val dx = e.clientX - startX
                val dy = e.clientY - startY
                if (dx > maxRight) {
                    dModalContent.style.left = "${initialOffsetLeft + maxRight}px"
                } else if (dx < -maxLeft) {
                    dModalContent.style.left = "${initialOffsetLeft - maxLeft}px"
                }

                if (dy > maxBottom) {
                    dModalContent.style.top = "${initialOffsetTop + maxBottom}px"
                } else if (dy < -maxTop) {
                    dModalContent.style.top = "${initialOffsetTop - maxTop}px"
                }
            }
            isDragging = false
            web.dom.document.body.style.userSelect = ""
        })
    }

    override fun getParentBlock(): Block {
        return parent
    }

    override fun toString(): String {
        return "Modal"
    }
}