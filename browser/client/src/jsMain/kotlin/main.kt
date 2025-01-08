
import taack.ui.base.element.Block
import web.events.EventHandler
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