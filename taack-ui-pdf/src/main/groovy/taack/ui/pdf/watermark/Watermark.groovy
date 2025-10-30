package taack.ui.pdf.watermark

import com.itextpdf.io.font.FontProgramFactory
import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.kernel.geom.Rectangle
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfPage
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEvent
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEventHandler
import com.itextpdf.kernel.pdf.event.PdfDocumentEvent
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState
import com.itextpdf.layout.Canvas
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.VerticalAlignment

class Watermark extends AbstractPdfDocumentEventHandler {
    String watermarkText

    Watermark(String watermarkText) {
        this.watermarkText = watermarkText
    }

    @Override
    protected void onAcceptedEvent(AbstractPdfDocumentEvent abstractPdfDocumentEvent) {
        PdfDocumentEvent docEvent = (PdfDocumentEvent) abstractPdfDocumentEvent
        PdfDocument pdf = docEvent.getDocument()
        PdfPage page = docEvent.getPage()
        Rectangle pageSize = page.getPageSize()
        PdfCanvas pdfCanvas = new PdfCanvas(page.getLastContentStream(), page.getResources(), pdf)
        Canvas canvas = new Canvas(pdfCanvas, pageSize)
        PdfExtGState state = new PdfExtGState()
        state.setFillOpacity(.4f)
        pdfCanvas.setExtGState(state)
        float heightWidthRadio = page.getPageSize().getHeight() / page.getPageSize().getWidth() as Float
        Paragraph watermarkParagraph = new Paragraph(watermarkText)
                .setFontSize(40f)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setRotationAngle(Math.atan(heightWidthRadio))
        float rotatedWidth = page.getPageSize().getWidth() / Math.sqrt(heightWidthRadio * heightWidthRadio + 1) as Float
        float rotatedHeight = rotatedWidth * heightWidthRadio as Float
        float x = (page.getPageSize().getWidth() - rotatedWidth) / 2 as Float
        float y = (page.getPageSize().getHeight() - rotatedHeight) / 2 as Float
        watermarkParagraph.setFixedPosition(x, y, page.getPageSize().getWidth())
        canvas.add(watermarkParagraph)
        canvas.close()
    }
}
