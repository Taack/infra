package taack.ui.wysiwyg.canvasMono.text

class H4Canvas(txtInit: String, indent: Int = 0) : CanvasText(txtInit, indent) {
    override val fillStyle: String
        get() = "#ba3925"

    override fun computeNum(): String {
        return ""
    }

    override fun dumpAsciidoc(): String {
        return "\n==== " + super.dumpAsciidoc() + "\n"
    }

}