package taack.ui.dsl.kanban

import groovy.transform.CompileStatic
import taack.ui.dsl.common.Style

@CompileStatic
class ColumnHeaderSpec {
    final IUiKanbanVisitor kanbanVisitor

    ColumnHeaderSpec(IUiKanbanVisitor kanbanVisitor) {
        this.kanbanVisitor = kanbanVisitor
    }

    void custom(final String html, final Style style = null) {
        kanbanVisitor.visitCustom(html, style)
    }
}
