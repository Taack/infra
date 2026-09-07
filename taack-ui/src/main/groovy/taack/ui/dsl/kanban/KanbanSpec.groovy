package taack.ui.dsl.kanban

import grails.util.Holders
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.ui.ITaackUiEnabler
import taack.ui.TrueTaackUiEnabler
import taack.ui.dsl.common.Style
/**
 * Kanban Drawing DSL Spec. A kanban is composed of a header and columns.
 */
@CompileStatic
final class KanbanSpec {
    final IUiKanbanVisitor kanbanVisitor
    ITaackUiEnabler taackUiEnabler = (Holders.grailsApplication.mainContext.getBean('taackUiEnablerService') ?: new TrueTaackUiEnabler()) as ITaackUiEnabler

    KanbanSpec(IUiKanbanVisitor kanbanVisitor) {
        this.kanbanVisitor = kanbanVisitor
    }

    /**
     * Draw the column of the Kanban. Should contains:
     *
     * @param MethodClosure drop action
     * @param action params
     * @param Closure header content
     */
    void column(String i18n, Style style = null, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = ColumnHeaderSpec) Closure headerClosure = null, MethodClosure action = null, Map<String, ? extends Object> params = null, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = ColumnSpec) Closure closure) {
        kanbanVisitor.visitColumn(taackUiEnabler.hasAccess(action, params) ? action : null, params)
        renderColumn(i18n, style, headerClosure, closure)
    }

    void column(String i18n, Style style = null, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = ColumnHeaderSpec) Closure headerClosure = null, MethodClosure action, Long id, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = ColumnSpec) Closure closure) {
        kanbanVisitor.visitColumn(taackUiEnabler.hasAccess(action, id) ? action : null, id ? [id: id] : null)
        renderColumn(i18n, style, headerClosure, closure)
    }

    private void renderColumn(String i18n, Style style, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = ColumnHeaderSpec) Closure headerClosure, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = ColumnSpec) Closure closure) {
        kanbanVisitor.visitColumnHeader(i18n, style)
        if (headerClosure) {
            headerClosure.delegate = new ColumnHeaderSpec(kanbanVisitor)
            headerClosure.call()
        }
        kanbanVisitor.visitColumnHeaderEnd()
        closure.delegate = new ColumnSpec(kanbanVisitor)
        closure.call()
        kanbanVisitor.visitColumnEnd()
    }
}
