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
    }

    private var dModal: HTMLDivElement = document.createElement("div") as HTMLDivElement
    private var dModalDialog: HTMLDivElement
    var dModalBody: HTMLDivElement
    private val dModalContent = document.createElement("div") as HTMLDivElement
    private val closeButton: HTMLButtonElement
    private val escModalCallback = { event: Event ->
        val e = event as KeyboardEvent
        if (e.key == "Escape" && dModal.closest("div.modal.tck-hidden") == null && dModalContent.querySelector("div.modal") == null) {
            e.preventDefault()
            close()
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
        minimizeButton.innerHTML = "&#128469;"
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
        fullscreenButton.innerHTML = "&#128470;"
        fullscreenButton.onclick = EventHandler { e ->
            e.preventDefault()
            trace("Modal::fullscreen $mId")
            dModalContent.removeAttribute("style")
            if (dModalDialog.classList.contains(ClassName("modal-fullscreen"))) {
                dModalDialog.classList.remove(ClassName("modal-fullscreen"))
                fullscreenButton.innerHTML = "&#128470;"
            } else {
                dModalDialog.classList.add(ClassName("modal-fullscreen"))
                fullscreenButton.innerHTML = "&#128471;"
            }
        }
        fullscreenButton.onmousedown = EventHandler { e ->
            e.stopPropagation()
        }
        closeButton = document.createElement("button") as HTMLButtonElement
        closeButton.type = ButtonType.button
        closeButton.classList.add(ClassName("btn"), ClassName("btn-taack-close"))
        closeButton.innerHTML = "&#128473;"
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
                header.style.cursor = "move"
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
            header.style.cursor = ""
        })
    }

    override fun getParentBlock(): Block {
        return parent
    }

    override fun toString(): String {
        return "Modal"
    }
}