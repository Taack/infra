package taack.ui.wysiwyg.canvasMono.text

class PCanvas(txtInit: String, indent: Int = 0) : CanvasText(txtInit, indent) {
    override val fillStyle: String
        get() = "#555"

    override fun computeNum(): String {
        return ""
    }

    override fun dumpAsciidoc(): String {
        return "\n${super.dumpAsciidoc()}\n"
    }
}