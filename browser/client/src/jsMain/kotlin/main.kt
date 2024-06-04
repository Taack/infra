import kotlinx.browser.window
import taack.ui.base.Helper.Companion.trace
import taack.ui.base.element.Block
import taack.ui.base.record.RecordState


fun main() {
    if (window.location.href.contains("recordState=")) {
        RecordState.restoreServerState(window.location.href.replace(Regex(".*recordState="), ""))
        Block.href = window.location.href.replace(Regex("recordState=[^&]*"), "")
    } else {
        Block.href = window.location.href
    }
    trace("main Block.href=${Block.href}")
    RecordState.restoreClientState()
    Block.getSiblingBlock(null)
}