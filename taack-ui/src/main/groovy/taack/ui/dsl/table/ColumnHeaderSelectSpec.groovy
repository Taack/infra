package taack.ui.dsl.table


import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.ui.ITaackUiEnabler
import taack.ui.dsl.helper.Utils
import taack.ui.dump.Parameter

@CompileStatic
class ColumnHeaderSelectSpec {
    final IUiTableVisitor tableVisitor

    ITaackUiEnabler taackUiEnabler = Parameter.taackUiEnabler

    ColumnHeaderSelectSpec(IUiTableVisitor tableVisitor) {
        this.tableVisitor = tableVisitor
    }

    void columnSelectButton(final String buttonText, final MethodClosure action, final Map params = null) {
        if (taackUiEnabler.hasAccess(action, null, params)) {
            tableVisitor.visitColumnSelectButton(buttonText, Utils.getControllerName(action), action.method, params, null)
        } else {
            tableVisitor.visitColumnSelectButton(buttonText, null, null, null, null)
        }
    }
}
