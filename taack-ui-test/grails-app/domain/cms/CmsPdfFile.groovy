package cms

import crew.User
import grails.compiler.GrailsCompileStatic
import taack.ast.annotation.TaackFieldEnum

@GrailsCompileStatic
@TaackFieldEnum
class CmsPdfFile {
    Date dateCreated
    Date lastUpdated

    User userCreated
    User userUpdated

    Boolean hide = false

    String filePath
    Map<String, String> altText
    String contentShaOne
    String originalName

    CmsPage cmsPage

    static constraints = {
        altText nullable: true
        filePath widget: "filePath"
        contentShaOne nullable: true
        originalName nullable: true
        cmsPage nullable: true
    }
}
