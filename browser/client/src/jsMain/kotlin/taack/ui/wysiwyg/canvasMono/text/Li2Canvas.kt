package taack.ui.wysiwyg.canvasMono.text

class Li2Canvas(txtInit: String, indent: Int = 0) : CanvasText(txtInit, indent) {
    override val fillStyle: String
        get() = "#555"

    override fun computeNum(): String {
        return "    ‧ "
    }

    override fun dumpAsciidoc(): String {
        return "** " + super.dumpAsciidoc() + "\n"
    }

}