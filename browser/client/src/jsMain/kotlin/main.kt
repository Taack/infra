import kotlinx.browser.window
import taack.ui.base.element.Block

fun main() {
    Block.href = window.location.href
    Block.getSiblingBlock(null)
}