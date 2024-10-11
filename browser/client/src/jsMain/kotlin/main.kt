import kotlinx.browser.window
import taack.ui.base.element.Block
import taack.ui.canvas.MainCanvas

fun main() {
    if (!window.location.href.contains("login")) {
        Block.href = window.location.href
        Block.getSiblingBlock(null)
        window.addEventListener("popstate", {
            if (window.location.hash.isEmpty()) window.location.reload()
        })
    }
    MainCanvas().draw()
}