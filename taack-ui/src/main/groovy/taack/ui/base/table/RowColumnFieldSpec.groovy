package taack.ui.base.table

import grails.util.Holders
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.ast.type.FieldInfo
import taack.ast.type.GetMethodReturn
import taack.render.TaackUiEnablerService
import taack.ui.base.common.ActionIcon
import taack.ui.base.common.Style
import taack.ui.base.helper.Utils

/**
 * Specify fields to be drawn in a row or a rowColumn.
 */
@CompileStatic
class RowColumnFieldSpec {
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

    void footerButton(String i18n = null, MethodClosure action, Long id = null, Map additionalParams = null) {
        if (taackUiEnablerService.hasAccess(action, id, additionalParams)) tableVisitor.visitFooterButton(i18n, Utils.getControllerName(action), action.method, id, additionalParams)
    }
}
