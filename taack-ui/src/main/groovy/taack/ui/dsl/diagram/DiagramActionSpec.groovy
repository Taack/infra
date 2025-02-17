package taack.ui.dsl.diagram

import grails.util.Holders
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.render.TaackUiEnablerService
import taack.ui.dsl.helper.Utils

@CompileStatic
class DiagramActionSpec {
    IUiDiagramVisitor diagramVisitor

    TaackUiEnablerService taackUiEnablerService = Holders.grailsApplication.mainContext.getBean('taackUiEnablerService') as TaackUiEnablerService

    void diagramAction(final MethodClosure action) {
        diagramAction(action ,null, null)
    }

    void diagramAction(final MethodClosure action, final Long id) {
        diagramAction(action ,id, null)
    }

    void diagramAction(final MethodClosure action, final Map params) {
        diagramAction(action ,null, params)
    }

    void diagramAction(final MethodClosure action, final Long id, final Map params) {
        if (taackUiEnablerService.hasAccess(action, id, params)) {
            diagramVisitor.visitDiagramAction(Utils.getControllerName(action), action.method, id, params)
        }
    }
}
