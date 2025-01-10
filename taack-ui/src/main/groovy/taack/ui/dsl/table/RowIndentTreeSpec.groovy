package taack.ui.dsl.table

import groovy.transform.CompileStatic

@CompileStatic
final class RowIndentTreeSpec {
    final IUiTableVisitor tableVisitor

    RowIndentTreeSpec(IUiTableVisitor tableVisitor) {
        this.tableVisitor = tableVisitor
    }

    /**
     * Row container that can be expended as a tree. Can be nested.
     *
     * @param hasChildren If true, the subsequent rows will be collapsed under the current row
     * @param closure Contains columns
     */
    void rowTree(boolean hasChildren, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = RowColumnSpec) Closure closure) {
        tableVisitor.visitRow(null, hasChildren)
        closure.delegate = new RowColumnSpec(tableVisitor)
        closure.call()
        tableVisitor.visitRowEnd()
    }

    void rowIndent(final Boolean isExpended = false, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = RowIndentTreeSpec) Closure closure) {
        tableVisitor.visitRowIndent(isExpended)
        closure.delegate = this
        closure.call()
        tableVisitor.visitRowIndentEnd()
    }
}
