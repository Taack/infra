package taack.render

import com.itextpdf.html2pdf.HtmlConverter
import groovy.transform.CompileStatic
/**
 * Convert HTML code to PDF (WiP).
 * <p>Fonts should be located in ${taackUiConfiguration.root}/pdf/fonts/
 */
@CompileStatic
class TaackPdfConverterFromHtmlService {

    void generatePdfFromHtmlIText(OutputStream outputStream, final String html) {
        HtmlConverter.convertToPdf(new ByteArrayInputStream(html.bytes), outputStream)
    }

}
