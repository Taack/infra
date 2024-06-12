package cms

import crew.User
import grails.compiler.GrailsCompileStatic
import taack.ast.annotation.TaackFieldEnum

enum ImageType {
    MEDIA_POSTER,
    PAGE_PREVIEW,
    PAGE_CONTENT,
    PAGE_PICTOGRAM,
    SLIDESHOW,
    APPLICATION_BACKGROUND
}

@TaackFieldEnum
@GrailsCompileStatic
class CmsImage {
    Date dateCreated
    Date lastUpdated

    User userCreated
    User userUpdated

    Boolean hide = false
    String filePath
    String contentType
    String contentShaOne
    String originalName
//    Map<String, String> filePathI18n
    Map<String, String> altText
    Integer width
    Integer height

    ImageType imageType = ImageType.PAGE_CONTENT

    CmsPage cmsPage

    static constraints = {
        altText nullable: true
        width nullable: true
        height nullable: true
        hide nullable: true
        filePath widget: "filePath"
//        filePathI18n nullable: true, widget: "filePath"
        cmsPage nullable: true
        originalName nullable: true
        contentShaOne nullable: true
    }

    String getFileName() {
        filePath.substring(filePath.lastIndexOf('/') + 1)
    }

    @Override
    String toString() {
        return "$fileName - $id"
    }
}
