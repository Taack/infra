package taack.ui.dsl.kanban

import grails.util.Holders
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.render.TaackUiEnablerService
import taack.ui.dsl.common.Style

/**
 * Kanban Drawing DSL Spec. A kanban is composed of a header and columns.
 */
@CompileStatic
final class KanbanSpec {
    final IUiKanbanVisitor kanbanVisitor
    TaackUiEnablerService taackUiEnablerService = Holders.grailsApplication.mainContext.getBean('taackUiEnablerService') as TaackUiEnablerService

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
    void column(String i18n, Style style = null, MethodClosure action, Map<String, ? extends Object> params = null, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = ColumnSpec) Closure closure) {
        kanbanVisitor.visitColumn(i18n, style, taackUiEnablerService.hasAccess(action, params) ? action : null, params)
        closure.delegate = new ColumnSpec(kanbanVisitor)
        closure.call()
        kanbanVisitor.visitColumnEnd()
    }

    void column(String i18n, Style style = null, MethodClosure action, Long id, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = ColumnSpec) Closure closure) {
        kanbanVisitor.visitColumn(i18n, style, taackUiEnablerService.hasAccess(action, id) ? action : null, id ? [id: id] : null)
        closure.delegate = new ColumnSpec(kanbanVisitor)
        closure.call()
        kanbanVisitor.visitColumnEnd()
    }
}
