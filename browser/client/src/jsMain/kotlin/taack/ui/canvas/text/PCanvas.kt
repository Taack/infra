package taack.ui.canvas.text

class PCanvas() : CanvasText() {
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

}