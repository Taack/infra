package cms

import cms.config.CmsSubsidiary
import crew.User
import grails.compiler.GrailsCompileStatic
import grails.gorm.DetachedCriteria
import taack.ast.annotation.TaackFieldEnum

@GrailsCompileStatic
@TaackFieldEnum
class CmsMenuEntry {
    Date dateCreated
    Date lastUpdated

    User userCreated
    User userUpdated

    CmsSubsidiary subsidiary
    String code
    Map title
    CmsMenuEntry parent

    Boolean isSideMenu = false
    String svgRefId

    CmsPage page

    String suffixLink

    boolean published = true
    Boolean includeInFooter
    Integer position = 0

    static constraints = {
        title nullable: true
        code updateable: false
        includeInFooter nullable: true
        parent nullable: true
        page nullable: true
        suffixLink nullable: true
        isSideMenu nullable: true
        svgRefId nullable: true
    }

    static mapping = {
        code unique: "subsidiary"
    }

    List<CmsMenuEntry> getChildren() {
        CmsMenuEntry.findAllWhere(parent: this)?.sort { it.position }
    }

    static DetachedCriteria<CmsMenuEntry> parents = where {
        parent == null
    }

    static DetachedCriteria<CmsMenuEntry> parentsForBranding(CmsSubsidiary brand) {
        where {
            parent == null && subsidiary == brand
        }
    }
}
