
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

    val textareas = document.querySelectorAll("textarea.asciidoctor") as List<*>
    for (element in textareas) {
        val textarea = element as HTMLTextAreaElement
        textarea.style.display = "none"
        val scrollContainer = document.createElement("div") as HTMLDivElement?
        scrollContainer?.style?.width = "${textarea.clientWidth}px"
        scrollContainer?.style?.height = "${textarea.clientHeight}px"
        scrollContainer?.style?.margin = "10px"
        scrollContainer?.style?.border = "1px solid grey"
        val canvasContainer = document.createElement("div") as HTMLDivElement?
        if (canvasContainer != null) {
            if (scrollContainer != null) {
                MainCanvas(canvasContainer, scrollContainer)
            }
        }
    }
}