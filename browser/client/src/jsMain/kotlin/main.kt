
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
}