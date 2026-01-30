package taack.ui.dsl

import groovy.transform.CompileStatic
import taack.ui.dsl.kanban.IUiKanbanVisitor
import taack.ui.dsl.kanban.KanbanSpec

/**
 * Class for creating kanban. Those kanbans could be used with a filter.
 *
 * <p>A simple Kanban is created with:
 * {@link taack.ui.dsl.block.BlockSpec#kanban(UiKanbanSpecifier)}
 */
@CompileStatic
final class UiKanbanSpecifier {

    Closure closure

    /**
     * Kanban Specifier Builder
     *
     * See {@link KanbanSpec} for more information
     *
     * @param closure The kanban specification
     * @return Itself
     */
    UiKanbanSpecifier ui(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = KanbanSpec) final Closure closure) {
        this.closure = closure
        this
    }

    /**
     * Allow to visit the kanban.
     *
     * @param kanbanVisitor
     */
    void visitKanban(final IUiKanbanVisitor kanbanVisitor) {
        if (kanbanVisitor && closure) {
            closure.delegate = new KanbanSpec(kanbanVisitor)
            kanbanVisitor.visitKanban()
            closure.call()
            kanbanVisitor.visitKanbanEnd()
        }
    }

    void visitKanbanWithNoFilter(final IUiKanbanVisitor kanbanVisitor) {
        if (kanbanVisitor && closure) {
            closure.delegate = new KanbanSpec(kanbanVisitor)
            kanbanVisitor.visitKanbanWithoutFilter()
            closure.call()
            kanbanVisitor.visitKanbanEnd()
        }
    }

}