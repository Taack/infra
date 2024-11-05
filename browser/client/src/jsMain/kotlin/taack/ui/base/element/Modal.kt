package taack.ui.base.element

import taack.ui.base.BaseElement
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.Helper.Companion.traceIndent
import web.dom.document
import web.events.EventHandler
import web.html.ButtonType
import web.html.HTMLButtonElement
import web.html.HTMLDivElement

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

    val mId = id++

    init {
        trace("Modal::init $mId")
        dModal = document.createElement("div") as HTMLDivElement
        dModal.classList.add("modal")
        dModalDialog = document.createElement("div") as HTMLDivElement
        dModalDialog.classList.add("modal-dialog", "modal-xl", "modal-dialog-scrollable", "modal-dialog-centered")
        dModalContent = document.createElement("div") as HTMLDivElement
        dModalBody = document.createElement("div") as HTMLDivElement
        dModalBody.classList.add("modal-body", "overflow-y-auto")
        dModalContent.classList.add("modal-content")
        dModalContent.classList.add("taackModal")
        val dClose = document.createElement("div") as HTMLDivElement
        val closeButton = document.createElement("button") as HTMLButtonElement
        closeButton.type = ButtonType.button
        closeButton.className = "btn-close"
        closeButton.onclick = EventHandler { e ->
            e.preventDefault()
            close()
        }

        dClose.classList.add("taack-close")
        dClose.classList.add("modal-header")
        dClose.appendChild(closeButton)
        dModalContent.appendChild(dClose)
        dModalContent.appendChild(dModalBody)
        dModalDialog.appendChild(dModalContent)
        dModal.appendChild(dModalDialog)
        parent.d.parentElement?.appendChild(dModal)
    }

    fun open(htmlContent: String) {
        traceIndent("Modal::open $mId")
        dModalBody.innerHTML = htmlContent
        dModal.style.display = "block"
        Block.getSiblingBlock(this)
        document.body.classList.add("modal-open")
        document.body.style.paddingRight = "15px"
        document.body.style.overflowY = "hidden"
        val modalBackdrop = document.createElement("div") as HTMLDivElement
        modalBackdrop.id = "modal-backdrop-$mId"
        modalBackdrop.classList.add("modal-backdrop", "fade", "show")
        parent.d.parentElement!!.appendChild(modalBackdrop)
    }

    fun close() {
        traceDeIndent("Modal::close $mId")
        dModal.style.display = "none"
        dModalBody.innerHTML = ""
        document.body.classList.remove("modal-open")
        document.body.style.removeProperty("padding-right")
        document.body.style.removeProperty("overflow-y")
        document.getElementById("modal-backdrop-$mId")?.remove()
    }

    override fun getParentBlock(): Block {
        return parent
    }

    override fun toString(): String {
        return "Modal"
    }
}