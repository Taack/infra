package taack.render

import grails.artefact.controller.support.ResponseRenderer
import grails.compiler.GrailsCompileStatic
import grails.gsp.PageRenderer
import grails.web.api.WebAttributes
import grails.web.databinding.DataBinder
import grails.web.servlet.mvc.GrailsParameterMap
import groovy.json.JsonSlurper
import org.grails.core.io.ResourceLocator
import org.grails.datastore.gorm.GormEntity
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.grails.web.util.WebUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.core.io.Resource
import org.springframework.web.servlet.ModelAndView
import taack.ast.type.FieldInfo
import taack.ui.TaackUiConfiguration
import taack.ui.dsl.UiBlockSpecifier
import taack.ui.dsl.UiDiagramSpecifier
import taack.ui.dsl.UiFilterSpecifier
import taack.ui.dsl.UiFormSpecifier
import taack.ui.dsl.UiMenuSpecifier
import taack.ui.dsl.UiPrintableSpecifier
import taack.ui.dsl.UiTableSpecifier
import taack.ui.dump.html.theme.ThemeMode
import taack.ui.dump.html.theme.ThemeSize
import taack.ui.dump.html.theme.ThemeSelector
import taack.ui.dsl.block.BlockSpec
import taack.ui.dump.*
import taack.ui.dump.pdf.RawHtmlPrintableDump

import javax.annotation.PostConstruct

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
final class TaackUiService implements WebAttributes, ResponseRenderer, DataBinder {

    static lazyInit = false

    TaackPdfConverterFromHtmlService taackPdfConverterFromHtmlService
    ThemeService themeService

    @Autowired
    TaackUiConfiguration taackUiPluginConfiguration

    @Autowired
    PageRenderer g

    @Value('${client.js.path}')
    String clientJsPath


    @Autowired
    ResourceLocator assetResourceLocator

    @Autowired
    MessageSource messageSource

    private static MessageSource staticMs

    @PostConstruct
    void init() {
        staticMs = messageSource
    }

    static final String tr(final String code, final Locale locale = null, final String... args) {
        if (LocaleContextHolder.locale.language == "test") return code
        try {
            staticMs.getMessage(code, args, locale ?: LocaleContextHolder.locale)
        } catch (e1) {
            try {
                staticMs.getMessage(code, args, new Locale("en"))
            } catch (e2) {
                code
            }
        }
    }

    static final String tr(final FieldInfo fieldInfo, final Locale locale = null, final String... args) {
        String[] keys = ['default.' + fieldInfo.fieldName + '.label', fieldInfo.fieldConstraint.field.type.simpleName.uncapitalize() + fieldInfo.fieldName + '.label']
        if (LocaleContextHolder.locale.language == "test") return keys.join(',')

        for (String key in keys) {
            String i18n = tr(key, locale, args)
            if (i18n && i18n != key)
                return i18n
        }
        return keys.join(',')
    }

    /**
     * Allows to retrieve the content of a block without rendering it.
     *
     * @param blockSpecifier block descriptor
     * @param isAjaxRendering if false a complete page will be generated, if true the ajax version is returned
     * @return String that contains the HTML snippet
     */
    String visit(final UiBlockSpecifier blockSpecifier, final boolean isAjaxRendering = false) {
        RawHtmlBlockDump htmlBlock = new RawHtmlBlockDump(new Parameter(isAjaxRendering, LocaleContextHolder.locale, messageSource))
        blockSpecifier.visitBlock(htmlBlock)
        htmlBlock.topElement.output
    }

    /**
     * Allows to retrieve the content of a menu without rendering it.
     *
     * @param menuSpecifier menu descriptor
     * @return String the contains the HTML snippet
     */
    static String visitMenu(final UiMenuSpecifier menuSpecifier) {
        RawHtmlBlockDump htmlBlock = new RawHtmlBlockDump(new Parameter(false, LocaleContextHolder.locale, staticMs))
        if (menuSpecifier) {
            menuSpecifier.visitMenu(htmlBlock)
            htmlBlock.menu.output
        } else ""
    }

    /**
     * Allows to retrieve the content of a table without rendering it.
     *
     * @param tableSpecifier table descriptor
     * @return String the contains the HTML snippet
     */
    static String visitTable(final UiTableSpecifier tableSpecifier) {
        ByteArrayOutputStream csvStream = new ByteArrayOutputStream()

        RawCsvTableDump csv = new RawCsvTableDump(csvStream)
        tableSpecifier.visitTable(csv)
        csvStream.toString()
    }

    private final static Object decodeCookie(String encoded) {
        if (encoded) new JsonSlurper().parseText(URLDecoder.decode(new String(Base64.getDecoder().decode(encoded)), "UTF-8"))
        else null
    }

    /**
     * Render the block to the browser. Either the page is updated with the block content, either the
     * full page is rendered if 'isAjax' params is true.
     *
     * @param block page descriptor
     * @param menu menu descriptor
     * @return
     */
    final def show(UiBlockSpecifier block, UiMenuSpecifier menu = null) {
        if (params.boolean("isAjax")) {
            render visit(block, true)
        } else {
            ThemeSelector themeSelector = themeService.themeSelector
            ThemeSize tableThemeSize = themeSelector.themeSize
            ThemeMode tableThemeMode = themeSelector.themeMode

            return new ModelAndView("/taackUi/block", [
                    themeSize   : tableThemeSize,
                    themeMode   : tableThemeMode,
                    block       : visit(block),
                    menu        : visitMenu(menu),
                    conf        : taackUiPluginConfiguration,
                    clientJsPath: clientJsPath?.length() > 0 ? clientJsPath : null,
            ])
        }
    }

    /**
     * Build a modal from a form and shows it.
     *
     * @param formSpecifier form descriptor
     * @param i18n title of the form
     * @return
     */
    final def showModal(UiFormSpecifier formSpecifier) {
        show(new UiBlockSpecifier().ui {
            modal {
                ajaxBlock actionName, {
                    form(formSpecifier)
                }
            }
        })
    }

    /**
     * Shortcut method that call the {@link #show(UiBlockSpecifier)} underneath.
     * <p>Allow to return the selection to the parent form from a table in a many to many relationship..
     * <p>Calling this method close the current modal, that contains usually a table.
     *
     * @param id id of the object selected
     * @param text label of the object
     * @return
     */
    final def closeModal(Long id = null, String text = null) {
        UiBlockSpecifier block = new UiBlockSpecifier()
        block.ui {
            closeModal id, text
        }
        show(block)
    }

    /**
     * Shortcut method that call the {@link #show(UiBlockSpecifier)} underneath.
     * <p>Allow to return the selection to the parent form from a table in a many to many relationship..
     * <p>Calling this method close the current modal, that contains usually a table.
     *
     * @param id id of the object selected
     * @param text label of the object
     * @return
     */
    final def closeModal(String id, String text) {
        UiBlockSpecifier block = new UiBlockSpecifier()
        block.ui {
            closeModal id, text
        }
        show(block)
    }

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
        RawHtmlPrintableDump htmlPdf = new RawHtmlPrintableDump(blockStream, new Parameter(false, locale ?: LocaleContextHolder.locale, messageSource))
        printableSpecifier.visitPrintableBlock(htmlPdf)
        final StringBuffer css = new StringBuffer()
        final listCss = [
                'pure-min.css',
                //'taack.css',
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
                block       : blockStream.toString(),
                css         : css.toString(),
                root        : taackUiPluginConfiguration.root,
                headerHeight: htmlPdf.headerHeight
        ]

        if (outputStream)
            taackPdfConverterFromHtmlService.generatePdfFromHtml(outputStream, html)
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
        webUtils.currentResponse.setContentType(isHtml ? "text/html" : "application/pdf")
        webUtils.currentResponse.setHeader("Content-disposition", "attachment;filename=${fileName}${isHtml ? ".html" : ""}")
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

    /**
     * Allow to upload the diagram to the client browser
     *
     * @param diagramSpecifier diagram descriptor
     * @param fileName
     * @param isSvg
     * @return
     */
    final def downloadDiagram(final UiDiagramSpecifier diagramSpecifier, final String fileName, final UiDiagramSpecifier.DiagramBase diagramBase) {
        GrailsWebRequest webUtils = WebUtils.retrieveGrailsWebRequest()
        ByteArrayOutputStream stream = new ByteArrayOutputStream()
        RawHtmlDiagramDump diagramDump = new RawHtmlDiagramDump(stream, "0", BlockSpec.Width.MAX)
        diagramSpecifier.visitDiagram(diagramDump, diagramBase)
        boolean isSvg = diagramBase == UiDiagramSpecifier.DiagramBase.SVG
        webUtils.currentResponse.setContentType(isSvg ? "image/svg+xml" : "image/png")
        webUtils.currentResponse.setHeader("Content-disposition", "attachment;filename=${fileName}.${isSvg ? "svg" : "png"}")
        stream.writeTo(webUtils.currentResponse.outputStream)
        try {
            webUtils.currentResponse.outputStream.flush()
            webUtils.currentResponse.outputStream.close()
        } catch (e) {
            log.error "${e.message}"
        }
    }

    /**
     * Allow to upload a CSV version of a table to the client browser
     *
     * @param tableSpecifier table descriptor
     * @param fileNamePrefix part of the filename before ".csv"
     * @return
     */
    final def downloadCsv(final UiTableSpecifier tableSpecifier, final String fileNamePrefix) {
        GrailsWebRequest webUtils = WebUtils.retrieveGrailsWebRequest()
        webUtils.currentResponse.setContentType("text/csv")
        webUtils.currentResponse.setHeader("Content-disposition", "filename=\"${URLEncoder.encode("${fileNamePrefix}.csv", "UTF-8")}\"")
        webUtils.currentResponse.outputStream << visitTable(tableSpecifier)
    }

    /**
     * Dump a Grails asset, typically an SVG file, in a text.
     *
     * @param assetName
     * @return the asset file content
     */
    final String dumpAsset(String assetName) {
        Resource r = assetResourceLocator.findResourceForURI(assetName)
        if (r?.exists()) r.inputStream.text
        else ""
    }

    /**
     * Dump a binary Grails asset, typically an image.
     *
     * @param assetName
     * @return binary content of the file
     */
    final byte[] dumpAssetBin(String assetName) {
        Resource r = assetResourceLocator.findResourceForURI(assetName)
        if (r?.exists()) r.inputStream.bytes
        else null
    }

    /**
     * @param aClass
     * @return objectClass initialized
     */
    final <T extends GormEntity> T ajaxBind(Class<T> aClass) {
        T anObject = aClass.getConstructor().newInstance()
        bindData(anObject, params.ajaxParams as GrailsParameterMap)
        params.remove("ajaxParams")
        final toRemove = params.keySet()
        params.removeAll { it.key.toString().startsWith("ajaxParams") }
        anObject
    }

    /**
     * Allow to reload the current page
     */
    final void ajaxReload() {
        render """__reload__"""
    }

    /**
     * Return true if the action is called from a form
     *
     * @return
     */
    final boolean isProcessingForm() {
        params.containsKey("originController")
    }

    /**
     * Clear all params elements not mandatory
     */
    final void cleanForm() {
        if (isProcessingForm())
            params.removeAll { k, v ->
                !["action", "controller"].contains(k)
            }
    }

    /**
     * Dump a block in Mail HTML version.
     *
     * @param blockSpecifier block descriptor
     * @param locale language
     * @return HTML content
     */
    final String dumpMailHtml(UiBlockSpecifier blockSpecifier, Locale locale = null) {
        RawHtmlBlockDump htmlPdf = new RawHtmlBlockDump(new Parameter(false, locale ?: LocaleContextHolder.locale, messageSource), "intranet.citel.fr")
        blockSpecifier.visitBlock(htmlPdf)

        String html = g.render template: "/taackUi/block-mail", model: [
                block: htmlPdf.topElement.output,
                root : taackUiPluginConfiguration.root
        ]
        html
    }

}