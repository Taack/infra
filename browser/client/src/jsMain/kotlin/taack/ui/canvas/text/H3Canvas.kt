package taack.ui.canvas.text

class H3Canvas() : CanvasText() {
    override val font: String
        get() = "27px sans-serif"
    override val fillStyle: String
        get() = "#ba3925"
    override val letterSpacing: Double
        get() = -0.37
    override val lineHeight: Double
        get() = 32.4
    override val wordSpacing: Double
        get() = -1.35
    override val marginTop: Double
        get() = 27.0
    override val marginBottom: Double
        get() = 13.5

    override fun computeNum(): String {
        num2++
        return "$num1.$num2. "
    }

}