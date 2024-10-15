package taack.ui.canvas.text

class CanvasLine(val posBegin: Int, val posEnd: Int, val posY1: Double, val posY2: Double) {
    override fun toString(): String {
        return "CanvasLine(posBegin=$posBegin, posEnd=$posEnd, posY1=$posY1, posY2=$posY2)"
    }
}