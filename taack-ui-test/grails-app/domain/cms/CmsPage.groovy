package cms

import attachment.Attachment
import cms.config.CmsSubsidiary
import crew.User
import grails.compiler.GrailsCompileStatic
import groovy.transform.CompileStatic
import taack.ast.annotation.TaackFieldEnum
import taack.ui.EnumOption

@CompileStatic
enum CmsPageLayout {
    REGULAR,
    NO_SIDE_COLUMN
}

@CompileStatic
enum CmsPageType {
    BLOG,
    PAGE,
    NEWS,
    SLIDESHOW

    static EnumOption[] getEnumOptionsPage() {
        EnumOption[] res = new EnumOption[3]
        res[0] = new EnumOption(BLOG.toString(), BLOG.toString())
        res[1] = new EnumOption(PAGE.toString(), PAGE.toString())
        res[2] = new EnumOption(NEWS.toString(), NEWS.toString())
        res
    }
}

@GrailsCompileStatic
@TaackFieldEnum
class CmsPage {

    CmsSubsidiary subsidiary
    Date dateCreated
    Date lastUpdated

    User userCreated
    User userUpdated

    Boolean published = false

    CmsPageLayout pageLayout = CmsPageLayout.NO_SIDE_COLUMN
    CmsPageType pageType

    String name

    Map<String, String> urlPart
    Map<String, String> title
    Map<String, String> hatContent
    Map<String, String> bodyContent

    CmsImage mainImage
    CmsVideoFile mainVideo

    Set<CmsImage> bodyImages
    Set<CmsPdfFile> bodyPdfs
    Set<CmsVideoFile> bodyVideos

    List<Attachment> bodyContentAttachmentList

    Integer width = 960
    Integer height = 480
    Boolean controls = true
    Boolean progress = true
    Integer autoSlide = 3000

    static hasMany = [
            bodyContentAttachmentList: Attachment,
            bodyImages: CmsImage,
            bodyPdfs: CmsPdfFile,
            bodyVideos: CmsVideoFile
    ]

    static mappedBy = [bodyImages: "none", bodyVideos: "none", bodyPdfs: "none"]

    static constraints = {
        name nullable: true
        urlPart nullable: true
        title nullable: true
        hatContent nullable: true
        bodyContent nullable: true, widget: "asciidoc"
        mainImage nullable: true
        mainVideo nullable: true
        bodyImages nullable: true
        width nullable: true
        height nullable: true
        controls nullable: true
        progress nullable: true
        autoSlide nullable: true
    }

    static mapping = {
        tablePerHierarchy true
    }

    @Override
    String toString() {
        return "CmsPage: ${pageType}, ${name}($id)"
    }
}
