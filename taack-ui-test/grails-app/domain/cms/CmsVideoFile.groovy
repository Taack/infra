package cms

import crew.User
import grails.compiler.GrailsCompileStatic
import taack.ast.annotation.TaackFieldEnum

@GrailsCompileStatic
@TaackFieldEnum
class CmsVideoFile {
    Date dateCreated
    Date lastUpdated

    User userCreated
    User userUpdated

    Boolean hide = false

    CmsImage preview
    Map<String, String> youtubeI18n
    Map<String, String> altText
    Integer width
    Integer height

    String filePath
    String contentType
    String contentShaOne
    String originalName

    CmsPage cmsPage

    static constraints = {
        contentType nullable: true
        youtubeI18n nullable: true
        preview nullable: true
        altText nullable: true
        width nullable: true
        height nullable: true
        filePath nullable: true, widget: 'filePath'
        contentShaOne nullable: true
        originalName nullable: true
        cmsPage nullable: true
    }
}
