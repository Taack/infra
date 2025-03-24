package taack.ui.wysiwyg.canvasMono.text

class LiCanvas(txtInit: String, indent: Int = 0) : CanvasText(txtInit, indent) {
    override val fillStyle: String
        get() = "#555"

}