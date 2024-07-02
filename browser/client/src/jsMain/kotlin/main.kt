import kotlinx.browser.window
import taack.ui.base.element.Block

fun main() {
    if (!window.location.href.contains("login")) {
        Block.href = window.location.href
        Block.getSiblingBlock(null)
        window.addEventListener("popstate", { window.location.reload() })
    }
}