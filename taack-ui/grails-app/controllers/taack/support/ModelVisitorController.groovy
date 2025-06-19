package taack.support

import grails.compiler.GrailsCompileStatic
import grails.plugin.springsecurity.annotation.Secured
import taack.domain.TaackMetaModelService


// TODO: Develop the UI
@GrailsCompileStatic
@Secured(['ROLE_ADMIN'])
class ModelVisitorController {
    TaackMetaModelService taackMetaModelService
    def model() {
        render taackMetaModelService.modelGraph()
    }
}
