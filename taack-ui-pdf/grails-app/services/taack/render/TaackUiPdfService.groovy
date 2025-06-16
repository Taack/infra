package taack.render


import grails.compiler.GrailsCompileStatic
import grails.gsp.PageRenderer
import grails.web.api.WebAttributes
import grails.web.databinding.DataBinder
import org.grails.core.io.ResourceLocator
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.grails.web.util.WebUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.core.io.Resource
import taack.ui.TaackUiConfiguration
import taack.ui.dsl.UiPrintableSpecifier
import taack.ui.dump.Parameter
import taack.ui.dump.pdf.RawHtmlPrintableDump
/**
 * Service responsible for rendering a <i>web page</i> or producing <i>ajax parts</i> of a web page.
 * <p>
 * <ul>
 * <li>A full web page is rendered when calling
 * {@link TaackUiService#show(taack.ui.dsl.UiBlockSpecifier, taack.ui.dsl.UiMenuSpecifier)}
 *
 * <li>An ajax render is returned to the browser if calling
 * {@link TaackUiService#show(taack.ui.dsl.UiBlockSpecifier)}
 * </ul>
 * <pre>{@code
 *  taackUiSimpleService.show(new UiBlockSpecifier() {
 *      modal {
 *          ajaxBlock "showUser", {
 *              show "${u.username}", crewUiService.buildUserShow(u), BlockSpec.Width.MAX
 *          }
 *      }
 *  })}</pre>
 *
 * @Author Adrien Guichard
 * @Version 0.1
 * @since taack-ui 0.1
 */
@GrailsCompileStatic
final class TaackUiPdfService implements WebAttributes, DataBinder {

    TaackPdfConverterFromHtmlService taackPdfConverterFromHtmlService

    TaackUiConfiguration taackUiConfiguration

    @Autowired
    PageRenderer g

    @Value('${client.js.path}')
    String clientJsPath

    @Value('${bootstrap.js.tag}')
    String bootstrapJsTag

    @Value('${bootstrap.css.tag}')
    String bootstrapCssTag


    @Autowired
    ResourceLocator assetResourceLocator

    @Autowired
    MessageSource messageSource

    /**
     * Allow to get the HTML version of the PDF, also, render the PDF in the outputStream parameter.
     *
     * @param printableSpecifier PDF descriptor
     * @param outputStream connection to the client browser
     * @param locale the language the PDF must be rendered
     * @return the HTML version of the PDF
     */
    final String streamPdf(final UiPrintableSpecifier printableSpecifier, final OutputStream outputStream = null, Locale locale = null) {
        ByteArrayOutputStream blockStream = new ByteArrayOutputStream(8_000)
        RawHtmlPrintableDump htmlPdf = new RawHtmlPrintableDump(blockStream, new Parameter(locale ?: LocaleContextHolder.locale, messageSource, Parameter.RenderingTarget.PDF))
        printableSpecifier.visitPrintableBlock(htmlPdf)
        final StringBuffer css = new StringBuffer()
        final listCss = [

                'taack.css',
                'taack-pdf.css',
                'custom-pdf.css'
        ]
        listCss.each {
            Resource r = assetResourceLocator.findResourceForURI(it)
            if (r?.exists()) {
                css.append('\n/*! ' + it.toString() + '++++ */\n')
                css.append(r.inputStream.text)
                css.append('\n')
                css.append('\n/*! ' + it.toString() + '---- */\n')
            }
        }

        String html = g.render template: "/taackUi/block-pdf", model: [
                block          : blockStream.toString(),
                css            : css.toString(),
                root           : taackUiConfiguration.root,
                headerHeight   : htmlPdf.headerHeight,
                bootstrapJsTag : bootstrapJsTag,
                bootstrapCssTag: bootstrapCssTag
        ]

        if (outputStream) {
            taackPdfConverterFromHtmlService.generatePdfFromHtmlIText(outputStream, html)
        }
        html
    }

    static final String getDateFileName() {
        Calendar cal = Calendar.getInstance()
        int y = cal.get(Calendar.YEAR)
        int m = cal.get(Calendar.MONTH)
        int dm = cal.get(Calendar.DAY_OF_MONTH)
        int hd = cal.get(Calendar.HOUR_OF_DAY)
        int mn = cal.get(Calendar.MINUTE)
        int sec = cal.get(Calendar.SECOND)
        "$y$m$dm$hd$mn$sec"
    }
    /**
     * Allow to upload the PDF to the client browser
     *
     * @param printableSpecifier PDF descriptor
     * @param fileName
     * @param isHtml
     * @param brutHtml
     * @return
     */
    final def downloadPdf(final UiPrintableSpecifier printableSpecifier, final String fileNamePrefix, final Boolean isHtml = false) {
        String fileName = fileNamePrefix + "-${dateFileName}.pdf"
        GrailsWebRequest webUtils = WebUtils.retrieveGrailsWebRequest()
        webUtils.currentResponse.setContentType(isHtml ? 'text/html' : 'application/pdf')
        webUtils.currentResponse.setHeader('Content-disposition', "${params.boolean('inline') ? 'inline' : 'attachment'};filename=${URLEncoder.encode(fileName, 'UTF-8')}${isHtml ? '.html' : ''}")
        if (!isHtml) streamPdf(printableSpecifier, webUtils.currentResponse.outputStream)
        else webUtils.currentResponse.outputStream << streamPdf(printableSpecifier)
        try {
            webUtils.currentResponse.outputStream.flush()
            webUtils.currentResponse.outputStream.close()
            webRequest.renderView = false
        } catch (e) {
            log.error "${e.message}"
        }
    }
}