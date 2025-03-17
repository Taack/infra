package taack.ui.wysiwyg.canvasMono.text

class CanvasFigure(txtInit: String, citationNumber: Int) : CanvasText(txtInit, citationNumber) {

    override val fillStyle: String
        get() = "#ba3925"

    override fun computeNum(): String {
        return "Fig. ${figNum++}: "
    }

    override fun dumpAsciidoc(): String {
        return "\n." + super.dumpAsciidoc() + "\n"
    }

}