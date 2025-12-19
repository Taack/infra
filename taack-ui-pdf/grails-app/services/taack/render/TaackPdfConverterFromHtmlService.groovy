package taack.render

import grails.compiler.GrailsCompileStatic
import org.openpdf.text.pdf.BaseFont
import org.xhtmlrenderer.layout.SharedContext
import org.xhtmlrenderer.pdf.ITextRenderer
import taack.ui.pdf.watermark.Watermark

/**
 * Convert HTML code to PDF (WiP).
 * <p>Fonts should be located in ${taackUiConfiguration.root}/pdf/fonts/
 */
@GrailsCompileStatic
class TaackPdfConverterFromHtmlService {
    static final String FONT_BOLD = TaackPdfConverterFromHtmlService.getClassLoader().getResource('fonts/Roboto-Bold.ttf').toString()
    static final String FONT_ITALIC = TaackPdfConverterFromHtmlService.getClassLoader().getResource('fonts/Roboto-Italic.ttf').toString()
    static final String FONT_REG = TaackPdfConverterFromHtmlService.getClassLoader().getResource('fonts/Roboto-Regular.ttf').toString()
    static final String FONT_REG_CN = TaackPdfConverterFromHtmlService.getClassLoader().getResource('fonts/NotoSansSC-Regular.ttf').toString()

    void generatePdfFromHtmlIText(OutputStream outputStream, final String html, String watermarkText = null) {
        ITextRenderer renderer = new ITextRenderer()

        boolean hasWatermark = watermarkText != null && !watermarkText.isEmpty()

        SharedContext sharedContext = renderer.getSharedContext()
        sharedContext.setPrint(true)
        sharedContext.setInteractive(false)

        renderer.setDocumentFromString(html)

        renderer.layout()
        renderer.createPDF(outputStream, !hasWatermark)

        // Add watermark
        if (hasWatermark) {
            BaseFont watermarkFont = BaseFont.createFont(
                    getClass().getClassLoader().getResource(FONT_REG_CN).toString(),
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED
            )
            renderer.getWriter().setPageEvent(new Watermark(watermarkText, watermarkFont))
            renderer.finishPDF()
        }

    }
}
