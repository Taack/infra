package taack.ui.canvas.text

class H4Canvas(txtInit: String) : CanvasText(txtInit) {
    override val fontWeight: String
        get() = "normal"
    override val fontSize: String
        get() = "23px"
    override val fontFace: String
        get() = "sans-serif"
    override val fillStyle: String
        get() = "#ba3925"
    override val letterSpacing: Double
        get() = -0.37
    override val lineHeight: Double
        get() = 27.6
    override val wordSpacing: Double
        get() = -1.15
    override val marginTop: Double
        get() = 23.0
    override val marginBottom: Double
        get() = 11.5

    override fun computeNum(): String {
        return ""
    }

    override fun dumpAsciidoc(): String {
        return "\n==== " + super.dumpAsciidoc() + "\n"
    }

}