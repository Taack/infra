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
import taack.ui.style.EnumStyle

import java.text.NumberFormat

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

    void rowField(final String value, final Style style = null, final MethodClosure action = null, final Long id = null) {
        tableVisitor.visitRowField(value, style, action && taackUiEnablerService.hasAccess(action, id) ? Utils.getControllerName(action) : null, action?.method, id)
    }

    void rowField(final FieldInfo field, final String format = null, final Style style = null, final MethodClosure action = null, final Long id = null) {
        tableVisitor.visitRowField(field, format, style, action && taackUiEnablerService.hasAccess(action, id) ? Utils.getControllerName(action) : null, action?.method, id)
    }

    void rowField(final GetMethodReturn field, final Style style = null, final MethodClosure action = null, final Long id = null) {
        tableVisitor.visitRowField(field, style, action && taackUiEnablerService.hasAccess(action, id) ? Utils.getControllerName(action) : null, action?.method, id)
    }

    void rowLink(final String i18n, final ActionIcon icon, final Long id, String label) {
        tableVisitor.visitRowLink(i18n, icon, id, label, null, true)
    }

    void rowLink(final String i18n = null, final ActionIcon icon, final MethodClosure action, final Map params) {
        rowLink(i18n, icon, action, null, params)
    }
    void rowLink(final String i18n = null, final ActionIcon icon, final MethodClosure action, final Long id, final Map params = null) {
        if (taackUiEnablerService.hasAccess(action, id, params)) {
            Map<String, ?> p = params ?: [:]
            p.put('id', id)
            tableVisitor.visitRowLink(i18n, icon, Utils.getControllerName(action), action.method, null, p, true)
        }
    }

    void footerButton(String i18n = null, MethodClosure action, Long id = null, Map additionalParams = null) {
        if (taackUiEnablerService.hasAccess(action, id, additionalParams)) tableVisitor.visitFooterButton(i18n, Utils.getControllerName(action), action.method, id, additionalParams)
    }
}
