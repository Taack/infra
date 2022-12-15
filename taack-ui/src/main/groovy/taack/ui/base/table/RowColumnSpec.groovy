package taack.ui.base.table

import groovy.transform.CompileStatic

@CompileStatic
final class RowColumnSpec extends RowColumnFieldSpec {

    RowColumnSpec(IUiTableVisitor tableVisitor) {
        super(tableVisitor)
    }

    void rowColumn(Integer colSpan = null, Integer rowSpan = null, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = RowColumnFieldSpec) Closure closure) {
        tableVisitor.visitRowColumn(colSpan, rowSpan)
        closure.delegate = new RowColumnFieldSpec(tableVisitor)
        closure.call()
        tableVisitor.visitRowColumnEnd()
    }
}
