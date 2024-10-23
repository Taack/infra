package taack.ui.canvas.table

import taack.ui.base.Helper.Companion.trace
import taack.ui.canvas.text.CanvasText

class TxtRowCanvas(txt: String) : CanvasText(txt) {
    override val fontWeight: String
        get() = "normal"
    override val fontSize: String
        get() = "17px"
    override val fontFace: String
        get() = "sans-serif"
    override val fillStyle: String
        get() = "#555"
    override val letterSpacing: Double
        get() = -0.17
    override val lineHeight: Double
        get() = 27.2
    override val wordSpacing: Double
        get() = -0.05
    override val marginTop: Double
        get() = 10.0
    override val marginBottom: Double
        get() = 10.0

    override fun computeNum(): String {
        return ""
    }

    override fun isClicked(posX: Double, posY: Double): Boolean {
        trace("TxtRowCanvas.isClicked($posX, $posY)")
        if (super.isClicked(posX, posY)) return posX in this.posXStart..this.posXEnd
        return false
    }

}