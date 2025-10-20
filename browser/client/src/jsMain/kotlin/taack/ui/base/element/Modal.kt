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

class Modal(val parent: Block) : BaseElement {
    companion object {
        fun buildModal(p: Block): Modal {
            return Modal(p)
        }

        var id: Int = 0
    }

    private var dModal: HTMLDivElement
    private var dModalDialog: HTMLDivElement
    var dModalBody: HTMLDivElement
    private val dModalContent: HTMLDivElement
    private val closeButton: HTMLButtonElement
    private val escModalCallback = { event: Event ->
        val e = event as KeyboardEvent
        if (e.key == "Escape") {
            e.preventDefault()
            close()
        }
    }

    val mId = id++

    init {
        trace("Modal::init $mId")
        dModal = document.createElement("div") as HTMLDivElement
        dModal.classList.add(ClassName("modal"))
        dModalDialog = document.createElement("div") as HTMLDivElement
        dModalDialog.classList.add(ClassName("modal-dialog"), ClassName("modal-xl"), ClassName("modal-dialog-scrollable"), ClassName("modal-dialog-centered"))
        dModalContent = document.createElement("div") as HTMLDivElement
        dModalBody = document.createElement("div") as HTMLDivElement
        dModalBody.classList.add(ClassName("modal-body"), ClassName("overflow-y-auto"))
        dModalContent.classList.add(ClassName("modal-content"))
        dModalContent.classList.add(ClassName("taackModal"))
        val dClose = document.createElement("div") as HTMLDivElement
        val minimiseButton = document.createElement("button") as HTMLButtonElement
        minimiseButton.type = ButtonType.button
        minimiseButton.classList.add(ClassName("btn"), ClassName("btn-taack-minimise"))
        minimiseButton.innerHTML = "&#128469;"
        minimiseButton.onclick = EventHandler { e ->
            e.preventDefault()
            // todo
        }
        minimiseButton.onmousedown = EventHandler { e ->
            e.stopPropagation()
        }
        val fullscreenButton = document.createElement("button") as HTMLButtonElement
        fullscreenButton.type = ButtonType.button
        fullscreenButton.classList.add(ClassName("btn"), ClassName("btn-taack-fullscreen"))
        fullscreenButton.innerHTML = "&#128470;"
        fullscreenButton.onclick = EventHandler { e ->
            e.preventDefault()
            cleanModalPosition()
            trace("Modal::fullscreen $mId")
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
        document.addEventListener("keydown", escModalCallback)

        dClose.classList.add(ClassName("taack-close"))
        dClose.classList.add(ClassName("modal-header"))
        dClose.appendChild(minimiseButton)
        dClose.appendChild(fullscreenButton)
        dClose.appendChild(closeButton)
        dModalContent.appendChild(dClose)
        dModalContent.appendChild(dModalBody)
        dModalDialog.appendChild(dModalContent)
        dModal.appendChild(dModalDialog)
        parent.d.parentElement?.appendChild(dModal)
        enableModalDraggable(dClose)
    }

    fun open(htmlContent: String) {
        traceIndent("Modal::open $mId")
        dModalBody.innerHTML = htmlContent
        dModal.style.display = "block"
        Block.getSiblingBlock(this)
        document.body!!.classList.add("modal-open")
        document.body!!.style.paddingRight = "15px"
        document.body!!.style.overflowY = "hidden"
        val modalBackdrop = document.createElement("div") as HTMLDivElement
        modalBackdrop.id = ElementId("modal-backdrop-$mId")
        modalBackdrop.classList.add(ClassName("modal-backdrop"), ClassName("fade"), ClassName("show"))
        parent.d.parentElement!!.appendChild(modalBackdrop)
        cleanModalPosition()
    }

    fun reloadPageWhenCloseModal() {
        closeButton.onclick = EventHandler { e ->
            e.preventDefault()
            processAjaxLink(null, RELOAD, parent)
        }
    }

    fun removeLastUrlWhenCloseModal() {
        if (Helper.urlStack.isNotEmpty()) {
            closeButton.onclick = EventHandler { e ->
                e.preventDefault()
                close()
                Helper.urlStack.removeLast()
            }
        }
    }

    fun close() {
        traceDeIndent("Modal::close $mId")
        dModal.style.display = "none"
        dModalBody.innerHTML = ""
        if (dModalBody.nextElementSibling != null) dModalBody.nextElementSibling!!.remove()
        document.removeEventListener("keydown", escModalCallback)
        document.body!!.classList.remove("modal-open")
        document.body!!.style.removeProperty("padding-right")
        document.body!!.style.removeProperty("overflow-y")
        document.getElementById("modal-backdrop-$mId")?.remove()
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

    private fun cleanModalPosition() {
        dModalContent.removeAttribute("style")
    }

    override fun getParentBlock(): Block {
        return parent
    }

    override fun toString(): String {
        return "Modal"
    }
}