package cms

import cms.config.CmsSubsidiary
import crew.CrewUiService
import crew.User
import crew.config.Subsidiary
import crew.config.SupportedLanguage
import grails.compiler.GrailsCompileStatic
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured
import grails.web.api.WebAttributes
import org.codehaus.groovy.runtime.MethodClosure as MC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import taack.domain.TaackFilterService
import taack.domain.TaackSaveService
import taack.render.TaackUiProgressBarService
import taack.render.TaackUiService
import taack.ui.dsl.*
import taack.ui.dsl.block.BlockSpec
import taack.ui.dsl.common.ActionIcon
import taack.ui.dsl.common.IconStyle
import taack.ui.dsl.common.Style
import taack.ui.dsl.diagram.DiagramTypeSpec
import taack.ui.dsl.diagram.UiDiagramVisitor
import taack.ui.dump.markdown.Markdown

import javax.annotation.PostConstruct
import static grails.async.Promises.task

/*
* TODO: Menu grouped by default
* TODO: Menu add submenu and menu
* TODO: Menu group header duplicated line
* TODO: Blocks
* TODO: Upload PDFs
* TODO: Upload Video
*/

@GrailsCompileStatic
@Secured(["ROLE_ADMIN", "ROLE_CMS_MANAGER", "ROLE_CMS_USER", "ROLE_CMS_DIRECTOR"])
class CmsController implements WebAttributes {
    TaackUiService taackUiService
    TaackSaveService taackSaveService
    CmsHtmlGeneratorService cmsHtmlGeneratorService
    CmsUiService cmsUiService
    TaackFilterService taackFilterService
    CmsSearchService cmsSearchService
    SpringSecurityService springSecurityService
    TaackUiProgressBarService taackUiProgressBarService

    @Value('${intranet.root}')
    String rootPath
    static String cmsFileRoot

    @PostConstruct
    void init() {
        cmsFileRoot = rootPath + "/cms"
        def f = new File(cmsFileRoot)
        f.mkdir()
        TaackSaveService.filePaths.put(controllerName, f)
    }

    static private UiMenuSpecifier buildMenu(String q = null) {
        UiMenuSpecifier m = new UiMenuSpecifier()
        m.ui {
            label "CMS"
            menu this.&pages as MC
            menu this.&images as MC
            menu this.&pdfs as MC
            menu this.&videos as MC
            menu this.&blocks as MC
            menu this.&slideshows as MC
            label "Admin", {
                subMenu this.&confSites as MC
                subMenu this.&menuEntries as MC
            }
            menuIcon ActionIcon.CHART, this.&testProgressBar as MC

            menuSearch CmsController.&search as MC, q
            menuOptions(SupportedLanguage.fromContext())
        }
        m
    }

    private Closure<BlockSpec> buildImagesTab(CmsPage cmsPage) {
        BlockSpec.buildBlockSpec {
            ajaxBlock() {
                table cmsUiService.buildCmsImageTable(cmsPage), {
                    menu this.&refreshCmsPageCmsImages as MC, cmsPage.id
                    menu this.&selectM2mCmsImage as MC, cmsPage.id
                    menu this.&editCmsImage as MC, [cmsPageId: cmsPage.id]
                }
            }
        }
    }

    private Closure<BlockSpec> buildVideosTab(CmsPage cmsPage) {
        BlockSpec.buildBlockSpec {
            table cmsUiService.buildCmsVideoTable(cmsPage), {
                menu this.&refreshCmsPageCmsVideos as MC, cmsPage.id
                menu this.&selectM2mCmsVideo as MC, cmsPage.id
                menu this.&editCmsVideo as MC, [cmsPageId: cmsPage.id]
            }
        }
    }

    private Closure<BlockSpec> buildPdfsTab(CmsPage cmsPage) {
        BlockSpec.buildBlockSpec {
            table cmsUiService.buildCmsPdfTable(cmsPage), {
                menu this.&refreshCmsPageCmsPdfs as MC, [id: cmsPage.id]
                menu this.&selectM2mCmsPdf as MC, [id: cmsPage.id]
                menu this.&editCmsPdf as MC, [cmsPageId: cmsPage.id]
            }
        }
    }

    private static UiFormSpecifier buildCmsPageForm(final CmsPage cmsPage) {
        new UiFormSpecifier().ui cmsPage, {
            section "Page Information", {
                row {
                    col {
                        field cmsPage.name_
                        row {
                            col {
                                field cmsPage.published_
                            }
                            col {
                                field cmsPage.pageLayout_
                            }
                        }
                    }
                    col {
                        field cmsPage.subsidiary_
                        field cmsPage.pageType_
                    }
                }
                row {
                    col {
                        ajaxField cmsPage.mainImage_, this.&selectPageImage as MC
                        innerFormAction this.&previewImage as MC
                    }
                    col {
                        ajaxField cmsPage.mainVideo_, this.&selectM2mCmsVideo as MC
                        innerFormAction this.&previewVideo as MC
                    }
                }
            }

            if (cmsPage.subsidiary)
                tabs BlockSpec.Width.MAX, {
                    for (SupportedLanguage language : cmsPage.subsidiary.languages) {
                        tabLabel "${language.label}", {
                            fieldFromMap cmsPage.title_, language.toString().toLowerCase()
                            fieldFromMap cmsPage.hatContent_, language.toString().toLowerCase()
                            fieldFromMap cmsPage.bodyContent_, language.toString().toLowerCase()
                            innerFormAction this.&previewBody as MC, null, [previewLanguage: language.toString().toLowerCase()]
                        }
                    }
                }
            formAction this.&saveCmsPage as MC, null, [:]
        }
    }

    private static UiFormSpecifier buildCmsSlideshowForm(CmsPage cmsPage) {
        cmsPage = cmsPage ?: new CmsPage(pageType: CmsPageType.SLIDESHOW)

        new UiFormSpecifier().ui cmsPage, {
            hiddenField(cmsPage.pageType_)
            section "Slideshow Information", {
                row {
                    col {
                        field cmsPage.name_
                        field cmsPage.published_
                        field cmsPage.subsidiary_
                    }
                    col {
                        field cmsPage.width_
                        field cmsPage.height_
                        field cmsPage.controls_
                        field cmsPage.progress_
                        field cmsPage.autoSlide_
                    }
                }
            }
            if (cmsPage.subsidiary)
                tabs BlockSpec.Width.MAX, {
                    for (SupportedLanguage language : cmsPage.subsidiary.languages) {
                        tabLabel "${language.label}", {
                            fieldFromMap cmsPage.bodyContent_, language.toString().toLowerCase()
                            innerFormAction this.&previewBodySlideshow as MC, null, [previewLanguage: language.toString().toLowerCase()]
                        }
                    }
                }
            formAction this.&saveCmsSlideshow as MC, null, [:]
        }
    }

    private static UiFormSpecifier buildCmsInsertForm(final CmsInsert cmsInsert) {
        new UiFormSpecifier().ui cmsInsert, {
            section "Position", {
                field cmsInsert.x_
                field cmsInsert.y_
                field cmsInsert.width_
            }

            section "Object", {
                field cmsInsert.itemId_
                field cmsInsert.subFamilyId_
                field cmsInsert.rangeId_
            }

            tabs BlockSpec.Width.MAX, {
                for (SupportedLanguage language : SupportedLanguage.values()) {
                    tabLabel "Text ${language.label}", {
                        fieldFromMap "Title ${language.toString().toLowerCase()}", cmsInsert.title_, language.toString().toLowerCase()
                        fieldFromMap "Hat ${language.toString().toLowerCase()}", cmsInsert.hat_, language.toString().toLowerCase()
                    }
                }
            }
            formAction this.&saveCmsInsert as MC, cmsInsert?.id
        }
    }

    private static UiFormSpecifier buildCmsImageForm(final CmsImage cmsImage) {
        UiFormSpecifier f = new UiFormSpecifier()
        f.ui cmsImage, {
            if (cmsImage.cmsPage) hiddenField cmsImage.cmsPage_
            section "File Upload", {
                field cmsImage.filePath_
                field cmsImage.imageType_
                for (SupportedLanguage language : SupportedLanguage.values()) {
                    fieldFromMap "ALT Text ${language.label}", cmsImage.altText_, language.toString().toLowerCase()
                }
            }
            formAction this.&uploadCmsImage as MC, cmsImage?.id
        }
        f
    }

    private static UiFormSpecifier buildCmsVideoForm(final CmsVideoFile cmsVideo) {
        new UiFormSpecifier().ui cmsVideo, {
            hiddenField cmsVideo.cmsPage_
            section "File Upload", {
                field cmsVideo.filePath_
            }
            section "Video Preview", {
                ajaxField cmsVideo.preview_, CmsController.&selectM2mCmsImage as MC, ['imageType': ImageType.MEDIA_POSTER.toString()]
            }
            section "Alt Text", {
                for (SupportedLanguage language : SupportedLanguage.values()) {
                    fieldFromMap "ALT Text ${language.label}", cmsVideo.altText_, language.toString().toLowerCase()
                }
            }
            section "Youtube id", {
                for (SupportedLanguage language : SupportedLanguage.values()) {
                    fieldFromMap "id for ${language.label}", cmsVideo.youtubeI18n_, language.toString().toLowerCase()
                }
            }
            formAction this.&uploadCmsVideo as MC, cmsVideo?.id
        }
    }

    private static UiFormSpecifier buildCmsPdfForm(final CmsPdfFile pdfFile) {
        new UiFormSpecifier().ui pdfFile, {
            hiddenField pdfFile.cmsPage_
            section "File Upload", {
                field pdfFile.filePath_
            }
            section "Alt Text", {
                for (SupportedLanguage language : SupportedLanguage.values()) {
                    fieldFromMap "ALT Text ${language.label}", pdfFile.altText_, language.toString().toLowerCase()
                }
            }
            formAction this.&uploadCmsPdf as MC, pdfFile?.id
        }
    }

    private UiTableSpecifier buildBlockTable() {
        CmsBlock cb = new CmsBlock()

        new UiTableSpecifier().ui {
            header {
                sortableFieldHeader cb.position_
                sortableFieldHeader cb.subsidiary_
                label "Page OR Menu Entry"
            }
            iterate(taackFilterService.getBuilder(CmsBlock).build()) { CmsBlock o ->
                rowField o.position
                rowField o.lastUpdated_
                rowField o.subsidiary.toString()
                String lastCol = null
                if (o.cmsPage) {
                    lastCol = " PAGE: " + o.cmsPage.title.toString()
                }
                if (o.cmsMenuEntry) {
                    lastCol += " MENU: " + o.cmsMenuEntry.title.toString()
                }
                rowColumn {
                    rowAction ActionIcon.EDIT * IconStyle.SCALE_DOWN, this.&editBlock as MC, o.id
                    rowField lastCol
                }
            }
        }
    }

    @Transactional
    @Secured(["ROLE_ADMIN", "ROLE_CMS_MANAGER", "ROLE_CMS_DIRECTOR"])
    def saveCmsPage() {
        taackSaveService.saveThenRedirectOrRenderErrors(CmsPage, this.&pages as MC)
    }

    @Transactional
    @Secured(["ROLE_ADMIN", "ROLE_CMS_MANAGER", "ROLE_CMS_DIRECTOR"])
    def saveCmsSlideshow() {
        taackSaveService.saveThenRedirectOrRenderErrors(CmsPage, this.&slideshows as MC)
    }

    @Transactional
    @Secured(["ROLE_ADMIN", "ROLE_CMS_MANAGER", "ROLE_CMS_DIRECTOR"])
    def saveCmsMenuEntry() {
        taackSaveService.saveThenRedirectOrRenderErrors(CmsMenuEntry, this.&menuEntries as MC)
    }

    @Transactional
    @Secured(["ROLE_ADMIN", "ROLE_CMS_MANAGER", "ROLE_CMS_DIRECTOR"])
    def saveCmsBlock() {
        taackSaveService.saveThenRedirectOrRenderErrors(CmsBlock, this.&blocks as MC)
    }

    def images() {
        UiBlockSpecifier b = new UiBlockSpecifier()
        b.ui {
            ajaxBlock "imagesBlockAction", {
                tableFilter CmsUiService.buildCmsImageFilter(), cmsUiService.buildCmsImageTable(), {
                    menu this.&editCmsImage as MC
                }
            }
        }
        taackUiService.show(b, buildMenu())
    }

    def pdfs() {
        UiBlockSpecifier b = new UiBlockSpecifier()
        b.ui {
            ajaxBlock "pdfsBlock", {
                table cmsUiService.buildCmsPdfTable()
            }
        }
        taackUiService.show(b, buildMenu())
    }

    def videos() {
        UiBlockSpecifier b = new UiBlockSpecifier()
        b.ui {
            ajaxBlock "videosBlock", {
                table cmsUiService.buildCmsVideoTable()
            }
        }
        taackUiService.show(b, buildMenu())
    }

    def slideshows() {
        def ss = new CmsPage()
        def u = new User()
        taackUiService.show(new UiBlockSpecifier().ui {
            tableFilter(cmsUiService.buildCmsSlideshowFilter(), cmsUiService.buildCmsSlideshowTable(), {
                menu this.&editSlideshow as MC
            })
        }, buildMenu())
    }

    def editSlideshow(CmsPage slideshow) {
        UiBlockSpecifier b = new UiBlockSpecifier()

        Boolean createNew = slideshow == null
        if (!slideshow) slideshow = new CmsPage(pageType: CmsPageType.SLIDESHOW)

        b.ui {
            if (createNew)
                modal {
                    ajaxBlock "createNew", {
                        form buildCmsSlideshowForm(slideshow)
                    }
                }
            else {
                col {
                    ajaxBlock "cmsSlideshowForm", {
                        form buildCmsSlideshowForm(slideshow)
                    }
                }
                col {
                    tabs {
                        tab "Images", buildImagesTab(slideshow)
                        tab "Videos", buildVideosTab(slideshow)
                    }
                }
            }
        }
        taackUiService.show(b, buildMenu())
    }

    @Transactional
    @Secured(["ROLE_CMS_MANAGER", "ROLE_CMS_DIRECTOR"])
    def saveCmsInsert() {
        taackSaveService.saveThenReloadOrRenderErrors(CmsInsert)
    }

    def cmsInsertForm(CmsInsert cmsInsert) {
        UiBlockSpecifier b = new UiBlockSpecifier()
        if (!cmsInsert) cmsInsert = new CmsInsert()
        b.ui {
            modal {
                ajaxBlock "imagesForm", {
                    form buildCmsInsertForm(cmsInsert)
                }
            }
        }
        taackUiService.show(b, buildMenu())

    }

    def editCmsImage(CmsImage cmsImage) {
        boolean removeModal = false
        if (!cmsImage)
            if (params.containsKey('cmsPageId')) {
                removeModal = true
                cmsImage = new CmsImage(cmsPage: CmsPage.read(params.long('cmsPageId')), imageType: ImageType.PAGE_CONTENT)
            } else {
                cmsImage = new CmsImage(imageType: ImageType.PAGE_PREVIEW)
            }

        UiBlockSpecifier b = new UiBlockSpecifier().ui {
            if (removeModal)
                closeModalAndUpdateBlock {
                    modal {
                        form buildCmsImageForm(cmsImage)
                    }
                }
            else
                modal {
                    form buildCmsImageForm(cmsImage)
                }
        }
        taackUiService.show(b)
    }

    def editCmsVideo(CmsVideoFile videoFile) {
        UiBlockSpecifier b = new UiBlockSpecifier()
        if (!videoFile) videoFile = new CmsVideoFile(cmsPage: CmsPage.read(params.long("cmsPageId")))
        b.ui {
            modal {
                ajaxBlock "videoForm", {
                    form buildCmsVideoForm(videoFile)
                }
            }
        }
        taackUiService.show(b, buildMenu())
    }

    def editCmsPdf(CmsPdfFile pdfFile) {
        UiBlockSpecifier b = new UiBlockSpecifier()
        if (!pdfFile) pdfFile = new CmsPdfFile(cmsPage: CmsPage.read(params.long("cmsPageId")))
        b.ui {
            modal {
                ajaxBlock "pdfsForm", {
                    form buildCmsPdfForm(pdfFile)
                }
            }
        }
        taackUiService.show(b, buildMenu())
    }

    @Transactional
    @Secured(["ROLE_ADMIN", "ROLE_CMS_MANAGER", "ROLE_CMS_DIRECTOR"])
    def uploadCmsImage() {
        if (taackUiService.isProcessingForm()) {
            if (params.containsKey('cmsPage.id')) {
                def i = taackSaveService.save(CmsImage)
                i.save(flush: true)
                def page = CmsPage.read(params.long('cmsPage.id'))
                taackUiService.cleanForm()
                taackSaveService.displayBlockOrRenderErrors(i, new UiBlockSpecifier().ui {
                    closeModalAndUpdateBlock buildImagesTab(page)
                })
            } else {
                def i = taackSaveService.save(CmsImage)
                i.save(flush: true)
                taackSaveService.displayBlockOrRenderErrors(i, new UiBlockSpecifier().ui {
                    closeModal i.id, i.toString()
                })
            }
        }
    }

    @Transactional
    @Secured(["ROLE_ADMIN", "ROLE_CMS_MANAGER", "ROLE_CMS_DIRECTOR"])
    def uploadCmsVideo() {
        if (taackUiService.isProcessingForm()) {
            def videoFile = taackSaveService.save(CmsVideoFile)
            videoFile.save(flush: true)
            def page = CmsPage.read(params.long("cmsPage.id"))
            taackUiService.cleanForm()
            taackSaveService.displayBlockOrRenderErrors(videoFile, new UiBlockSpecifier().ui {
                closeModalAndUpdateBlock buildVideosTab(page)
            })
        }
    }

    @Transactional
    @Secured(["ROLE_ADMIN", "ROLE_CMS_MANAGER", "ROLE_CMS_DIRECTOR"])
    def uploadCmsPdf() {
        if (taackUiService.isProcessingForm()) {
            def pdfFile = taackSaveService.save(CmsPdfFile)
            pdfFile.save(flush: true)
            def page = CmsPage.read(params.long("cmsPage.id"))
            taackUiService.cleanForm()
            taackSaveService.displayBlockOrRenderErrors(pdfFile, new UiBlockSpecifier().ui {
                closeModalAndUpdateBlock buildPdfsTab(page)
            })
        }
    }

    def previewBody(String previewLanguage) {
        UiBlockSpecifier b = new UiBlockSpecifier()
        String html = """<div class="markdown-body">
                            ${cmsHtmlGeneratorService.translate(params["bodyContent"][previewLanguage] as String, previewLanguage)}
                    </div>
                    <script>
                    function youtubeVideo() {
                        const ele = document.getElementsByClassName('popup');
                        for (let i = 0; i < ele.length; i++) {
                            const e = ele[i];
                            e.onclick = function (ev) {
                                const videoRatio = e.getAttribute("data-width") / e.getAttribute("data-height");
                                const maximumWidth = document.body.offsetWidth;
                                const iframe = document.createElement("iFrame");
                                iframe.setAttribute("src", e.getAttribute("data-link"));
                                iframe.style.width = "66%";
                                iframe.style.height = "80%";
                                iframe.style.position = "fixed";
                                iframe.style.left = 0;
                                iframe.style.right = 0;
                                iframe.style.bottom = 0;
                                iframe.style.top = 0;
                                iframe.style.margin = 'auto';
                                iframe.style.overflow = 'hidden';
                                iframe.id = 'theIframe';
                                const div = document.getElementById("video-view");
                                const background = document.createElement("div");
                                background.style.position = "fixed";
                                background.style.width = "100%";
                                background.style.height = "100%";
                                background.style.top = 0;
                                background.style.left = 0;
                                background.style.right = 0;
                                background.style.bottom = 0;
                                background.style.backgroundColor = "rgba(0, 0, 0, 0.5)";
                                background.zIndex = 2;
                                background.cursor = "pointer";
                                background.onclick = function () {
                                    background.remove();
                                };
                                background.append(iframe);
                                div.append(background);
                                //ifram.wrap("<div class='class-video'>");
                            }
                        }
                    }
                    youtubeVideo();
                    </script>
                    """
        b.ui {
            modal {
                ajaxBlock "renderedBody", {
                    custom html
                }
            }
        }
        taackUiService.show(b)
    }

    def previewBodySlideshow(CmsPage slideshow) {

        def html = cmsUiService.bodySlideshow(slideshow, SupportedLanguage.fromIso2(params.previewLanguage as String))

        UiBlockSpecifier b = new UiBlockSpecifier()
        b.ui {
            modal {
                ajaxBlock "renderedBody", {
                    custom html
                }
            }
        }
        taackUiService.show(b)
    }

    def previewImage() {
        UiBlockSpecifier b = new UiBlockSpecifier()
        String html = params.long("mainImage") ? """<div class="markdown-body">
                        <img src="/cms/mediaPreview/${params.long("mainImage")}"
                    </div>""" : """No Preview"""
        b.ui {
            modal {
                ajaxBlock "renderedBody", {
                    custom html
                }
            }
        }
        taackUiService.show(b)
    }

    def previewVideo() {
        CmsVideoFile videoFile = CmsVideoFile.read params.long("mainVideo")
        UiBlockSpecifier b = new UiBlockSpecifier()
        String html = videoFile?.preview?.id ? """<div class="markdown-body">
                        <img src="/cms/mediaPreview/${videoFile?.preview?.id}"
                    </div>""" : """ No Preview"""
        b.ui {
            modal {
                ajaxBlock "renderedBody", {
                    custom html
                }
            }
        }
        taackUiService.show(b)
    }

    def selectPageImage() {
        UiBlockSpecifier b = new UiBlockSpecifier()
        def filter = CmsUiService.buildCmsImageFilter(null, ImageType.PAGE_PREVIEW)
        b.ui {
            modal {
                ajaxBlock "selectPageImage", {
                    tableFilter filter, cmsUiService.buildCmsImageTable(null, CmsUiService.CmsTableMode.MANY_2_MANY, filter), {
                        menu this.&editCmsImage as MC
                    }
                }
            }
        }
        taackUiService.show(b)
    }

    def selectPageImageCloseModal(CmsImage cmsImage) {
        taackUiService.show new UiBlockSpecifier().ui {
            closeModal cmsImage.id, cmsImage.toString()
        }
    }

    private static String localPath(final String filePath) {
        cmsFileRoot + "/" + filePath
    }

    def mediaPreview(Long id) {
        CmsImage cmsImage = CmsImage.read(id)
        response.setContentType(cmsImage?.contentType ?: "image/svg+xml")
        response.setHeader("Content-disposition", "filename=\"${URLEncoder.encode(cmsImage?.fileName ?: 'noPreview.svg', 'UTF-8')}\"")
        if (!cmsImage) response.setHeader("Cache-Control", "max-age=604800")
        if (!cmsImage?.filePath) {
            response.outputStream << """\
                <svg fill="#000000" width="800px" height="800px" viewBox="0 0 32 32" id="icon" xmlns="http://www.w3.org/2000/svg">
                    <defs>
                        <style>.cls-1{fill:none;}</style>
                    </defs>
                    <title>no-image</title>
                    <path d="M30,3.4141,28.5859,2,2,28.5859,3.4141,30l2-2H26a2.0027,2.0027,0,0,0,2-2V5.4141ZM26,26H7.4141l7.7929-7.793,2.3788,2.3787a2,2,0,0,0,2.8284,0L22,19l4,3.9973Zm0-5.8318-2.5858-2.5859a2,2,0,0,0-2.8284,0L19,19.1682l-2.377-2.3771L26,7.4141Z"/>
                    <path d="M6,22V19l5-4.9966,1.3733,1.3733,1.4159-1.416-1.375-1.375a2,2,0,0,0-2.8284,0L6,16.1716V6H22V4H6A2.002,2.002,0,0,0,4,6V22Z"/>
                    <rect id="_Transparent_Rectangle_" data-name="&lt;Transparent Rectangle&gt;" class="cls-1" width="32" height="32"/>
                </svg>
            """.stripIndent()
        } else {
            String fp = localPath(cmsImage.filePath)
            if (fp) {
                response.outputStream << new File(fp).bytes
                return true
            } else log.error "Path: ${fp} does not point to a file (${cmsFileRoot}, ${cmsImage.filePath}, ${cmsImage.fileName})"
        }
        return false
    }

    def mediaPreviewPdf(CmsPdfFile pdfFile) {
        if (pdfFile) {
            final String fp = localPath(pdfFile.filePath)
            if (fp) {
                final String fn = fp.substring(fp.lastIndexOf('/') + 1)
                try {
                    response.setHeader("Content-disposition", "attachment; filename=" + (fn)?.replaceAll('[^a-zA-Z0-9_\\.\\-]', '_'))
                    response.contentType = "image/png"
                    response.outputStream << cmsUiService.getWebSmallPic(pdfFile, fp)
                } catch (e) {
                    log.error "mediaPreviewPdf ${pdfFile.id}: ${e.message}"
                }
            }
        }
        return false
    }

    def index() {
        UiBlockSpecifier b = new UiBlockSpecifier().ui {
            ajaxBlock "indexHelp", {
                show new UiShowSpecifier().ui {
                    inlineHtml Markdown.getContentHtml("""\
                        # Markdown
                        
                        Install [Typora](https://typora.io/)
                        
                        You will be able to redact **Markdown** content in a **WYSIWYG** environment, used inside the page content. Typora allows you to copy / past HTML content and translate it into Markdown (**.md** file extension)
                        
                        Inside **Typora**, you can see Markdown content clicking on `Presentation` > `Source code mode`. You can write some text in Drive, then copy / past the content in it.
                        
                        # Special Features
                        
                        ## Insert an image
                        
                        ```
                        \${IMG#<image_id_from_the_cms>}
                        ```
                        
                        The **image** will be resized and converted to be quickly downloadable from the browser.
                        
                        ## Insert a link to a CMS page
                        
                        ```
                        \${LINK#<page_id_from_**the_cms**>}<some-text>\${CLOSE_LINK}
                        ```
                        
                        `\${LINK#2190595}*voir page "Technologie VG"*\${CLOSE_LINK}` 
                        See [comment-choisir-son-parafoudre](https://citel.fr/fr/comment-choisir-son-parafoudre)
                        
                        ## Insert a link to an Item
                        
                        ```
                        \${ITEM_LINK#<item_id_from_**Product Database**>}<some-text>\${CLOSE_LINK}`
                        ```
                    
                        `\${ITEM_LINK#2066424}***DAC50VGS***\${CLOSE_LINK} Type 2+3, Technologie VG`
                        See: [gamme-parafoudres-dac-ddc](https://citel.fr/fr/gamme-parafoudres-dac-ddc)

                        
                        ## Insert a link to a PDF
                        
                        ```
                        \${PDF#<pdf_id_from_**the_cms**>}
                        ```
                        
                        A preview of the **PDF** along with a link to **download** the PDF will be inserted.
                        
                        ## Insert a link to a Youtube video
                        
                        ```
                        \${VID_LINK#<pdf_id_from_**the_cms**>}
                        ```
                        
                        A preview of the **video** along with a link to see it in a popup will be inserted.
                        
                        ## Insert a non-youtube video viewer
                        
                        ```
                        \${VID#<pdf_id_from_**the_cms**>}
                        ```
                        
                        Same as above, but the video has been uploaded directly into the Intranet
                        
                        ## <s>Link to Product database content (**Item**, **Range**, **Family**, **Sub-family**)</s>
                        
                        `DEPRECATED`
                        ```
                        \${INSERT_LINK#<insert_id_from_**the_cms**>}<some-text>\${CLOSE_LINK}
                        ```
                        
                        # Asciidoc and Slideshows
                        
                        See current slideshows from the menu to understand the syntax. It is much more powerfull than Markdown, 
                        but we just need a subset of its functionalities for the slides. 
                        
                        You can insert a slideshow into a page, invoking: 
                        
                        ```
                        \${SLIDESHOW#<slideshow_id_from_**the_cms**>}
                        ```

                        You can change the main site slideshow via the admin menu (Conf Sites)
                        
                        ## Use image or video background in a slideshow
                        
                        ```
                        image::\${IMG_LINK#id_of_the_image}[]
                        ```
                        OR
                        ```
                        image::\${IMG_LINK#id_of_the_image}[background, size=cover]
                        ```

                        Complete sample will be available soon.
                        
                        ```
                        [.invertNight,transition=zoom]
                        == Gamme DAC
                        
                        image::\${IMG_LINK#id_of_the_image}[background, size=cover]
                        ```
                        
                        *Adrien Guichard*

                        """.stripIndent()), "markdown-body"
                }
            }
        }
        taackUiService.show(b, buildMenu())
    }

    def pages() {
        UiBlockSpecifier b = new UiBlockSpecifier().ui {
            tableFilter CmsUiService.buildCmsPageFilter(), cmsUiService.buildCmsPageTable(), {
                menu this.&editPage as MC
            }
        }
        taackUiService.show(b, buildMenu())
    }

    def editPage(CmsPage cmsPage) {
        Boolean createNew = cmsPage == null
        if (!cmsPage) cmsPage = new CmsPage()

        if (createNew)
            taackUiService.show new UiBlockSpecifier().ui {
                modal {
                    form buildCmsPageForm(cmsPage)
                }
            }
        else
            taackUiService.show new UiBlockSpecifier().ui {
                row {
                    col {
                        form buildCmsPageForm(cmsPage)
                    }
                    col {
                        tabs {
                            tab "Images", buildImagesTab(cmsPage)
                            tab "Pdfs", buildPdfsTab(cmsPage)
                            tab "Videos", buildVideosTab(cmsPage)
                        }
                    }
                }
            }, buildMenu()
    }

    @Secured(["ROLE_ADMIN", "ROLE_CMS_DIRECTOR", "ROLE_CMS_MANAGER"])
    def editBlock(CmsBlock block) {
        def cu = springSecurityService.currentUser as User
        def cmsSub = CmsSubsidiary.values().find { it.getSubsidiary() == cu.subsidiary }
        block ?= new CmsBlock(subsidiary: cmsSub)
        taackUiService.show(new UiBlockSpecifier().ui {
            modal {
                ajaxBlock "editBlock", {
                    form new UiFormSpecifier().ui(block, {
                        section('Block Position') {
                            field block.position_
                            field block.subsidiary_
                        }
                        section("Block Content") {
                            ajaxField block.cmsMenuEntry_, this.&selectM2oMenu as MC, ['subsidiary': block.subsidiary, 'theId': block.id]
                            ajaxField block.cmsPage_, this.&selectM2oPage as MC, ['subsidiary': block.subsidiary]
                        }
                        formAction this.&saveCmsBlock as MC
                    })
                }
            }
        })
    }

    def selectM2oMenu() {
        UiBlockSpecifier b = new UiBlockSpecifier()
        def s = params['subsidiary'] as CmsSubsidiary
        b.ui {
            modal {
                ajaxBlock "menuEntries", {
                    table buildCmsMenuEntryTable(s, params.long('theId'))
                }
            }
        }
        taackUiService.show(b)
    }

    def selectM2oPage() {
        def p = taackUiService.ajaxBind(CmsPage)
        UiBlockSpecifier b = new UiBlockSpecifier()
        def filter = CmsUiService.buildCmsPageFilter(p)

        b.ui {
            modal {
                ajaxBlock "selectM2oPage", {
                    tableFilter filter, cmsUiService.buildCmsPageTable(filter)
                }
            }
        }
        taackUiService.show(b, buildMenu())
    }

    @Transactional
    def refreshCmsPageCmsImages(CmsPage cmsPage) {
        UiBlockSpecifier b = new UiBlockSpecifier()

        cmsPage.bodyContent.each {
            def m = it.value =~ /\$\{IMG#([0-9]*)/
            (0..<m.count).each { occ ->
                CmsImage img = CmsImage.get((m[occ] as List)[1] as String)
                if (img) cmsPage.addToBodyImages(img)
            }
        }

        cmsPage.save()

        b.ui buildImagesTab(cmsPage)
        taackUiService.show(b)
    }

    @Transactional
    def refreshCmsPageCmsVideos(CmsPage cmsPage) {
        UiBlockSpecifier b = new UiBlockSpecifier()

        cmsPage.bodyContent.each {
            def m = it.value =~ /\$\{VID(_LINK)?#([0-9]*)/
            (0..<m.count).each { occ ->
                String id = (m[occ] as List)[1] as String == '_LINK' ? (m[occ] as List)[2] as String : (m[occ] as List)[1] as String
                CmsVideoFile img = CmsVideoFile.get(id)
                if (img) cmsPage.addToBodyVideos(img)
            }
        }

        cmsPage.save()

        b.ui buildVideosTab(cmsPage)
        taackUiService.show(b)
    }

    @Transactional
    def refreshCmsPageCmsPdfs(CmsPage cmsPage) {
        UiBlockSpecifier b = new UiBlockSpecifier()

        cmsPage.bodyContent.each {
            def m = it.value =~ /\$\{PDF#([0-9]*)/
            (0..<m.count).each { occ ->
                CmsPdfFile img = CmsPdfFile.get((m[occ] as List)[1] as String)
                if (img) cmsPage.addToBodyPdfs(img)
            }
        }

        cmsPage.save()

        b.ui buildPdfsTab(cmsPage)
        taackUiService.show(b)
    }

    def selectM2mCmsImage(CmsPage cmsPage) {
        def filter = cmsPage ? CmsUiService.buildCmsImageFilter(cmsPage) : CmsUiService.buildCmsImageFilter(null, params['imageType'] as ImageType)

        taackUiService.show new UiBlockSpecifier().ui {
            modal {
                tableFilter filter, cmsUiService.buildCmsImageTable(cmsPage, CmsUiService.CmsTableMode.MANY_2_MANY, filter)
            }
        }
    }

    def selectM2mCmsVideo(CmsPage cmsPage) {
        UiBlockSpecifier b = new UiBlockSpecifier()
        def filter = CmsUiService.buildCmsVideoFilter(cmsPage)

        b.ui {
            modal {
                ajaxBlock "selectM2mCmsVideoBlock", {
                    tableFilter filter, cmsUiService.buildCmsVideoTable(cmsPage, CmsUiService.CmsTableMode.MANY_2_MANY, filter)
                }
            }
        }
        taackUiService.show(b, buildMenu())
    }

    def selectM2mCmsPdf(CmsPage cmsPage) {
        UiBlockSpecifier b = new UiBlockSpecifier()
        def filter = CmsUiService.buildCmsPdfFilter(cmsPage)

        b.ui {
            modal {
                ajaxBlock "selectM2mCmsPdfBlock", {
                    tableFilter filter, cmsUiService.buildCmsPdfTable(cmsPage, CmsUiService.CmsTableMode.MANY_2_MANY, filter)
                }
            }
        }
        taackUiService.show(b, buildMenu())
    }

    def selectM2mCmsSlideshow() {
        def cs = taackUiService.ajaxBind(CmsConfSite)
        CmsSubsidiary cmsSubsidiary = cs.subsidiary
        UiBlockSpecifier b = new UiBlockSpecifier()
        def cmsPage = new CmsPage(subsidiary: cmsSubsidiary, pageType: CmsPageType.SLIDESHOW)
        def filter = CmsUiService.buildCmsSlideshowFilter(cmsPage)
        b.ui {
            modal {
                ajaxBlock "selectM2mCmsSlideshow", {
                    tableFilter filter, cmsUiService.buildCmsSlideshowTable(filter)
                }
            }
        }
        taackUiService.show(b, buildMenu())
    }

    @Transactional
    @Secured(["ROLE_ADMIN", "ROLE_CMS_MANAGER", "ROLE_CMS_DIRECTOR"])
    def addCmsImageToPage() {
        CmsPage cmsPage = CmsPage.get(params.long("cmsPageId"))
        cmsPage.addToBodyImages(CmsImage.get(params.getLong("cmsImageId")))
        taackUiService.show(new UiBlockSpecifier().ui {
            closeModalAndUpdateBlock buildImagesTab(cmsPage)
        })
    }

    @Transactional
    @Secured(["ROLE_ADMIN", "ROLE_CMS_MANAGER", "ROLE_CMS_DIRECTOR"])
    def addCmsPdfToPage() {
        CmsPage cmsPage = CmsPage.get(params.long("cmsPageId"))
        cmsPage.addToBodyPdfs(CmsPdfFile.get(params.getLong("cmsPdfId")))
        taackUiService.show(new UiBlockSpecifier().ui {
            closeModalAndUpdateBlock buildPdfsTab(cmsPage)
        })
    }

    @Transactional
    @Secured(["ROLE_ADMIN", "ROLE_CMS_MANAGER", "ROLE_CMS_DIRECTOR"])
    def addCmsVideoToPage() {
        CmsPage cmsPage = CmsPage.get(params.long("cmsPageId"))
        cmsPage.addToBodyVideos(CmsVideoFile.get(params.getLong("cmsVideoId")))
        taackUiService.show(new UiBlockSpecifier().ui {
            closeModalAndUpdateBlock buildVideosTab(cmsPage)
        })
    }

    @Transactional
    @Secured(["ROLE_ADMIN", "ROLE_CMS_MANAGER", "ROLE_CMS_DIRECTOR"])
    def removeCmsImageFromPage() {
        CmsPage cmsPage = CmsPage.get(params.long("cmsPageId"))
        cmsPage.removeFromBodyImages(CmsImage.read(params.getLong("cmsImageId")))
        taackUiService.show(new UiBlockSpecifier().ui(buildImagesTab(cmsPage)))
    }

    @Transactional
    @Secured(["ROLE_ADMIN", "ROLE_CMS_MANAGER", "ROLE_CMS_DIRECTOR"])
    def removeCmsPdfFromPage() {
        CmsPage cmsPage = CmsPage.get(params.long("cmsPageId"))
        cmsPage.removeFromBodyPdfs(CmsPdfFile.read(params.getLong("cmsPdfId")))
        taackUiService.show(new UiBlockSpecifier().ui(buildPdfsTab(cmsPage)))
    }

    @Transactional
    @Secured(["ROLE_ADMIN", "ROLE_CMS_MANAGER", "ROLE_CMS_DIRECTOR"])
    def removeCmsVideoFromPage() {
        CmsPage cmsPage = CmsPage.get(params.long("cmsPageId"))
        cmsPage.removeFromBodyVideos(CmsVideoFile.read(params.getLong("cmsVideoId")))
        taackUiService.show(new UiBlockSpecifier().ui(buildVideosTab(cmsPage)))
    }

    private static UiTableSpecifier buildCmsMenuEntryTable(CmsSubsidiary subsidiary = null, Long theId = null) {
        UiTableSpecifier t = new UiTableSpecifier()
        final CmsMenuEntry me = new CmsMenuEntry()

        t.ui {
            header {
                column {
                    label "Subsidiary"
                    label "Code"
                }
                label "Published"
                label "Title L1"
                label "Title L2"
                label "Title L3"
                label "Page"
                label "Link"
            }

            Closure rec
            rec = { List<CmsMenuEntry> menuEntries, CmsSubsidiary sub, int level ->
                rowIndent({
                    level++
                    for (def menuEntry : menuEntries.sort { it.position }) {
                        boolean muHasChildren = !menuEntry.children.isEmpty()
                        rowTree muHasChildren, {
                            rowColumn {
                                if (subsidiary) rowAction "Select Menu", ActionIcon.SELECT * IconStyle.SCALE_DOWN, this.&selectM2oMenuCloseModal as MC, menuEntry.id, ['theId': theId]
                                else {
                                    rowAction "Edit Menu", ActionIcon.EDIT * IconStyle.SCALE_DOWN, this.&editMenu as MC, menuEntry.id
                                    if (level == 1 || menuEntry.isSideMenu) rowAction "Add Child", ActionIcon.ADD * IconStyle.SCALE_DOWN, this.&editMenu as MC, ['parentMenu': menuEntry.id]
                                }
                                rowField menuEntry.code
                            }
                            rowField menuEntry.published.toString()
                            for (def l : sub.languages) rowField menuEntry.title[l.toString().toLowerCase()]?.toString()
                            for (int i = 0; i < 3 - sub.languages.size(); i++) rowField ""
                            rowField menuEntry.page?.name
                            rowField menuEntry.suffixLink
                        }
                        if (muHasChildren) {
                            rec(menuEntry.children, sub, level)
                        }
                    }
                })
            }

            List<CmsSubsidiary> subsidiaries = []
            if (subsidiary) subsidiaries << subsidiary
            else subsidiaries.addAll CmsSubsidiary.values().toList()

            for (def g : subsidiaries) {
                row {
                    rowColumn(2) {
                        rowField g.toString() + " " + g.languages*.label.join(', ')
                    }
                }
                rec(CmsMenuEntry.findAllBySubsidiaryAndParentIsNull(g), g, 0)
            }
        }
        t
    }

    def selectM2mCmsVideoCloseModal(CmsVideoFile videoFile) {
        taackUiService.show(new UiBlockSpecifier().ui {
            closeModal(videoFile.id, videoFile.toString())
        })
    }

    def selectM2mCmsPdfCloseModal(CmsPdfFile pdfFile) {
        taackUiService.show(new UiBlockSpecifier().ui {
            closeModal(pdfFile.id, pdfFile.toString())
        })
    }

    def selectM2oMenuCloseModal(CmsMenuEntry menuEntry) {
        taackUiService.show(new UiBlockSpecifier().ui {
            closeModal(menuEntry.id, menuEntry.toString())
        })
    }

    def selectM2oPageCloseModal(CmsPage page) {
        taackUiService.show(new UiBlockSpecifier().ui {
            closeModal(page.id, page.toString())
        })
    }

    def editMenu(CmsMenuEntry menuEntry) {
        CmsMenuEntry p = null
        if (params.containsKey('parentMenu')) p = CmsMenuEntry.read(params.long('parentMenu'))
        String title = menuEntry ? "Edit Menu Entry" : "Create Menu Entry"
        menuEntry ?= new CmsMenuEntry(parent: p, subsidiary: p?.subsidiary)
        taackUiService.show(new UiBlockSpecifier().ui {
            modal {
                ajaxBlock "editMenu", {
                    form new UiFormSpecifier().ui(menuEntry, {
                        if (p || menuEntry.id) {
                            hiddenField menuEntry.parent_
                            hiddenField menuEntry.subsidiary_
                        }
                        section "Menu Entry", {
                            field menuEntry.code_
                            field menuEntry.suffixLink_
                            field menuEntry.position_
                            field menuEntry.published_
                            field menuEntry.isSideMenu_
                            field menuEntry.svgRefId_
                            field menuEntry.includeInFooter_
                            if (!(p || menuEntry.id)) field menuEntry.subsidiary_
                            ajaxField menuEntry.page_, this.&selectM2oPage as MC, menuEntry.subsidiary_

                        }
                        section "Menu Entry Translation", {
                            for (def l in SupportedLanguage.values()) {
                                fieldFromMap "Title ${l.label}", menuEntry.title_, l.toString().toLowerCase()
                            }
                        }
                        formAction this.&saveCmsMenuEntry as MC
                    })
                }
            }
        })
    }

    def menuEntries() {
        UiBlockSpecifier b = new UiBlockSpecifier()

        b.ui {
            ajaxBlock "menuEntries", {
                table buildCmsMenuEntryTable(), {
                    menu this.&editMenu as MC
                }
            }
        }
        taackUiService.show(b, buildMenu())
    }

    def blocks() {
        taackUiService.show(new UiBlockSpecifier().ui({
            ajaxBlock 'blocks', {
                table(buildBlockTable()) {
                    menu this.&editBlock as MC
                }
            }
        }), buildMenu())
    }

    def search(String q) {
        taackUiService.show(cmsSearchService.buildSearchBlock(q), buildMenu(q))
    }

    def confSites() {
        def cs = new CmsConfSite()
        taackUiService.show(new UiBlockSpecifier().ui {
            ajaxBlock 'confSites', {
                table(new UiTableSpecifier().ui({
                    header {
                        sortableFieldHeader cs.subsidiary_
                        sortableFieldHeader cs.cmsSiteType_
                        sortableFieldHeader cs.mainSlideShow_
                    }
                    iterate taackFilterService.getBuilder(CmsConfSite).build(), { CmsConfSite o ->
                        rowColumn {
                            rowAction 'Edit', ActionIcon.EDIT * IconStyle.SCALE_DOWN, this.&editCmsConfSite as MC, o.id
                            rowField o.subsidiary.toString()
                        }
                        rowField o.cmsSiteType.toString()
                        if (o.mainSlideShow) rowField o.mainSlideShow.name + "(${o.mainSlideShow.id})"
                        else rowField 'Default slideshow'
                    }
                }), {
                    menu this.&editCmsConfSite as MC
                })
            }
        }, buildMenu())
    }

    def editCmsConfSite(CmsConfSite confSite) {
        confSite ?= new CmsConfSite()
        taackUiService.show(new UiBlockSpecifier().ui {
            modal {
                ajaxBlock 'editCmsConfSite', {
                    form new UiFormSpecifier().ui(confSite, {
                        field confSite.subsidiary_
                        field confSite.cmsSiteType_
                        ajaxField confSite.mainSlideShow_, this.&selectM2mCmsSlideshow as MC, confSite.subsidiary_
                        formAction this.&saveCmsConfSite as MC
                    })
                }
            }
        })
    }

    @Secured(["ROLE_ADMIN", "ROLE_CMS_MANAGER", "ROLE_CMS_DIRECTOR"])
    @Transactional
    def saveCmsConfSite() {
        taackSaveService.saveThenReloadOrRenderErrors(CmsConfSite)
    }

    def testProgressBar() {
        String pId = taackUiProgressBarService.progressStart(BlockSpec.buildBlockSpec {
            row {
                custom("""<p>Test ended</p>""", null) {
                    menuIcon(ActionIcon.EXPORT_PDF, this.&downloadBinPdf2 as MC)
                }
            }
            row {
                col {
                    barDiagram(false, 360.0, DiagramTypeSpec.HeightWidthRadio.ONE)
                }
                col {
                    barDiagram(true, 300.0, DiagramTypeSpec.HeightWidthRadio.ONE)
                }
            }
        }, 100)
        task {
            it.sleep(1_000)
            taackUiProgressBarService.progress(pId, 10)
            sleep(1_000)
            taackUiProgressBarService.progress(pId, 10)
            sleep(1_000)
            taackUiProgressBarService.progress(pId, 10)
            sleep(1_000)
            taackUiProgressBarService.progress(pId, 10)
            sleep(1_000)
            taackUiProgressBarService.progress(pId, 10)
            sleep(1_000)
            taackUiProgressBarService.progress(pId, 10)
            sleep(1_000)
            taackUiProgressBarService.progress(pId, 10)
            sleep(1_000)
            taackUiProgressBarService.progress(pId, 10)
            sleep(1_000)
            taackUiProgressBarService.progress(pId, 10)
            sleep(1_000)
            taackUiProgressBarService.progress(pId, 10)
            sleep(1_000)
            taackUiProgressBarService.progressEnded(pId)
        }
        println "test action ends"
    }

    def downloadBinPdf2() {
        User cu = springSecurityService.currentUser as User

        def pdf = new UiPrintableSpecifier().ui {
            printableHeaderLeft('8.5cm') {
                show new UiShowSpecifier().ui {
                    field null, "Printed for", Style.BOLD
                    field null, """${cu.firstName} ${cu.lastName}"""
                }, BlockSpec.Width.THIRD
                show new UiShowSpecifier().ui {
                    field """\
                        <div style="height: 2cm; text-align: center;align-content: center; width: 100%;margin-left: 1cm;">
                            ${this.taackUiService.dumpAsset("logo-taack-web.svg")}
                        </div>
                    """.stripIndent()
                }, BlockSpec.Width.THIRD
                show new UiShowSpecifier().ui {
                    field null, """${new Date()}""", Style.ALIGN_RIGHT
                }, BlockSpec.Width.THIRD

            }

            printableBody {
                // width will be set to default value (720px) if null. (No auto-fit width in PDF)
                diagram(barDiagram(false, 300.0, DiagramTypeSpec.HeightWidthRadio.HALF), BlockSpec.Width.MAX)
                diagram(barDiagram(true, 600.0, DiagramTypeSpec.HeightWidthRadio.HALF), BlockSpec.Width.MAX)
                diagram(barDiagram(true, null, DiagramTypeSpec.HeightWidthRadio.HALF), BlockSpec.Width.MAX)

                diagram(areaDiagram(true, null, DiagramTypeSpec.HeightWidthRadio.HALF), BlockSpec.Width.MAX)

                diagram pieDiagram(false, null, DiagramTypeSpec.HeightWidthRadio.ONE), BlockSpec.Width.MAX
                diagram pieDiagram(true, null, DiagramTypeSpec.HeightWidthRadio.HALF), BlockSpec.Width.MAX
            }

            printableFooter {
                show new UiShowSpecifier().ui {
                    field "<b>Taackly</b> Powered"
                }, BlockSpec.Width.MAX
            }

        }

        taackUiService.downloadPdf(pdf, 'testChart', false)
    }

    private static UiDiagramSpecifier barDiagram(boolean isStacked, BigDecimal widthInPx, DiagramTypeSpec.HeightWidthRadio heightWidthRadio) {
        new UiDiagramSpecifier().ui {
            bar(["T1", "T2", "T3", "T4"] as List<String>, isStacked, {
                dataset 'Truc1', [1.0, 2.0, 1.0, 4.0]
                dataset 'Truc2', [2.0, 0.1, 1.0, 0.0]
                dataset 'Truc3', [2.0, 0.1, 1.0, 1.0]
            }, widthInPx, heightWidthRadio) // widthInPx to define the diagram width, heightWidthRadio to define the diagram height
        }
    }
    private static UiDiagramSpecifier areaDiagram(boolean isStacked, BigDecimal widthInPx, DiagramTypeSpec.HeightWidthRadio heightWidthRadio) {
        new UiDiagramSpecifier().ui {
            area(["T1", "T2", "T3", "T4"] as List<String>, isStacked, {
                dataset 'Truc1', [1.0, 1.0, 1.0, 2.0]
                dataset 'Truc2', [2.0, 2.0, 1.0, 0.0]
                dataset 'Truc3', [3.0, 3.0, 1.0, 3.0]
            }, widthInPx, heightWidthRadio)
        }
    }
    private static UiDiagramSpecifier pieDiagram(boolean hasSlice, BigDecimal widthInPx, DiagramTypeSpec.HeightWidthRadio heightWidthRadio) {
        new UiDiagramSpecifier().ui({
            pie(hasSlice, {
                dataset("cli", 1.47)
                dataset("client", 0.28)
                dataset("client1", 0.1)
                dataset("client2", 1.45)
                dataset("client3", 0.05)
                dataset("client31", 0.05)
                dataset("client32", 0.05)
                dataset("c33", 0.05)
                dataset("client311", 0.05)
                dataset("client312", 0.05)
                dataset("client313", 0.05)
                dataset("client4", 0.8)
                dataset("client5", 2.1)
                dataset("client55", 0.1)
                dataset("client555", 0.2)
                dataset("client5555", 0.3)
                dataset("client55555", 0.3)
                dataset("admin", 1.6)
                dataset("test1", 0.05)
            }, widthInPx, heightWidthRadio)
        })
    }
    def testDiagramHtml() {
        taackUiService.show(new UiBlockSpecifier().ui({
            ajaxBlock 'blocks', {
                row {
                    // ------- Bar diagram -------
                    // widthInPx was set (Font size is always same, no matter how large/small the diagramWidth is)
                    diagram barDiagram(false, 600.0, DiagramTypeSpec.HeightWidthRadio.THIRD)
                    diagram barDiagram(false, 1500.0, DiagramTypeSpec.HeightWidthRadio.THIRD)

                    // widthInPx was not set / was set to null -> the diagram width is auto-fit to 100% of section width (by doing ZOOM)
                    // We see that everything (including font size) is zoomed
                    // zoomRate = sectionWidth / 960.0 (960px is the default width value which is saved in RawHtmlDiagramDump.visitDiagramPreparation())
                    col BlockSpec.Width.QUARTER, { diagram barDiagram(false, null, DiagramTypeSpec.HeightWidthRadio.THIRD) }
                    col BlockSpec.Width.MAX, { diagram barDiagram(false, null, DiagramTypeSpec.HeightWidthRadio.THIRD) }

                    // ------- Area diagram -------
                    diagram areaDiagram(true, 1500.0, DiagramTypeSpec.HeightWidthRadio.THIRD)

                    // ------- Pie diagram -------
                    // No slice: all information is shown
                    col { diagram pieDiagram(false, 600.0, DiagramTypeSpec.HeightWidthRadio.ONE) }
                    // has slice: make the first dataset as slice, and a part of information is hidden
                    col { diagram pieDiagram(true, 600.0, DiagramTypeSpec.HeightWidthRadio.ONE) }
                }
            }
        }), buildMenu())
    }
}
