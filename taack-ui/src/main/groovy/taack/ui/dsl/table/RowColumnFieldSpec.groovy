package taack.ui.dsl.table

import grails.util.Holders
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.ast.type.FieldInfo
import taack.ast.type.GetMethodReturn
import taack.render.TaackUiEnablerService
import taack.ui.dsl.branching.BranchingSpec
import taack.ui.dsl.common.ActionIcon
import taack.ui.dsl.common.Style
import taack.ui.dsl.helper.Utils

/**
 * Specify fields to be drawn in a row or a rowColumn.
 */
@CompileStatic
class RowColumnFieldSpec implements BranchingSpec {
    final IUiTableVisitor tableVisitor

    TaackUiEnablerService taackUiEnablerService = Holders.grailsApplication.mainContext.getBean('taackUiEnablerService') as TaackUiEnablerService

    RowColumnFieldSpec(IUiTableVisitor tableVisitor) {
        this.tableVisitor = tableVisitor
    }

    void rowField(final String value, final Style style = null) {
        tableVisitor.visitRowField(value, style)
    }

    void rowField(final FieldInfo field, final String format = null, final Style style = null) {
        tableVisitor.visitRowField(field, format, style)
    }

    void rowField(final GetMethodReturn field, final String format = null, final Style style = null) {
        tableVisitor.visitRowField(field, format, style)
    }

    void rowAction(final String i18n, final ActionIcon icon, final Long id, String label) {
        tableVisitor.visitRowAction(i18n, icon, id, label, null, true)
    }

    void rowAction(final String i18n = null, final ActionIcon icon, final MethodClosure action, final Map params) {
        rowAction(i18n, icon, action, null, params)
    }

    void rowAction(final String i18n = null, final ActionIcon icon, final MethodClosure action, final Long id) {
        rowAction(i18n, icon, action, id, null)
    }

    void rowAction(final String i18n = null, final ActionIcon icon, final MethodClosure action, final Long id, final Map params) {
        if (taackUiEnablerService.hasAccess(action, id, params)) {
            Map<String, ?> p = params ?: [:]
            p.put('id', id)
            tableVisitor.visitRowAction(i18n, icon, Utils.getControllerName(action), action.method, null, p, true)
        }
    }

    void rowAction(final String linkText, final MethodClosure action, final Map params) {
        rowAction(linkText, action, null, params)
    }

    void rowAction(final String linkText, final MethodClosure action, final Long id) {
        rowAction(linkText, action, id, null)
    }

    void rowAction(final String linkText, final MethodClosure action, final Long id, final Map params) {
        if (taackUiEnablerService.hasAccess(action, id, params)) {
            Map<String, ?> p = params ?: [:]
            p.put('id', id)
            tableVisitor.visitRowAction(linkText, Utils.getControllerName(action), action.method, null, p, true)
        }
    }

}
