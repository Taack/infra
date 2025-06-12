package taack.render

import com.itextpdf.html2pdf.ConverterProperties
import com.itextpdf.html2pdf.HtmlConverter
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider
import com.itextpdf.io.font.FontProgram
import com.itextpdf.io.font.FontProgramFactory
import com.itextpdf.layout.font.FontProvider
import grails.compiler.GrailsCompileStatic
/**
 * Convert HTML code to PDF (WiP).
 * <p>Fonts should be located in ${taackUiConfiguration.root}/pdf/fonts/
 */
@GrailsCompileStatic
class TaackPdfConverterFromHtmlService {
    static final String FONT_BOLD = "fonts/Roboto-Bold.ttf"
    static final String FONT_ITALIC = "fonts/Roboto-Italic.ttf"
    static final String FONT_REG = "fonts/Roboto-Regular.ttf"
    static final String FONT_REG_CN = "fonts/NotoSansSC-Regular.ttf"

    void generatePdfFromHtmlIText(OutputStream outputStream, final String html) {
        try {
            ConverterProperties properties = new ConverterProperties()
            FontProvider fontProvider = new DefaultFontProvider()
            FontProgram fontProgram = FontProgramFactory.createFont(FONT_REG)
            FontProgram fontProgramCn = FontProgramFactory.createFont(FONT_REG_CN)
            fontProvider.addFont(fontProgram)
            fontProvider.addFont(fontProgramCn)
            properties.setFontProvider(fontProvider)

            HtmlConverter.convertToPdf(new ByteArrayInputStream(html.bytes), outputStream, properties)
        } catch (Throwable e) {
            log.error("${e.message}")
        }
    }
}
