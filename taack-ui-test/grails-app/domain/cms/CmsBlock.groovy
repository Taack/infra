package cms

import cms.config.CmsSubsidiary
import grails.compiler.GrailsCompileStatic
import taack.ast.annotation.TaackFieldEnum

@GrailsCompileStatic
@TaackFieldEnum
class CmsBlock {
    Date dateCreated
    Date lastUpdated

    String position
    CmsSubsidiary subsidiary
    CmsPage cmsPage
    CmsMenuEntry cmsMenuEntry

    static constraints = {
        position unique: 'subsidiary'
        cmsMenuEntry nullable: true
        cmsPage nullable: true
    }
}
