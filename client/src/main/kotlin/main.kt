import kotlinx.browser.window
import taack.ui.base.element.Block
import taack.ui.base.record.RecordState


fun main() {
    if (window.location.href.contains("recordState=")) {
        RecordState.restoreServerState(window.location.href.replace(Regex(".*recordState="), ""))
        Block.href = window.location.href.replace(Regex("recordState=[^&]*"), "")
    } else {
        Block.href = window.location.href
    }
    println("main+++ ${Block.href}")
    RecordState.restoreClientState()
    val blocks = Block.getSiblingBlock(null)
    println("main--- ${Block.href}")
}