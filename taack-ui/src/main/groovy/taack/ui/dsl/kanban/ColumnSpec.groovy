package taack.ui.dsl.kanban

import groovy.transform.CompileStatic
import taack.ast.type.FieldInfo
import taack.ui.dsl.common.Style

/**
 * {@link taack.ui.dsl.kanban.KanbanSpec(groovy.lang.Closure)} delegated class.
 *
 * <p>A column can contains many cards
 */
@CompileStatic
final class ColumnSpec {
    final IUiKanbanVisitor kanbanVisitor

    ColumnSpec(IUiKanbanVisitor kanbanVisitor) {
        this.kanbanVisitor = kanbanVisitor
    }

    /**
     * Display kanban column header
     *
     * @param closure
     */
    void header(final String i18n, final Style style = null) {
        kanbanVisitor.visitColumnHeader(i18n, style)
    }

    void card(FieldInfo cardId, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = CardFieldSpec) Closure closure) {
        kanbanVisitor.visitCard(cardId)
        closure.delegate = new CardFieldSpec(kanbanVisitor)
        closure.call()
        kanbanVisitor.visitCardEnd()
    }
}
