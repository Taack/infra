/*
    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package taack.render

import com.itextpdf.html2pdf.ConverterProperties
import com.itextpdf.html2pdf.HtmlConverter
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider
import com.itextpdf.io.font.FontProgram
import com.itextpdf.io.font.FontProgramFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.event.PdfDocumentEvent
import com.itextpdf.layout.font.FontProvider
import com.itextpdf.styledxmlparser.resolver.font.BasicFontProvider
import grails.compiler.GrailsCompileStatic
import taack.ui.pdf.watermark.Watermark

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
        try {
            ConverterProperties properties = new ConverterProperties()
            FontProvider fontProvider = new BasicFontProvider()
            FontProgram fontProgram = FontProgramFactory.createFont(FONT_REG)
            FontProgram fontProgramCn = FontProgramFactory.createFont(FONT_REG_CN)
            fontProvider.addFont(fontProgram)
            fontProvider.addFont(fontProgramCn)
            properties.setFontProvider(fontProvider)

            PdfWriter writer = new PdfWriter(outputStream)
            PdfDocument pdfDocument = new PdfDocument(writer)
            if (watermarkText) {
                Watermark watermark = new Watermark(watermarkText)
                pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, watermark)
            }
            HtmlConverter.convertToPdf(new ByteArrayInputStream(html.bytes), pdfDocument, properties)
            pdfDocument.close()
        } catch (Throwable e) {
            log.error("${e.message}")
        }
    }
}
