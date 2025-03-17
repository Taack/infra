package taack.ui.wysiwyg.canvasStyled.text

class CanvasFigure(txtInit: String, citationNumber: Int) : CanvasText(txtInit, citationNumber) {

    override val fontWeight: String
        get() = "italic"
    override val fontSize: String
        get() = "12px"
    override val fontFace: String
        get() = "sans-serif"
    override val fillStyle: String
        get() = "#ba3925"
    override val letterSpacing: Double
        get() = -0.37
    override val lineHeight: Double
        get() = 16.0
    override val wordSpacing: Double
        get() = -1.35
    override val marginTop: Double
        get() = 5.0
    override val marginBottom: Double
        get() = 15.0

    override fun computeNum(): String {
        return "Fig. ${figNum++}: "
    }

    override fun dumpAsciidoc(): String {
        return "\n." + super.dumpAsciidoc() + "\n"
    }

}