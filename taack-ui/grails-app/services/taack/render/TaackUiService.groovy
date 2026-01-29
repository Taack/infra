package taack.render

import asset.pipeline.grails.AssetResourceLocator
import grails.artefact.controller.support.ResponseRenderer
import grails.compiler.GrailsCompileStatic
import grails.gsp.PageRenderer
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.util.Pair
import grails.util.Triple
import grails.web.api.ServletAttributes
import grails.web.api.WebAttributes
import grails.web.databinding.DataBinder
import grails.web.servlet.mvc.GrailsParameterMap
import groovy.json.JsonSlurper
import jakarta.annotation.PostConstruct
import org.codehaus.groovy.runtime.MethodClosure
import org.grails.core.io.ResourceLocator
import org.grails.datastore.gorm.GormEntity
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.grails.web.util.WebUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.core.io.Resource
import org.springframework.web.util.HtmlUtils
import taack.ast.type.FieldInfo
import taack.domain.TaackGormClass
import taack.domain.TaackGormClassRegisterService
import taack.ui.TaackUi
import taack.ui.TaackUiConfiguration
import taack.ui.dsl.*
import taack.ui.dsl.block.BlockSpec
import taack.ui.dsl.common.ActionIcon
import taack.ui.dump.*
import taack.ui.dump.html.theme.ThemeMode
import taack.ui.dump.html.theme.ThemeSelector
import taack.ui.dump.html.theme.ThemeSize
import taack.user.TaackUser

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
 *          ajaxBlock 'showUser', {
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
final class TaackUiService implements WebAttributes, ResponseRenderer, DataBinder, ServletAttributes {

    static lazyInit = false

    private final int bufferSize = 65_536

    ThemeService themeService
    SpringSecurityService springSecurityService

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

    private static MessageSource staticMs
    protected final static Map<String, UiMenuSpecifier> contextualMenuClosures = [:]
    final static Map<Class, Pair<MethodClosure, FieldInfo[]>> contextualFieldEdit = [:]

    static void registerContextualMenuClosure(Class domain, final UiMenuSpecifier menu) {
        contextualMenuClosures.put(domain.simpleName, menu)
    }

    static void registerContextualMenuClosure(FieldInfo fieldInfo, final UiMenuSpecifier menu) {
        contextualMenuClosures.put(fieldInfo.fieldConstraint.field.declaringClass.simpleName + '::' + fieldInfo.fieldName, menu)
    }

    static UiMenuSpecifier contextualMenuClosureFromField(FieldInfo fieldInfo) {
        if (fieldInfo)
            contextualMenuClosures.get(fieldInfo.fieldConstraint.field.declaringClass.simpleName + '::' + fieldInfo.fieldName) ?: contextualMenuClosureFromField(fieldInfo.fieldConstraint.field.type)
        else null
    }

    static UiMenuSpecifier contextualMenuClosureFromField(Object value) {
        contextualMenuClosureFromField(value?.class)
    }

    static UiMenuSpecifier contextualMenuClosureFromField(Class aClass) {
        contextualMenuClosures.get(aClass?.simpleName)
    }

    static UiMenuSpecifier contextualMenuClosureFromClassName(String className, String fieldName) {
        contextualMenuClosures.get(className + '::' + fieldName) ?: contextualMenuClosures.get(className)
    }

    static void registerFieldEdit(Class domain, MethodClosure edit, FieldInfo... fields) {
        contextualFieldEdit.put(domain, new Pair<>(edit, fields))
    }

    @PostConstruct
    void init() {
        staticMs = messageSource
    }

    static final String tr(final String code, final Locale locale = null, final String... args) {
        if (LocaleContextHolder.locale.language == 'test') return code
        try {
            staticMs.getMessage(code, args, locale ?: LocaleContextHolder.locale)
        } catch (e1) {
            try {
                staticMs.getMessage(code, args, new Locale('en'))
            } catch (e2) {
                code
            }
        }
    }

    static final String tr(final FieldInfo fieldInfo, final Locale locale = null, final String... args) {
        String[] keys = ['default.' + fieldInfo.fieldName + '.label', fieldInfo.fieldConstraint.field.type.simpleName.uncapitalize() + fieldInfo.fieldName + '.label']
        if (LocaleContextHolder.locale.language == 'test') return keys.join(',')

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
     * @return String that contains the HTML snippet
     */
    void visit(final UiBlockSpecifier blockSpecifier, String... paramsToKeep) {
        if (!blockSpecifier) return
        RawHtmlBlockDump htmlBlock = new RawHtmlBlockDump(new Parameter(LocaleContextHolder.locale, messageSource, Parameter.RenderingTarget.WEB, paramsToKeep))
        blockSpecifier.visitBlock(htmlBlock)
        htmlBlock.getOutput(webRequest.response.outputStream)
    }

    private TaackUser getCurrentUser() {
        try {
            return springSecurityService.currentUser as TaackUser
        } catch (ignored) {
            return null
        }
    }

    /**
     * Render a block.
     *
     * @param blockSpecifier block descriptor
     */
    void visitAndRender(UiMenuSpecifier menu, final UiBlockSpecifier blockSpecifier, String... paramsToKeep) {
        if (!blockSpecifier) return
        Parameter p = new Parameter(LocaleContextHolder.locale, messageSource, Parameter.RenderingTarget.WEB, paramsToKeep)
        RawHtmlBlockDump htmlBlock = new RawHtmlBlockDump(p)
        blockSpecifier.visitBlock(htmlBlock)
        if (p.isModal && params.boolean('isAjax') != false) {
            params['isAjax'] = true
            htmlBlock.getOutput(webRequest.response.outputStream)
        } else {
            ThemeSelector themeSelector = themeService.themeSelector
            ThemeSize themeSize = themeSelector.themeSize
            ThemeMode themeMode = themeSelector.themeMode
            ThemeMode themeAuto = themeSelector.themeAuto


//            ModelAndView mv = new ModelAndView('/taackUi/blockNoLayout', [
//                    themeSize      : themeSize,
//                    themeMode      : themeMode,
//                    themeAuto      : themeAuto,
//                    block          : htmlBlock,
//                    menu           : visitMenu(menu, paramsToKeep),
//                    conf           : TaackUiConfiguration,
//                    clientJsPath   : clientJsPath?.length() > 0 ? clientJsPath : null,
//                    bootstrapJsTag : bootstrapJsTag,
//                    bootstrapCssTag: bootstrapCssTag,
//                    currentUser    : currentUser
//            ])
//            mv
            RawHtmlBlockDump menuBlock = new RawHtmlBlockDump(new Parameter(LocaleContextHolder.locale, staticMs, Parameter.RenderingTarget.WEB, paramsToKeep))
            menu?.visitMenu(menuBlock)

            noTemplate(
                    params.lang as String,
                    themeMode,
                    themeAuto,
                    htmlBlock,
                    menuBlock
            )
        }

    }

    void visitMenu(final UiMenuSpecifier menuSpecifier, String... paramsToKeep) {
        RawHtmlBlockDump htmlBlock = new RawHtmlBlockDump(new Parameter(LocaleContextHolder.locale, staticMs, Parameter.RenderingTarget.WEB, paramsToKeep))
        if (menuSpecifier) {
            menuSpecifier.visitMenu(htmlBlock)
            htmlBlock.menu.getOutput(webRequest.response.outputStream)
        }
    }

    /**
     * Allows to retrieve the content of a menu without rendering it.
     *
     * @param menuSpecifier menu descriptor
     * @return String the contains the HTML snippet
     */
    static void visitContextualMenu(final OutputStream out, final UiMenuSpecifier menuSpecifier, Long id) {
        RawHtmlDropdownMenuDump htmlBlock = new RawHtmlDropdownMenuDump(new Parameter(LocaleContextHolder.locale, staticMs, Parameter.RenderingTarget.WEB))
        if (menuSpecifier) {
            menuSpecifier.visitMenu(htmlBlock, id)
            htmlBlock.menu.getOutput(out)
        } else ''
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
        if (encoded) new JsonSlurper().parseText(URLDecoder.decode(new String(Base64.getDecoder().decode(encoded)), 'UTF-8'))
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
    final void show(UiBlockSpecifier block, UiMenuSpecifier menu = null, String... paramsToKeep) {
        if (!block) return
        if (menu && !params.containsKey('refresh') && !params.containsKey('targetAjaxBlockId')) params.remove('isAjax')
        if (params.boolean('isAjax')) {
            visit(block, paramsToKeep)
        } else {
            visitAndRender(menu, block, paramsToKeep)
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

    static UiBlockSpecifier downloadPdfIFrame(MethodClosure action, Long id = null) {
        new UiBlockSpecifier().ui {
            modal {
                custom """\
                    <iframe src="${(new Parameter(Parameter.RenderingTarget.WEB)).urlMapped(action, [id: id])}?inline=true"
                            width="100%"
                            height="800px">
                    </iframe>
                """.stripIndent()
            }
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
        RawHtmlDiagramDump diagramDump = new RawHtmlDiagramDump(stream)
        diagramSpecifier.visitDiagram(diagramDump, diagramBase)
        boolean isSvg = diagramBase != UiDiagramSpecifier.DiagramBase.PNG
        webUtils.currentResponse.setContentType(isSvg ? 'image/svg+xml' : 'image/png')
        webUtils.currentResponse.setHeader('Content-disposition', "attachment;filename=${fileName}.${isSvg ? 'svg' : 'png'}")
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
     * @param fileNamePrefix part of the filename before '.csv'
     * @return
     */
    final def downloadCsv(final UiTableSpecifier tableSpecifier, final String fileNamePrefix) {
        GrailsWebRequest webUtils = WebUtils.retrieveGrailsWebRequest()
        webUtils.currentResponse.setContentType('text/csv')
        webUtils.currentResponse.setHeader('Content-disposition', "filename=${fileNamePrefix}.csv")
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
        else ''
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
    final <T extends GormEntity> T ajaxBind(Class<T> aClass, boolean deleteParams = true) {
        final String AJAX_PARAMS = 'ajaxParams'
        T anObject = aClass.getConstructor().newInstance()
        if (params.containsKey(AJAX_PARAMS)) {
            GrailsParameterMap ajaxParams = params[AJAX_PARAMS]
            String stringKey = ajaxParams.keySet().find { it instanceof String }?.toString()
            if (ajaxParams.keySet().size() == 2 && stringKey.endsWith('id')) {
                anObject = aClass.invokeMethod('get', ajaxParams[stringKey]) as T
            } else {
                bindData(anObject, params.ajaxParams as GrailsParameterMap)
            }
            if (deleteParams) {
                params.remove(AJAX_PARAMS)
                params.removeAll { it.key.toString().startsWith(AJAX_PARAMS) }
            }
        }
        return anObject
    }

    /**
     * Allow to reload the current page
     */
    final void ajaxReload() {
        render '''__reload__'''
    }

    /**
     * Return true if the action is called from a form
     *
     * @return
     */
    final boolean isProcessingForm() {
        params.containsKey('originController')
    }

    /**
     * Clear all params elements not mandatory
     */
    final void cleanForm(String... paramsToKeep) {
        if (isProcessingForm())
            params.removeAll { k, v ->
                !(['action', 'controller', 'isAjax'] + paramsToKeep.toList()).contains(k)
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
        RawHtmlBlockDump htmlPdf = new RawHtmlBlockDump(new Parameter(locale ?: LocaleContextHolder.locale, messageSource, Parameter.RenderingTarget.MAIL))
        blockSpecifier.visitBlock(htmlPdf)
        ByteArrayOutputStream output = new ByteArrayOutputStream(bufferSize)
        htmlPdf.getOutput(output)
        String html = g.render template: '/taackUi/block-mail', model: [
                block: output.toString(),
                root : TaackUiConfiguration.root
        ]
        html
    }

    /**
     * Dump a block in Mail HTML version.
     *
     * @param blockSpecifier block descriptor
     * @param locale language
     * @return HTML content, and attachment to add to the mail
     */
    final Pair<String, Map<String, byte[]>> dumpMailHtmlWithDiagram(UiBlockSpecifier blockSpecifier, Locale locale = null) {
        RawHtmlBlockDump htmlPdf = new RawHtmlBlockDump(new Parameter(locale ?: LocaleContextHolder.locale, messageSource, Parameter.RenderingTarget.MAIL))
        blockSpecifier.visitBlock(htmlPdf)
        ByteArrayOutputStream output = new ByteArrayOutputStream(4096)
        htmlPdf.getOutput(output)
        String html = g.render template: '/taackUi/block-mail', model: [
                block: output.toString(),
                root : TaackUiConfiguration.root
        ]
        new Pair(html, htmlPdf.mailAttachment)
    }

    final void createModal(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = BlockSpec) final Closure closure) {
        show(TaackUi.createModal(closure))
    }

    private void noTemplate(String lang, ThemeMode themeMode, ThemeMode themeAuto, RawHtmlBlockDump htmlBlock, RawHtmlBlockDump menuBlock, String... paramsToKeep) {

//        AssetResourceLocator assets = assetResourceLocator as AssetResourceLocator

        BufferedOutputStream bout = new BufferedOutputStream(webRequest.response.outputStream, 4096)

        TaackUiConfiguration conf
        bout << """\
<!DOCTYPE html>

<html lang="${lang}" ${themeMode == ThemeMode.NORMAL ? "data-bs-theme-auto=auto data-bs-theme=${themeAuto.name}" : "data-bs-theme=${themeMode.name}"}>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width">
"""
        if (themeMode == ThemeMode.DARK)
            bout << """\
        <meta name="color-scheme" content="dark">
        <meta name="theme-color" content="#eeeeee" media="(prefers-color-scheme: dark)">
"""
        else if (themeMode == ThemeMode.LIGHT)
            bout << """\
        <meta name="color-scheme" content="light">
        <meta name="theme-color" content="#111111" media="(prefers-color-scheme: light)">
"""
        bout << """\
    <title>${conf.defaultTitle}</title>
    ${bootstrapCssTag}
    <link rel="stylesheet" href="/assets/application-taack.css"/>

    <style>
    .navbar-nav > li > .dropdown-menu {
        background-color: ${conf.bgColor};
        z-index: 9999;
    }

    body > nav .navbar-nav a.nav-link {
        color: ${conf.fgColor};
    }
    </style>

    <link rel="icon" type="image/png" href="/assets/favicon.png"/>
</head>

<body>

<nav class="navbar navbar-expand-md ${conf.fixedTop ? "fixed-top" : ""}" style="background-color: ${conf.bgColor}; color: ${conf.fgColor};">
    <div id="dropdownNav" class="container-fluid">
        <a class="navbar-brand" href="/"><img src='/assets/${conf.logoFileName}' width='${conf.logoWidth}'
                                                      height='${conf.logoHeight}' alt="Logo"/>
        </a>
        <button id="dLabel" class="navbar-toggler navbar-dark" type="button" data-bs-toggle="collapse"
                data-bs-target="#navbarSupportedContent"
                aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation"
                data-bs-toggle="dropdownNav">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarSupportedContent">"""

        menuBlock.getOutput(bout)

        bout << """
            <ul class="navbar-nav flex-row ml-md-auto ">
"""
        if (conf.hasMenuLogin) {
            TaackUser currentUser = getCurrentUser()
            if (currentUser) {
                String switchedUsername = SpringSecurityUtils.getSwitchedUserOriginalUsername()
                if (switchedUsername)
                    bout << """
                    <li class="nav-item dropdown">
                        <form action='${request.contextPath}/logout/impersonate' method='POST'>
                            <input type='submit'
                                   value="Resume as ${switchedUsername}"/>
                        </form>
                    </li>
                """
                else
                    bout << """
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" id="navbarUser" role="button"
                           data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                            ${currentUser.username}
                        </a>

                        <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="navbarUser" style="text-align: center;">
                            <li class="nav-item dropdown">
                                <a class="nav-link ajaxLink taackAjaxLink"
                                   ajaxaction="/theme?isAjax=true">${tr 'theme.label'}</a>
                            </li>
                            <li class="nav-item dropdown">
                                <a class="nav-link" href="/logout">${tr 'logout.label'}</a>
                            </li>
                        </ul>
                    </li>
                """
            }
        } else
            bout << """
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" id="navbarUser" role="button"
                           data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                            <img src="/assets/taack/icons/actions/config.svg"/>
                        </a>

                        <ul class="dropdown-menu" aria-labelledby="navbarUser">
                            <li class="nav-item dropdown">
                                <a class="nav-link" ajaxaction="/theme?isAjax=true">${tr('theme.label')}</a>
                            </li>
                            <li class="nav-item dropdown">
                                <a class="nav-link" href="/taackLogin">Login</a>
                            </li>

                        </ul>
                    </li>
            """
        bout << """
            </ul>
        </div>
    </div>
</nav>

<div id="taack-main-block">"""
        htmlBlock.getOutput(bout)
        bout << """\
</div>
<div id="taack-load-spinner" class="tck-hidden"></div>
<div id="taack-modal-minimize-items"></div>
${bootstrapJsTag}
"""
        if (clientJsPath != null && !clientJsPath.empty)
            bout << """\
    <script src="${clientJsPath}"></script>
    <script src="/assets/application-taack-debug.js"></script>
"""
        else
            bout << """\
    <script src="/assets/application-taack.js"></script>
    """
        bout << """\
</body>
</html>
        """
        bout.flush()
    }
}