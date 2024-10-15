package taack.ui.canvas.text

class PCanvas() : CanvasText() {
    override val font: String
        get() = "17px sans-serif"
    override val fillStyle: String
        get() = "#555"
    override val letterSpacing: Double
        get() = -0.17
    override val lineHeight: Double
        get() = 27.2
    override val wordSpacing: Double
        get() = -0.05
    override val marginTop: Double
        get() = 0.0
    override val marginBottom: Double
        get() = 20.0

    override fun computeNum(): String {
        return ""
    }

}