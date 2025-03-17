package taack.ui.wysiwyg.canvasMono.text

class H2Canvas(txtInit: String, indent: Int = 0) : CanvasText(txtInit, indent) {
    override val fillStyle: String
        get() = "#ba3925"

    override fun computeNum(): String {
        num1++
        num2 = 0
        return "$num1. "
    }

    override fun dumpAsciidoc(): String {
        return "\n== " + super.dumpAsciidoc() + "\n"
    }
}