package taack.ui.base.element

import kotlinx.browser.document
import kotlinx.dom.addClass
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLDivElement
import taack.ui.base.BaseElement
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.Helper.Companion.traceIndent
import taack.ui.base.Helper.Companion.traceDeIndent
import taack.ui.base.record.RecordState

class Modal(val parent: Block) : BaseElement {
    companion object {
        fun buildModal(p: Block): Modal {
            return Modal(p)
        }

        var id: Int = 0
    }

    private var dModal: HTMLDivElement
    private var dModalDialog: HTMLDivElement
    private var dModalBody: HTMLDivElement
    val dModalContent: HTMLDivElement

    val mId = id++

    init {
        trace("Modal::init $mId")
        dModal = document.createElement("div") as HTMLDivElement
        dModal.classList.add("modal")
        dModalDialog = document.createElement("div") as HTMLDivElement
        dModalDialog.addClass("modal-dialog", "modal-xl", "modal-dialog-scrollable")
        dModalContent = document.createElement("div") as HTMLDivElement
        dModalBody = document.createElement("div") as HTMLDivElement
        dModalBody.addClass("modal-body")
//        val innerModal = document.createElement("div") as HTMLDivElement
        dModalContent.classList.add("modal-content")
        dModalContent.classList.add("taackModal")
        val a = document.createElement("a") as HTMLAnchorElement
        val dClose = document.createElement("div") as HTMLDivElement
        a.innerText = "X"
        a.addClass("close")
        a.onclick = { _ ->
            RecordState.clearServerState()
            close()
        }
        dClose.addClass("taack-close")
        dClose.addClass("modal-header")
        dClose.appendChild(a)
        dModalContent.appendChild(dClose)
        dModalContent.appendChild(dModalBody)
//        dModalContent.appendChild(innerModal)
        dModalDialog.appendChild(dModalContent)
        dModal.appendChild(dModalDialog)
        parent.d.parentElement?.appendChild(dModal)
    }

    fun open(htmlContent: String) {
        traceIndent("Modal::open $id")
        dModalBody.innerHTML = htmlContent
        dModal.style.display = "block"
        Block.getSiblingBlock(this)
    }

    fun close() {
        traceDeIndent("Modal::close $mId")
        dModal.style.display = "none"
        dModalBody.innerHTML = ""
//        if (parent.parent != null) d1.remove()
    }

    override fun getParentBlock(): Block {
        return parent
    }

    override fun toString(): String {
        return "Modal"
    }
}