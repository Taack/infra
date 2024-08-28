package cms

import cms.config.CmsSubsidiary
import grails.compiler.GrailsCompileStatic
import taack.ast.annotation.TaackFieldEnum

enum CmsSiteType {
    REGULAR,
    WITH_SUBSCRIPTION,
    WITH_ESHOP
}

@GrailsCompileStatic
@TaackFieldEnum
class CmsConfSite {

    CmsSubsidiary subsidiary
    CmsPage mainSlideShow
    CmsSiteType cmsSiteType = CmsSiteType.REGULAR

    static constraints = {
        subsidiary unique: true
        mainSlideShow nullable: true
    }

    @Override
    String toString() {
        return "Conf: ${subsidiary}, ${mainSlideShowId}, ${cmsSiteType} ($id)..."
    }
}
