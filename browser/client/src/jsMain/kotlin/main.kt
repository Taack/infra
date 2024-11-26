
import taack.ui.base.element.Block
import taack.ui.canvas.MainCanvas
import web.dom.document
import web.events.EventHandler
import web.html.HTMLDivElement
import web.html.HTMLTextAreaElement
import web.location.location
import web.window.window

fun main() {
    if (!location.href.contains("login")) {
        Block.href = location.href
        Block.getSiblingBlock(null)
        window.onpopstate = EventHandler{
            if (location.hash.isEmpty()) location.reload()
        }
    }

    val textareaList = document.querySelectorAll("textarea.asciidoctor")

    for (element in textareaList) {
        val textarea = element as HTMLTextAreaElement
        textarea.style.display = "none"
        val scrollContainer = document.createElement("div") as HTMLDivElement
//        scrollContainer.style.width = "calc(100% - 22px)"
        scrollContainer.style.height = "calc(max(30vh, 640px))"
//        scrollContainer.style.height = "calc(640px - 22px)"
//        scrollContainer.style.margin = "10px"
        scrollContainer.style.border = "1px solid grey"
        scrollContainer.style.overflow = "auto"
        val largeContainer = document.createElement("div") as HTMLDivElement
        largeContainer.style.overflow = "hidden"
        val canvasContainer = document.createElement("div") as HTMLDivElement
//        canvasContainer.style.display = "block"
        largeContainer.append(canvasContainer)
        scrollContainer.append(largeContainer)
        textarea.parentElement?.append(scrollContainer)
        MainCanvas(textarea, canvasContainer, scrollContainer)
    }
}