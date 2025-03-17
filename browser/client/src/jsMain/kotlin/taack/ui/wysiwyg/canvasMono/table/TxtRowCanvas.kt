package taack.ui.wysiwyg.canvasMono.table

import taack.ui.base.Helper.Companion.trace
import taack.ui.wysiwyg.canvasMono.text.CanvasText

class TxtRowCanvas(txtInit: String, indent: Int = 0) : CanvasText(txtInit, indent) {
    override val fillStyle: String
        get() = "#555"

    override fun computeNum(): String {
        return ""
    }

    override fun isClicked(posX: Double, posY: Double): Boolean {
        trace("TxtRowCanvas.isClicked($posX, $posY)")
        if (super.isClicked(posX, posY)) return posX in this.posXStart..this.posXEnd
        return false
    }

}