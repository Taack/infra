package taack.ui.base.element

import kotlinx.browser.document
import kotlinx.dom.addClass
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLDivElement
import taack.ui.base.BaseElement
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.record.RecordState

class Modal(val parent: Block): BaseElement {
    companion object {
        fun buildModal(p: Block): Modal {
            return Modal(p)
        }
        var id: Int = 0
    }

    private var d1: HTMLDivElement
    private val d2: HTMLDivElement
    val d3: HTMLDivElement
    val innerModal: HTMLDivElement
    val mId = id++
    init {
        trace("Modal::init $mId")
        d1 = document.createElement("div") as HTMLDivElement
        d1.classList.add("modal")
        d2 = document.createElement("div") as HTMLDivElement
        d3 = document.createElement("div") as HTMLDivElement
        innerModal = document.createElement("div") as HTMLDivElement
        d2.classList.add("modal-content")
        d2.classList.add("taackModal")
        val a = document.createElement("a") as HTMLAnchorElement
        val divA = document.createElement("div") as HTMLDivElement
        a.innerText = "X"
        a.addClass("close")
        a.onclick = { e->
            RecordState.clearServerState()
            close()
        }
        divA.addClass("taack-close")
        divA.appendChild(a)
        d2.appendChild(divA)
        d2.appendChild(d3)
        d2.appendChild(innerModal)
        d1.appendChild(d2)
        parent.d.appendChild(d1)
        //        parent.modal.innerModal.appendChild(d1)

    }

    fun open(htmlContent: String) {
        d3.innerHTML = htmlContent
        d1.style.display = "block"
        Block.getSiblingBlock(this)
    }

    fun close() {
        trace("Modal::close $mId")
        d1.style.display = "none"
        d3.innerHTML = ""
//        if (parent.parent != null) d1.remove()
    }

    override fun toString(): String {
        return "Modal"
    }
}