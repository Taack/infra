package cms

import grails.compiler.GrailsCompileStatic
import taack.ast.annotation.TaackFieldEnum

@GrailsCompileStatic
@TaackFieldEnum
class CmsInsert {
    Date dateCreated
    Date lastUpdated

    int x
    int y
    Integer width
    Map hat
    Map title

    CmsPage cmsPage
    CmsImage imageApplication
    Long itemId
    Long subFamilyId
    Long rangeId

    String additionalClasses

    static constraints = {
        cmsPage nullable: true
        imageApplication nullable: true
        itemId nullable: true
        title nullable: true
        hat nullable: true, widget: "textarea"
        width nullable: true
        additionalClasses nullable: true
        imageApplication nullable: true
        subFamilyId nullable: true
        rangeId nullable: true
    }

    static mapping = {
        autoTimestamp true
    }
}
