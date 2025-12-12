package taack.render

import grails.compiler.GrailsCompileStatic
import org.xhtmlrenderer.layout.SharedContext
import org.xhtmlrenderer.pdf.ITextRenderer

/**
 * Convert HTML code to PDF (WiP).
 * <p>Fonts should be located in ${taackUiConfiguration.root}/pdf/fonts/
 */
@GrailsCompileStatic
class TaackPdfConverterFromHtmlService {
    static final String FONT_BOLD = 'fonts/Roboto-Bold.ttf'
    static final String FONT_ITALIC = 'fonts/Roboto-Italic.ttf'
    static final String FONT_REG = 'fonts/Roboto-Regular.ttf'
    static final String FONT_REG_CN = 'fonts/NotoSansSC-Regular.ttf'

    void generatePdfFromHtmlIText(OutputStream outputStream, final String html, String watermarkText = null) {
        ITextRenderer renderer = new ITextRenderer()
        SharedContext sharedContext = renderer.getSharedContext()
        sharedContext.setPrint(true)
        sharedContext.setInteractive(false)
        renderer.setDocumentFromString(html)
        renderer.layout()
        renderer.getFontResolver().addFont(getClass().getClassLoader().getResource(FONT_BOLD).toString(), true)
        renderer.getFontResolver().addFont(getClass().getClassLoader().getResource(FONT_ITALIC).toString(), true)
        renderer.getFontResolver().addFont(getClass().getClassLoader().getResource(FONT_REG).toString(), true)
        renderer.getFontResolver().addFont(getClass().getClassLoader().getResource(FONT_REG_CN).toString(), true)
        renderer.createPDF(outputStream)
    }
}
