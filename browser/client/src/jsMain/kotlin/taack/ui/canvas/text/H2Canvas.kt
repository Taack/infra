package taack.ui.canvas.text

class H2Canvas() : ICanvasText() {
    override val font: String
        get() = "37px sans-serif"
    override val fillStyle: String
        get() = "#ba3925"
    override val letterSpacing: Double
        get() = -0.37
    override val lineHeight: Double
        get() = 44.4
    override val wordSpacing: Double
        get() = -1.85
    override val marginTop: Double
        get() = 37.0
    override val marginBottom: Double
        get() = 18.5

    override fun computeNum(): String {
        num1++
        num2 = 0
        return "$num1. "
    }

}