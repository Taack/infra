package taack.ui.canvas.text

class CanvasWord(var word: String, val posX1: Double, val posX2: Double, val posY1: Double, val posY2: Double) {
    override fun toString(): String {
        return "CanvasWord(word='$word', posX1=$posX1, posX2=$posX2, posY1=$posY1, posY2=$posY2)"
    }
}