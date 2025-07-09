
import taack.ui.base.element.Block
import web.dom.document
import web.events.EventHandler
import web.events.EventType
import web.events.addEventListener
import web.location.location
import web.scroll.ScrollToOptions
import web.scroll.instant
import web.storage.localStorage
import web.window.window

fun main() {
    if (!location.href.contains("login")) {
        Block.href = location.href
        Block.getSiblingBlock(null)
        window.onpopstate = EventHandler {
            if (location.hash.isEmpty()) location.reload()
        }
        document.addEventListener(EventType("DOMContentLoaded"), EventHandler {
            val yScroll = localStorage.getItem("y-scroll")
            if (yScroll != null) {
                window.scrollTo(ScrollToOptions(web.scroll.ScrollBehavior.instant, 0.0, yScroll.toDouble()))
                localStorage.removeItem("y-scroll")
            }
        })
    }
}