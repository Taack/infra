package taack.ui.dsl.table

import grails.util.Holders
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import org.springframework.context.i18n.LocaleContextHolder
import taack.ast.type.FieldInfo
import taack.ast.type.GetMethodReturn
import taack.render.TaackUiEnablerService
import taack.render.TaackUiService
import taack.ui.IEnumOptions
import taack.ui.dsl.branching.BranchingSpec
import taack.ui.dsl.common.ActionIcon
import taack.ui.dsl.common.Style
import taack.ui.dsl.helper.Utils

import java.text.NumberFormat

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

    void rowFieldRaw(final String value, final Style style = null) {
        tableVisitor.visitRowFieldRaw(value, style)
    }

    void rowField(final String value, final Style style = null) {
        tableVisitor.visitRowField(value, style)
    }

    void rowField(final BigDecimal value, NumberFormat nf, final Style style = Style.ALIGN_RIGHT) {
        tableVisitor.visitRowField(value, nf, style)
    }

    void rowField(final BigDecimal value, final Style style = Style.ALIGN_RIGHT) {
        rowField(value, NumberFormat.getInstance(LocaleContextHolder.locale), style)
    }

    void rowField(final FieldInfo field, Long id, final String format = null, final Style style = null) {
        tableVisitor.visitRowField(field, id, format, style)
    }

    void rowField(final FieldInfo field, final String format = null, final Style style = null) {
        tableVisitor.visitRowField(field, format, style)
    }

    void rowFieldEdit(final FieldInfo field, final String format = null, final Style style = null, IEnumOptions eos = null) {
        MethodClosure mc = TaackUiService.registerFieldEditEditMethod(field)
        if (mc && taackUiEnablerService.hasAccess(mc)) {
            tableVisitor.visitRowFieldEdit(field, format, style, eos)
        } else tableVisitor.visitRowField(field, format, style)
    }

    void rowField(final GetMethodReturn field, final String format = null, final Style style = null) {
        tableVisitor.visitRowField(field, format, style)
    }

    void rowAction(final String i18n = null, final ActionIcon icon, final Long id, String label) {
        rowAction(i18n, icon, id?.toString(), label)
    }

    void rowAction(final String i18n = null, final ActionIcon icon, final String key, String label) {
        tableVisitor.visitRowAction(i18n, icon, key, label)
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

    void rowAction(final String linkText, final MethodClosure action, final Long id, final Map params, boolean isAjax = true) {
        if (linkText && taackUiEnablerService.hasAccess(action, id, params)) {
            Map<String, ?> p = params ?: [:]
            p.put('id', id)
            tableVisitor.visitRowAction(linkText, Utils.getControllerName(action), action.method, null, p, isAjax)
        } else {
            tableVisitor.visitRowFieldRaw(linkText, null)
        }
    }

    void rowSelect(final String value, final boolean isSelectable = true) {
        tableVisitor.visitRowSelect(value, isSelectable)
    }

    /**
     * Call drop action when an Item is dropped into the next sibling column
     *
     * @param dropAction
     * @param dropActionParams
     */
    void cellDropAction(MethodClosure dropAction, Map<String, ? extends Serializable> dropActionParams = null) {
        tableVisitor.visitCellDropAction(dropAction, dropActionParams)
    }
}
