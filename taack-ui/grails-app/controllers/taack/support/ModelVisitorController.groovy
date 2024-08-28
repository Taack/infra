package taack.support

import grails.compiler.GrailsCompileStatic
import grails.plugin.springsecurity.annotation.Secured
import taack.domain.TaackMetaModelService
import taack.domain.TaackSearchService


// TODO: Develop the UI
@GrailsCompileStatic
@Secured(["ROLE_ADMIN"])
class ModelVisitorController {
    TaackMetaModelService taackMetaModelService
    TaackSearchService taackSearchService
    def model() {
        render taackMetaModelService.modelGraph()
    }

    def indexAll() {
        taackSearchService.indexAll()
        render "OK"
    }

}
