package taack.ui.base.table

import grails.util.Holders
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import org.grails.datastore.gorm.GormEntity
import taack.ast.type.FieldInfo
import taack.ast.type.GetMethodReturn
import taack.base.TaackUiEnablerService
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

    void rowField(final FieldInfo field, final String format = null, final Style style = null) {
        tableVisitor.visitRowField(field, format, style)
    }

    void rowField(final GetMethodReturn field, final Style style = null) {
        tableVisitor.visitRowField(field, style)
    }

    void rowField(final String value, final Style style = null) {
        tableVisitor.visitRowField(value, style)
    }

    void rowField(final Long value, final Style style = null) {
        tableVisitor.visitRowField(value, style)
    }

    void rowField(final BigDecimal value, final Style style = null) {
        tableVisitor.visitRowField(value, null as NumberFormat, style)
    }

    void rowField(final BigDecimal value, final NumberFormat format, final Style style = null) {
        tableVisitor.visitRowField(value, format, style)
    }

    void rowField(final Map value, final Style style = null) {
        tableVisitor.visitRowField(value, style)
    }

    void rowField(final EnumStyle value, final Style style = null) {
        tableVisitor.visitRowField(value, style)
    }

    void rowField(final Date value, final String format = null, final Style style = null) {
        tableVisitor.visitRowField(value, format, style)
    }

    void rowLink(final String i18n, final ActionIcon icon, final Long id, String label, final Boolean isAjax = true) {
        tableVisitor.visitRowLink(i18n, icon, id, label, null, isAjax)
    }

    void rowLink(final String i18n, final ActionIcon icon, final String controller, final String action, final Long id = null, final Boolean isAjax = true) {
        if (taackUiEnablerService.hasAccess(controller, action, id, null)) tableVisitor.visitRowLink(i18n, icon, controller, action, id, null, isAjax)
    }

    /**
     * Add a link to the target action in a table
     *
     * @param i18n Hover text
     * @param icon Icon to use
     * @param action Target action
     * @param id (optional) ID parameter
     * @param isAjax Default to true, if true the target action is an ajax one, if false, the target action will redraw the entire page.
     */
    void rowLink(final String i18n, final ActionIcon icon, final MethodClosure action, final Long id = null, final Boolean isAjax = true) {
        if (taackUiEnablerService.hasAccess(action, id)) tableVisitor.visitRowLink(i18n, icon, Utils.getControllerName(action), action.method, id, null, isAjax)
    }

    void rowLink(final String i18n, final ActionIcon icon, final MethodClosure action, final Long id, final Map<String, ?> params, final Boolean isAjax = true) {
        if (taackUiEnablerService.hasAccess(action, id, params)) {
            Map<String, ?> p = params ?: [:]
            p.put('id', id)
            tableVisitor.visitRowLink(i18n, icon, Utils.getControllerName(action), action.method, null, p, isAjax)
        }
    }

    void rowLink(final String i18n, final ActionIcon icon, final String controller, final String action, final Map<String, ?> params, final Boolean isAjax = true) {
        if (taackUiEnablerService.hasAccess(controller, action, null, params)) tableVisitor.visitRowLink(i18n, icon, controller, action, null, params, isAjax)
    }

    void rowLink(final String i18n, final ActionIcon icon, final MethodClosure action, final Map<String, ?> params, final Boolean isAjax = true) {
        if (taackUiEnablerService.hasAccess(action, params)) tableVisitor.visitRowLink(i18n, icon, Utils.getControllerName(action), action.method, null, params, isAjax)
    }

    void footerButton(String i18n, MethodClosure action, Long id = null, Map<String, ?> additionalParams = null) {
        if (taackUiEnablerService.hasAccess(action, id, additionalParams)) tableVisitor.visitFooterButton(i18n, Utils.getControllerName(action), action.method, id, additionalParams)
    }
}
