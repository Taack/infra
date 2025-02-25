package cms


import crew.User
import crew.config.SupportedLanguage
import grails.compiler.GrailsCompileStatic
import grails.web.api.WebAttributes
import org.asciidoctor.*
import org.asciidoctor.ast.Document
import org.codehaus.groovy.runtime.MethodClosure as MC
import taack.app.TaackApp
import taack.app.TaackAppRegisterService
import taack.domain.TaackFilter
import taack.domain.TaackFilterService
import taack.ui.dsl.UiFilterSpecifier
import taack.ui.dsl.UiTableSpecifier
import taack.ui.dsl.common.ActionIcon
import taack.ui.dsl.common.IconStyle
import taack.ui.dsl.common.Style
import taack.ui.dsl.filter.expression.FilterExpression
import taack.ui.dsl.filter.expression.Operator

import javax.annotation.PostConstruct

@GrailsCompileStatic
class CmsUiService implements WebAttributes {

    static lazyInit = false

    CmsHtmlGeneratorService cmsHtmlGeneratorService
    enum CmsTableMode {
        NONE,
        MANY_2_MANY
    }
    static CmsUiService INSTANCE
    private Asciidoctor asciidoctor

    @PostConstruct
    void init() {
        INSTANCE = this
        TaackAppRegisterService.register(new TaackApp(CmsController.&index as MC, new String(CmsUiService.getResourceAsStream("/cms/cms.svg").readAllBytes())))

        // TODO: Pfffff
        //     asciidoctor = Asciidoctor.Factory.create()
        //     asciidoctor.requireLibrary("asciidoctor-diagram", "asciidoctor-revealjs")
    }


    private static String previewMedia(Long id) {
        """<img style="max-height: 64px; max-width: 64px;" src="/cms/mediaPreview/${id ?: 0}">"""
    }

    private static String previewPdf(Long id) {
        """<img style="max-height: 64px; max-width: 64px;" src="/cms/mediaPreviewPdf/${id ?: 0}">"""
    }

    TaackFilterService taackFilterService

    static UiFilterSpecifier buildCmsPageFilter(CmsPage p = null) {
        p ?= new CmsPage()
        User u = new User()
        UiFilterSpecifier f = new UiFilterSpecifier()

        f.ui CmsPage, {
            section "Page", {
                filterField p.name_
                filterField p.subsidiary_
                filterField p.pageType_
                println p.userCreated_
                innerFilter(new UiFilterSpecifier().ui(User, {
                    section true, {
                        filterField u.username_
                        filterField u.dateCreated_
                    }
                }), p.userCreated_)
            }
        }
        f
    }

    static UiFilterSpecifier buildCmsSlideshowFilter(CmsPage ss = null) {
        ss = ss ?: new CmsPage()
        def u = new User()

        new UiFilterSpecifier().ui(CmsPage, {
            section "Slideshow Meta-inf", {
                filterField ss.userCreated_, u.username_
                filterField ss.userUpdated_, u.username_
                filterField ss.name_
                filterField ss.subsidiary_
                filterField ss.published_
            }
        })
    }

    UiTableSpecifier buildCmsSlideshowTable(UiFilterSpecifier filter = null) {
        def ss = new CmsPage()
        def u = new User()

        new UiTableSpecifier().ui {
            header {
                column {
                    sortableFieldHeader ss.dateCreated_
                    sortableFieldHeader ss.lastUpdated_
                }
                column {
                    sortableFieldHeader ss.userCreated_, u.username_
                    sortableFieldHeader ss.userUpdated_, u.username_
                }
                column {
                    sortableFieldHeader ss.name_
                    sortableFieldHeader ss.subsidiary_
                }
                column {
                    sortableFieldHeader ss.published_
                    sortableFieldHeader ss.controls_
                    sortableFieldHeader ss.progress_
                    sortableFieldHeader ss.autoSlide_
                }
            }

            UiFilterSpecifier f2 = new UiFilterSpecifier().sec(CmsPage, {
                filterFieldExpressionBool(new FilterExpression(CmsPageType.SLIDESHOW, Operator.EQ, ss.pageType_))
            })
            f2.join(filter)

            iterate(taackFilterService.getBuilder(CmsPage)
                    .setMaxNumberOfLine(20)
                    .addFilter(f2)
                    .setSortOrder(TaackFilter.Order.DESC, new CmsPage().dateCreated_)
                    .build()) { CmsPage o ->
                rowColumn {
                    rowField o.dateCreated_
                    rowField o.lastUpdated_
                }
                rowColumn {
                    rowField o.userCreated_
                    rowField o.userUpdated_
                }
                rowColumn {
                    if (!filter) rowAction ActionIcon.EDIT * IconStyle.SCALE_DOWN, CmsController.&editSlideshow as MC, o.id
                    else rowAction ActionIcon.SELECT * IconStyle.SCALE_DOWN, CmsController.&selectM2oPageCloseModal as MC, o.id
                    rowField o.name
                    rowField o.subsidiary.toString()
                }
                rowColumn {
                    rowField o.published?.toString()
                    rowField(o.controls ? 'W controls, ' : 'Wo controls, ' + o.progress ? 'W progress, ' : 'Wo progress, ' + "AutoSlide: ${o.autoSlide}")
                }
            }
        }
    }


    UiTableSpecifier buildCmsPageTable(UiFilterSpecifier filter = null) {
        CmsPage p = new CmsPage(userCreated: new User(), userUpdated: new User())
        new UiTableSpecifier().ui {
            header {
                column {
                    label "Preview"
                }
                column {
                    sortableFieldHeader p.dateCreated_
                    sortableFieldHeader p.userCreated_
                }
                column {
                    sortableFieldHeader p.lastUpdated_
                    sortableFieldHeader p.userUpdated_
                }
                column {
                    sortableFieldHeader p.name_
                    label "Languages"
                }
                column {
                    sortableFieldHeader p.subsidiary_
                    sortableFieldHeader p.pageType_
                }
                column {
                    label "Title"
                }
            }

            UiFilterSpecifier f2 = new UiFilterSpecifier().sec CmsPage, {
                filterFieldExpressionBool new FilterExpression(CmsPageType.SLIDESHOW, Operator.NE, p.pageType_)
            }
            f2.join(filter)

            iterate(taackFilterService.getBuilder(CmsPage)
                    .setMaxNumberOfLine(20)
                    .addFilter(f2)
                    .setSortOrder(TaackFilter.Order.DESC, new CmsPage().dateCreated_)
                    .build()) { CmsPage cp ->
                rowColumn {
                    rowField this.previewMedia(cp.mainImage?.id)
                }
                rowColumn {
                    rowField cp.dateCreated_
                    rowField cp.userCreated_
                }
                rowColumn {
                    rowField cp.lastUpdated_
                    rowField cp.userUpdated_
                }
                rowColumn {
                    if (!filter) rowAction ActionIcon.EDIT * IconStyle.SCALE_DOWN, CmsController.&editPage as MC, cp.id
                    else rowAction ActionIcon.SELECT * IconStyle.SCALE_DOWN, CmsController.&selectM2oPageCloseModal as MC, cp.id
                    rowField cp.name
                    rowField cp.bodyContent?.keySet()?.join(', ')
                }
                rowColumn {
                    rowField cp.subsidiary.toString()
                    rowField cp.pageType.toString()
                }
                rowColumn {
                    rowField cp.title_
                }
            }
        }
    }

    static UiFilterSpecifier buildCmsImageFilter(final CmsPage cmsPage = null, final ImageType imageType = null) {
        UiFilterSpecifier f = new UiFilterSpecifier()

        CmsImage i = new CmsImage(cmsPage: new CmsPage(pageLayout: null), imageType: imageType)

        f.ui CmsImage, cmsPage ? [id: cmsPage.id] : null, {
            section "Image", {
                filterField i.hide_
                filterField i.originalName_
                filterField i.imageType_
                if (!cmsPage)
                    filterFieldExpressionBool "Type Restriction", true, new FilterExpression(i.imageType_, Operator.EQ, i.imageType_)
            }
            section "Page", {
                filterField i.cmsPage_, i.cmsPage.name_
                filterField i.cmsPage_, i.cmsPage.subsidiary_
                filterField i.cmsPage_, i.cmsPage.pageType_
            }
        }
        f
    }

    static UiFilterSpecifier buildCmsPdfFilter(final CmsPage cmsPage = null) {
        def pdf = new CmsPdfFile(cmsPage: new CmsPage(pageLayout: null))

        new UiFilterSpecifier().ui CmsPdfFile, cmsPage ? [id: cmsPage.id] : null, {
            section "Image", {
                filterField pdf.hide_
                filterField pdf.originalName_
            }
            section "Origin", {
                filterField pdf.cmsPage_, pdf.cmsPage.name_
                filterField pdf.cmsPage_, pdf.cmsPage.subsidiary_
                filterField pdf.cmsPage_, pdf.cmsPage.pageType_
            }
        }
    }

    static UiFilterSpecifier buildCmsVideoFilter(final CmsPage cmsPage = null) {
        def i = new CmsVideoFile(cmsPage: new CmsPage(pageLayout: null))

        new UiFilterSpecifier().ui CmsVideoFile, cmsPage ? [id: cmsPage.id] : null, {
            section "Video", {
                filterField i.hide_
                filterField i.originalName_
            }
            section "Page", {
                filterField i.cmsPage_, i.cmsPage.name_
                filterField i.cmsPage_, i.cmsPage.subsidiary_
                filterField i.cmsPage_, i.cmsPage.pageType_
            }
        }
    }

    UiTableSpecifier buildCmsImageTable(final CmsPage cmsPage = null, final CmsTableMode tableMode = CmsTableMode.NONE, final UiFilterSpecifier filter = null) {
        CmsImage i = new CmsImage(cmsPage: new CmsPage())
        User u = new User()

        new UiTableSpecifier().ui {
            header {
                column {
                    label "Preview"
                }
                column {
                    sortableFieldHeader i.dateCreated_
                    sortableFieldHeader i.lastUpdated_
                }
                column {
                    sortableFieldHeader i.userCreated_, u.username_
                    sortableFieldHeader i.userUpdated_, u.username_
                }
                column {
                    sortableFieldHeader i.contentType_
                    sortableFieldHeader i.imageType_
                    label "Insert TEXT"
                }
                column {
                    sortableFieldHeader i.originalName_
                    label "Page Name"
                }
                if (!cmsPage) {
                    column {
                        sortableFieldHeader i.cmsPage_, i.cmsPage.subsidiary_
                        sortableFieldHeader i.cmsPage_, i.cmsPage.pageType_
                    }
                }
            }

            Long[] listOfImages = null
            if (cmsPage && tableMode == CmsTableMode.NONE) {
                listOfImages = new Long[cmsPage.bodyImages.size() + (CmsImage.findAllByCmsPage(cmsPage) as List<CmsImage>).size()]
                listOfImages += cmsPage.bodyImages*.id
                listOfImages += (CmsImage.findAllByCmsPage(cmsPage) as List<CmsPage>)*.id
            }

            iterate(taackFilterService.getBuilder(CmsImage)
                    .setMaxNumberOfLine(20).addRestrictedIds(listOfImages)
                    .setSortOrder(TaackFilter.Order.DESC, new CmsPage().dateCreated_)
                    .build()) { CmsImage ci ->
                rowColumn {
                    rowField this.previewMedia(ci.id)
                }
                rowColumn {
                    rowField ci.dateCreated_
                    rowField ci.lastUpdated_
                }
                rowColumn {
                    rowField ci.userCreated_
                    rowField ci.userUpdated_
                }
                rowColumn {
                    rowField ci.contentType
                    rowField ci.imageType.toString()
                    rowField '${IMG#' + ci.id.toString() + '}', Style.BOLD + Style.BLUE
                }
                rowColumn {
                    if (tableMode == CmsTableMode.NONE) {
                        rowAction ActionIcon.EDIT * IconStyle.SCALE_DOWN, CmsController.&editCmsImage as MC, ci.id
                        if (cmsPage && ci.cmsPage != cmsPage) rowAction ActionIcon.UNSELECT * IconStyle.SCALE_DOWN, CmsController.&removeCmsImageFromPage as MC, [cmsImageId: ci.id, cmsPageId: cmsPage.id]
                    } else if (tableMode == CmsTableMode.MANY_2_MANY && cmsPage) {
                        if (!cmsPage?.bodyImages*.id?.contains(ci.id)) rowAction ActionIcon.SELECT * IconStyle.SCALE_DOWN, CmsController.&addCmsImageToPage as MC, [cmsImageId: ci.id, cmsPageId: cmsPage.id]
                    } else if (tableMode == CmsTableMode.MANY_2_MANY && !cmsPage) {
                        rowAction ActionIcon.SELECT * IconStyle.SCALE_DOWN, CmsController.&selectPageImageCloseModal as MC, ci.id
                    }
                    rowField ci.originalName
                    rowField ci.cmsPage?.name
                }
                if (!cmsPage) {
                    rowColumn {
                        rowField ci.cmsPage?.subsidiary?.toString()
                        rowField ci.cmsPage?.pageType?.toString()
                    }
                }
            }
        }
    }

    UiTableSpecifier buildCmsPdfTable(final CmsPage cmsPage = null, final CmsTableMode tableMode = CmsTableMode.NONE, final UiFilterSpecifier filter = null) {
        CmsPdfFile i = new CmsPdfFile()
        User u = new User()

        new UiTableSpecifier().ui {
            header {
                column {
                    label "Preview"
                }
                column {
                    sortableFieldHeader i.dateCreated_
                    sortableFieldHeader i.lastUpdated_
                }
                column {
                    sortableFieldHeader i.userCreated_, u.username_
                    sortableFieldHeader i.userUpdated_, u.username_
                }
                column {
                    label "Insert TEXT"
                }
                column {
                    sortableFieldHeader i.originalName_
                }
            }

            Long[] listOfPdf = null
            if (cmsPage && tableMode == CmsTableMode.NONE) {
                listOfPdf = new Long[cmsPage.bodyPdfs.size() + (CmsPdfFile.findAllByCmsPage(cmsPage) as List<CmsImage>).size()]
                listOfPdf += cmsPage.bodyPdfs*.id
                listOfPdf += (CmsPdfFile.findAllByCmsPage(cmsPage) as List<CmsImage>)*.id
            }

            iterate(taackFilterService.getBuilder(CmsPdfFile)
                    .setMaxNumberOfLine(20).addRestrictedIds(listOfPdf)
                    .setSortOrder(TaackFilter.Order.DESC, new CmsPdfFile().dateCreated_)
                    .build()) { CmsPdfFile ci ->
                rowColumn {
                    rowField this.previewPdf(ci.id)
                }
                rowColumn {
                    rowField ci.dateCreated_
                    rowField ci.lastUpdated_
                }
                rowColumn {
                    rowField ci.userCreated_
                    rowField ci.userUpdated_
                }
                rowColumn {
                    rowField '${PDF#' + ci.id.toString() + '}', Style.BOLD + Style.BLUE
                }
                rowColumn {
                    if (tableMode == CmsTableMode.NONE) {
                        rowAction ActionIcon.EDIT * IconStyle.SCALE_DOWN, CmsController.&editCmsPdf as MC, ci.id
                        if (cmsPage && ci.cmsPage != cmsPage) rowAction ActionIcon.UNSELECT * IconStyle.SCALE_DOWN, CmsController.&removeCmsPdfFromPage as MC, [cmsPdfId: ci.id, cmsPageId: cmsPage.id]
                    } else if (tableMode == CmsTableMode.MANY_2_MANY && cmsPage) {
                        if (!cmsPage.bodyPdfs*.id.contains(ci.id)) rowAction ActionIcon.SELECT * IconStyle.SCALE_DOWN, CmsController.&addCmsPdfToPage as MC, [cmsPdfId: ci.id, cmsPageId: cmsPage.id]
                    } else if (tableMode == CmsTableMode.MANY_2_MANY && !cmsPage) {
                        rowAction ActionIcon.SELECT * IconStyle.SCALE_DOWN, CmsController.&selectM2mCmsPdfCloseModal as MC, ci.id
                    }

                    rowField ci.originalName
                }
            }
        }
    }

    UiTableSpecifier buildCmsInsertTable(Collection<CmsInsert> cmsInserts = null) {
        CmsInsert i = new CmsInsert()

        new UiTableSpecifier().ui {
            header {
                column {
                    sortableFieldHeader i.dateCreated_
                    sortableFieldHeader i.lastUpdated_
                }
                column {
                    label "x, y, width"
                    label "Title"
                }
                column {
                    label "Page"
                    label "Image application"
                }
                column {
                    sortableFieldHeader i.itemId_
                    sortableFieldHeader i.subFamilyId_
                    sortableFieldHeader i.rangeId_
                }
                label "Action"
            }

            iterate(taackFilterService.getBuilder(CmsInsert)
                    .setMaxNumberOfLine(20)
                    .setSortOrder(TaackFilter.Order.DESC, new CmsInsert().dateCreated_)
                    .build()) { CmsInsert ci ->
                rowColumn {
                    rowField ci.dateCreated_
                    rowField ci.lastUpdated_
                }
                rowColumn {
                    rowField "${ci.x}, ${ci.y}, ${ci.width}"
                    rowField ci.title_
                }
                rowColumn {
                    rowField ci.cmsPage_
                    rowField ci.imageApplication?.id + " " + ci.imageApplication?.originalName
                }
                rowColumn {
                    rowField ci.itemId_
                    rowField ci.subFamilyId_
                    rowField ci.rangeId_
                }
                rowAction ActionIcon.EDIT, CmsController.&cmsInsertForm as MC, ci.id
            }
        }
    }

    UiTableSpecifier buildCmsVideoTable(final CmsPage cmsPage = null, final CmsTableMode tableMode = CmsTableMode.NONE, final UiFilterSpecifier filter = null) {
        def i = new CmsVideoFile()
        User u = new User()

        new UiTableSpecifier().ui {
            header {
                column {
                    label "Preview"
                }
                column {
                    sortableFieldHeader i.dateCreated_
                    sortableFieldHeader i.lastUpdated_
                }
                column {
                    sortableFieldHeader i.userCreated_, u.username_
                    sortableFieldHeader i.userUpdated_, u.username_
                }
                column {
                    label "Insert TEXT"
                }
                column {
                    sortableFieldHeader i.originalName_
                }
            }

            Set<Long> listOfVideo = null
            if (cmsPage && tableMode == CmsTableMode.NONE) {
                listOfVideo = []
                listOfVideo.addAll cmsPage.bodyVideos*.id ?: [] as List<Long>
                listOfVideo.addAll((CmsVideoFile.findAllByCmsPage(cmsPage) as List<CmsVideoFile>)*.id as List<Long>)
            }

            iterate(taackFilterService.getBuilder(CmsVideoFile)
                    .setMaxNumberOfLine(20).addRestrictedIds(listOfVideo as Long[])
                    .setSortOrder(TaackFilter.Order.DESC, new CmsVideoFile().dateCreated_)
                    .build()) { CmsVideoFile ci ->
                rowColumn {
                    rowField this.previewMedia(ci.preview?.id)
                }
                rowColumn {
                    rowField ci.dateCreated_
                    rowField ci.lastUpdated_
                }
                rowColumn {
                    rowField ci.userCreated_
                    rowField ci.userUpdated_
                }
                rowColumn {
                    rowField '${VID#' + ci.id.toString() + '}', Style.BOLD + Style.BLUE
                }
                rowColumn {
                    if (tableMode == CmsTableMode.NONE) {
                        rowAction ActionIcon.EDIT * IconStyle.SCALE_DOWN, CmsController.&editCmsVideo as MC, ci.id
                        if (cmsPage && ci.cmsPage != cmsPage) rowAction ActionIcon.UNSELECT * IconStyle.SCALE_DOWN, CmsController.&removeCmsVideoFromPage as MC, [cmsVideoId: ci.id, cmsPageId: cmsPage.id]
                    } else if (tableMode == CmsTableMode.MANY_2_MANY && cmsPage) {
                        if (!cmsPage.bodyVideos*.id.contains(ci.id)) rowAction ActionIcon.SELECT * IconStyle.SCALE_DOWN, CmsController.&addCmsVideoToPage as MC, [cmsVideoId: ci.id, cmsPageId: cmsPage.id]
                    } else if (tableMode == CmsTableMode.MANY_2_MANY && !cmsPage) {
                        rowAction ActionIcon.SELECT * IconStyle.SCALE_DOWN, CmsController.&selectM2mCmsVideoCloseModal as MC, ci.id
                    }

                    rowField ci.originalName
                }
            }
        }
    }

    byte[] getWebSmallPic(CmsPdfFile pdfFile, String localPath) {
        String extension = "-smallpic2.png"
        File pic = new File(localPath + extension)
        if (!pic.exists()) {
            final String cmd = "gs -sDEVICE=png16m -o ${pic.path} -dDownScaleFactor=6 -dFirstPage=1 -dLastPage=1 ${localPath}"
            Process process = cmd.execute()
            InputStream errorStream = process.getErrorStream()
            InputStream inputStream = process.getInputStream()
            process.waitFor()
            log.info cmd
            log.info "convert errorStream.text: ${errorStream.text}"
            log.info "convert inputStream.text: ${inputStream.text}"
        }
        if (pic.exists()) pic.bytes
        else null
    }

    Map<String, String> bodySlideshows(CmsPage slideshow) {
        Map<String, String> ret = [:]
        SupportedLanguage.values().each {
            if (slideshow.bodyContent.containsKey(it.iso2)) ret[it.iso2] = bodySlideshow(slideshow, it)
        }
        ret
    }

    String bodySlideshow(CmsPage slideshow, SupportedLanguage language) {
        bodySlideshow(slideshow.controls, slideshow.progress, slideshow.autoSlide, slideshow.height, slideshow.bodyContent[language.iso2], language, slideshow.id)
    }

    String bodySlideshow(boolean controls, boolean progress, int autoSlide, int height, String content, SupportedLanguage language, Long id = null) {
        StringBuffer innerHtml = new StringBuffer(4096)
        innerHtml.append """
:revealjs_controls: ${controls}
:revealjs_progress: ${progress}
:revealjs_slidenumber: true
:revealjs_history: true
:revealjs_keyboard: true
:revealjs_overview: true
:revealjs_center: true
:revealjs_touch: true
:revealjs_loop: false
:revealjs_rtl: false
:revealjs_fragments: true
:revealjs_embedded: false
:revealjs_autoslide: ${autoSlide}
:revealjs_autoslidestoppable: true
:revealjs_mousewheel: true
:revealjs_hideaddressbar: true
:revealjs_previewlinks: false

:revealjs_transition: default
:revealjs_transitionspeed: default
:revealjs_backgroundtransition: default
:revealjs_viewdistance: 3
:revealjs_parallaxbackgroundimage:
:revealjs_parallaxbackgroundsize:
:revealjs_customtheme: reveal.js/css/theme/solarized.css
//:revealjs_customtheme: reveal.js/css/theme/solarized.css
:revealjs_theme: serif

:source-highlighter: highlightjs
:highlightjs-languages: groovy, gnuplot
:title-slide-transition: zoom
:title-slide-transition-speed: fast
:icons: font
:docinfo: shared
:customcss: custom.css
:revealjs_height: ${height}

"""
        innerHtml.append this.cmsHtmlGeneratorService.translateExpression(content, language.iso2)
        AttributesBuilder attributes = Attributes.builder()
                .docType("article")
                .backend("revealjs")
                .title("Test")
        OptionsBuilder options = Options.builder()
                .safe(SafeMode.UNSAFE)
                .attributes(attributes.build())

//        Asciidoctor asciidoctor = Asciidoctor.Factory.create()
//        asciidoctor.requireLibrary("asciidoctor-diagram", "asciidoctor-revealjs")
        Document document = asciidoctor.load(innerHtml.toString(), options.build())
        String slideshowContent = document.convert()
//        asciidoctor.shutdown()

        """
            <div style="height: ${height}px;">
                <div class="reveal deck${id ?: 1}">
                    <div class="slides">
                        ${slideshowContent}
                    </div>
                </div>
            </div>
<script postExecute="true">
    // More info about initialization & config:
    // - https://revealjs.com/initialization/
    // - https://revealjs.com/config/
    if (typeof Reveal != 'undefined' && document.querySelector( '.deck${id ?: 1}' )) {
        let deck1 = Reveal(document.querySelector( '.deck${id ?: 1}' ), {
            embedded: true,
            keyboardCondition: 'focused' // only react to keys when focused
        })
        deck1.initialize({
            hash: false,
            fragments: true,
            fragmentInURL: false,
            loop: true,
            transition: 'default',
            transitionSpeed: 'default',
            backgroundTransition: 'default',
            viewDistance: 3,
            
            width: 960,
height: ${height},
controls: ${controls},
progress: ${progress},
autoSlide: ${autoSlide},

            

            plugins: [RevealHighlight, RevealZoom]
        });
    }
    
</script>
        """
    }

}
