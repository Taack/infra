import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLDivElement
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

    val scrollContainer = document.getElementById("scroll-container") as HTMLDivElement?
    val canvasContainer = document.getElementById("canvas-container") as HTMLDivElement?
    if (canvasContainer != null) {
        if (scrollContainer != null) {
            MainCanvas(canvasContainer, scrollContainer)
        }
    }
}