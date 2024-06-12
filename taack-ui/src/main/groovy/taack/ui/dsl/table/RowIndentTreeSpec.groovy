package taack.ui.dsl.table

import groovy.transform.CompileStatic

@CompileStatic
final class RowIndentTreeSpec extends RowColumnSpec {

    RowIndentTreeSpec(IUiTableVisitor tableVisitor) {
        super(tableVisitor)
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
}
