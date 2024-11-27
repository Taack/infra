package taack.ui.canvas.text

class H3Canvas(txtInit: String, indent: Int = 0) : CanvasText(txtInit, indent) {
    override val fontWeight: String
        get() = "normal"
    override val fontSize: String
        get() = "27px"
    override val fontFace: String
        get() = "sans-serif"
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

    override fun dumpAsciidoc(): String {
        return "\n=== " + super.dumpAsciidoc() + "\n"
    }

}