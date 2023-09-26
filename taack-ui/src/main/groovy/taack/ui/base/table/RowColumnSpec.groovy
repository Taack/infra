package taack.ui.base.table

import groovy.transform.CompileStatic
import taack.ui.base.common.Style

@CompileStatic
final class RowColumnSpec extends RowColumnFieldSpec {

    RowColumnSpec(IUiTableVisitor tableVisitor) {
        super(tableVisitor)
    }

    void rowColumn(Integer colSpan = null, Integer rowSpan = null, Style style = null, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = RowColumnFieldSpec) Closure closure) {
        tableVisitor.visitRowColumn(colSpan, rowSpan, style)
        closure.delegate = new RowColumnFieldSpec(tableVisitor)
        closure.call()
        tableVisitor.visitRowColumnEnd()
    }
}
