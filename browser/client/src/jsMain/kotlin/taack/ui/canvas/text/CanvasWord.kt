package taack.ui.canvas.text


class CanvasWord(type: Type, posNStart: Int, posNEnd: Int) {
    enum class Type {
        NORMAL,
        BOLD,
        MONOSPACED,
        BOLD_MONOSPACED,
    }
}