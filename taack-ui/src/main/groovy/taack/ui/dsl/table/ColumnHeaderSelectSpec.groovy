package taack.ui.dsl.table

import grails.util.Holders
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.render.TaackUiEnablerService
import taack.ui.dsl.helper.Utils

@CompileStatic
class ColumnHeaderSelectSpec {
    final IUiTableVisitor tableVisitor

    TaackUiEnablerService taackUiEnablerService = Holders.grailsApplication.mainContext.getBean('taackUiEnablerService') as TaackUiEnablerService

    ColumnHeaderSelectSpec(IUiTableVisitor tableVisitor) {
        this.tableVisitor = tableVisitor
    }

    void columnSelectButton(final String buttonText, final MethodClosure action, final Map params = null) {
        if (taackUiEnablerService.hasAccess(action, null, params)) {
            tableVisitor.visitColumnSelectButton(buttonText, Utils.getControllerName(action), action.method, params, null)
        } else {
            tableVisitor.visitColumnSelectButton(buttonText, null, null, null, null)
        }
    }
}
