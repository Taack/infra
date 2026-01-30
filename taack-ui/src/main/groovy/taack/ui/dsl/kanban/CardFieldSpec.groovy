package taack.ui.dsl.kanban

import grails.util.Holders
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import org.springframework.context.i18n.LocaleContextHolder
import taack.ast.type.FieldInfo
import taack.ast.type.GetMethodReturn
import taack.render.TaackUiEnablerService
import taack.ui.dsl.common.ActionIcon
import taack.ui.dsl.common.Style
import taack.ui.dsl.helper.Utils

import java.text.NumberFormat

/**
 * Base class to define fields in the card
 *
 * <p>Specify fields to be drawn in the card
 */
@CompileStatic
class CardFieldSpec {
    final IUiKanbanVisitor kanbanVisitor

    TaackUiEnablerService taackUiEnablerService = Holders.grailsApplication.mainContext.getBean('taackUiEnablerService') as TaackUiEnablerService

    CardFieldSpec(IUiKanbanVisitor kanbanVisitor) {
        this.kanbanVisitor = kanbanVisitor
    }

    void cardFieldRaw(final String value, final Style style = null) {
        kanbanVisitor.visitCardFieldRaw(value, style)
    }

    void cardField(final FieldInfo field, final String format = null, final Style style = null) {
        kanbanVisitor.visitCardField(field,null, format, style)
    }

    void cardField(final FieldInfo field, Long id, final String format = null, final Style style = null) {
        kanbanVisitor.visitCardField(field, id, format, style)
    }

    void cardField(final String value, final Style style = null) {
        kanbanVisitor.visitCardField(value, style)
    }

    void cardField(final BigDecimal value, NumberFormat nf, final Style style = Style.ALIGN_RIGHT) {
        kanbanVisitor.visitCardField(value, nf, style)
    }

    void cardField(final BigDecimal value, final Style style = Style.ALIGN_RIGHT) {
        cardField(value, NumberFormat.getInstance(LocaleContextHolder.locale), style)
    }

    void rowField(final GetMethodReturn field, final String format = null, final Style style = null) {
        kanbanVisitor.visitCardField(field, format, style)
    }

    void cardAction(final String i18n = null, final ActionIcon icon, final MethodClosure action, final Long id, final Map params) {
        if (taackUiEnablerService.hasAccess(action, id, params)) {
            Map<String, ?> p = params ?: [:]
            p.put('id', id)
            kanbanVisitor.visitCardAction(i18n, icon, Utils.getControllerName(action), action.method, null, p, true)
        }
    }

    void cardAction(final String i18n = null, final ActionIcon icon, final MethodClosure action, final Long id) {
        cardAction(i18n, icon, action, id, null)
    }

    void cardAction(final String i18n = null, final ActionIcon icon, final Long id, String label) {
        cardAction(i18n, icon, id?.toString(), label)
    }

    void cardAction(final String i18n = null, final ActionIcon icon, final String key, String label) {
        kanbanVisitor.visitCardAction(i18n, icon, key, label)
    }

    void cardAction(final String i18n = null, final ActionIcon icon, final MethodClosure action, final Map params) {
        cardAction(i18n, icon, action, null, params)
    }

    void cardAction(final String linkText, final MethodClosure action, final Long id, final Map params, boolean isAjax = true) {
        if (linkText && taackUiEnablerService.hasAccess(action, id, params)) {
            Map<String, ?> p = params ?: [:]
            p.put('id', id)
            kanbanVisitor.visitCardAction(linkText, Utils.getControllerName(action), action.method, null, p, isAjax)
        } else {
            kanbanVisitor.visitCardFieldRaw(linkText, null)
        }
    }

    void cardAction(final String linkText, final MethodClosure action, final Map params) {
        cardAction(linkText, action, null, params)
    }

    void cardAction(final String linkText, final MethodClosure action, final Long id) {
        cardAction(linkText, action, id, null)
    }
}
