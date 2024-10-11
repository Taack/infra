package taack.ui.canvas.text

class CanvasCharSequence(
    override val height: Int,
    override val wordSpacing: Double,
    override val letterSpacing: Double,
    override val lineHeight: Double,
    override val fontSize: Double
) : ISelectable, IStyledText {
    var charSequence: CharSequence = ""
    var yPos: Double = 0.0
    var wordAndLetterPos: List<List<Double>> = listOf()
}