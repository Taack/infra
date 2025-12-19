package taack.ui.pdf.watermark

import org.openpdf.text.Document
import org.openpdf.text.pdf.BaseFont
import org.openpdf.text.pdf.PdfContentByte
import org.openpdf.text.pdf.PdfGState
import org.openpdf.text.pdf.PdfPageEventHelper
import org.openpdf.text.pdf.PdfWriter
import org.openpdf.text.Element

class Watermark extends PdfPageEventHelper {
    private final String watermarkText
    private final BaseFont baseFont
    private final float fontSize = 50f
    private final float opacity = 0.15f
    private final float rotation = 45f

    Watermark(String watermarkText, BaseFont baseFont) {
        this.watermarkText = watermarkText
        this.baseFont = baseFont
    }

    @Override
    void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte canvas = writer.getDirectContentUnder()
        PdfGState gs = new PdfGState()
        gs.setFillOpacity(opacity)
        canvas.setGState(gs)
        canvas.beginText()
        canvas.setFontAndSize(baseFont, fontSize)
        float x = (document.right() + document.left()) / 2 as Float
        float y = (document.top() + document.bottom()) / 2 as Float
        canvas.showTextAligned(Element.ALIGN_CENTER, watermarkText, x, y, rotation)
        canvas.endText()
    }
}
