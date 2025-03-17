package taack.ui.wysiwyg.canvasMono.text

class H3Canvas(txtInit: String, indent: Int = 0) : CanvasText(txtInit, indent) {
    override val fillStyle: String
        get() = "#ba3925"

    override fun computeNum(): String {
        num2++
        return "$num1.$num2. "
    }

    override fun dumpAsciidoc(): String {
        return "\n=== " + super.dumpAsciidoc() + "\n"
    }

}