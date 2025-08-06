package taack.ui.base.element

import kotlinx.browser.document
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import taack.ui.base.BaseElement
import taack.ui.base.Helper.Companion.RELOAD
import taack.ui.base.Helper.Companion.processAjaxLink
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import web.cssom.ClassName
import web.dom.ElementId
import web.events.EventHandler
import web.html.ButtonType
import web.html.HTMLButtonElement
import web.html.HTMLDivElement
import web.html.button

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
        val fullscreenButton = document.createElement("button") as HTMLButtonElement
        fullscreenButton.type = ButtonType.button
        fullscreenButton.className = ClassName("btn-fullscreen")
        fullscreenButton.onclick = EventHandler { e ->
            e.preventDefault()
            toggleFullscreen()
        }
        closeButton = document.createElement("button") as HTMLButtonElement
        closeButton.type = ButtonType.button
        closeButton.className = ClassName("btn-close")
        closeButton.onclick = EventHandler { e ->
            e.preventDefault()
            close()
        }
        document.addEventListener("keydown", escModalCallback)

        dClose.classList.add(ClassName("taack-close"))
        dClose.classList.add(ClassName("modal-header"))
        dClose.appendChild(fullscreenButton)
        dClose.appendChild(closeButton)
        dModalContent.appendChild(dClose)
        dModalContent.appendChild(dModalBody)
        dModalDialog.appendChild(dModalContent)
        dModal.appendChild(dModalDialog)
        parent.d.parentElement?.appendChild(dModal)
    }

    fun open(htmlContent: String, reloadWhenClose: Boolean = false) {
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

        if (reloadWhenClose) {
            closeButton.onclick = EventHandler { e ->
                e.preventDefault()
                processAjaxLink(null, RELOAD, parent)
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

    private fun toggleFullscreen() {
        trace("Modal::fullscreen $mId")
        if (dModalDialog.classList.contains(ClassName("modal-fullscreen"))) {
            dModalDialog.classList.remove(ClassName("modal-fullscreen"))
        } else {
            dModalDialog.classList.add(ClassName("modal-fullscreen"))
        }
    }

    override fun getParentBlock(): Block {
        return parent
    }

    override fun toString(): String {
        return "Modal"
    }
}