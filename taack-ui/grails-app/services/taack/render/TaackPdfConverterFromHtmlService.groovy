package taack.render

import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import com.openhtmltopdf.svgsupport.BatikSVGDrawer
import com.openhtmltopdf.util.XRLog
import groovy.transform.CompileStatic
import org.jsoup.Jsoup
import org.jsoup.helper.W3CDom
import org.springframework.beans.factory.annotation.Autowired
import taack.ui.TaackUiConfiguration

import javax.annotation.PostConstruct
import java.util.logging.Level
/**
 * Convert HTML code to PDF (WiP).
 * <p>Fonts should be located in ${taackUiConfiguration.root}/pdf/fonts/
 */
@CompileStatic
class TaackPdfConverterFromHtmlService {

    private final static String ST_ROBOTO = 'Roboto'
    private final static String ST_NOTO = 'NotoSansSC'
    private final static int FONT_W_700 = 700
    private final static int FONT_W_400 = 400
    private final static BaseRendererBuilder.FontStyle FONT_STYLE = BaseRendererBuilder.FontStyle.NORMAL

    @Autowired
    TaackUiConfiguration taackUiConfiguration

    @PostConstruct
    void configureLogging() {
        XRLog.listRegisteredLoggers().each {  logger -> XRLog.setLevel(logger, Level.SEVERE) }
    }

    void generatePdfFromHtml(OutputStream outputStream, final String html) {
        final PdfRendererBuilder builder = new PdfRendererBuilder()
        builder.useFastMode()
        try {
            builder.withW3cDocument(new W3CDom().fromJsoup(Jsoup.parse(html)), 'http://localhost:8080/')
            builder.useFont(new File("${taackUiConfiguration.root}/pdf/fonts/NotoSansSC-Bold.ttf"), ST_NOTO, FONT_W_700, FONT_STYLE, true)
            builder.useFont(new File("${taackUiConfiguration.root}/pdf/fonts/NotoSansSC-Regular.ttf"), ST_NOTO, FONT_W_400, FONT_STYLE, true)
            builder.useFont(new File("${taackUiConfiguration.root}/pdf/fonts/Roboto-Medium.ttf"), ST_ROBOTO, FONT_W_700, FONT_STYLE, true)
            builder.useFont(new File("${taackUiConfiguration.root}/pdf/fonts/Roboto-Regular.ttf"), ST_ROBOTO, FONT_W_400, FONT_STYLE, true)
            builder.useSVGDrawer(new BatikSVGDrawer())
            builder.toStream(outputStream)
            builder.run()
            outputStream.close()
        } catch(e) {
            log.error "${e.message}"
        }
    }

}
