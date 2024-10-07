package taack.ui.dsl.table

import groovy.transform.CompileStatic
import taack.ui.dsl.common.Style

@CompileStatic
class RowColumnSpec extends RowColumnFieldSpec {

    RowColumnSpec(IUiTableVisitor tableVisitor) {
        super(tableVisitor)
    }

    void innerRow(final Style style = null, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = RowColumnSpec) Closure closure) {
        tableVisitor.visitRow(style, false)
        closure.delegate = new RowColumnSpec(tableVisitor)
        closure.call()
        tableVisitor.visitRowEnd()
    }

    void rowColumn(Integer colSpan = null, Integer rowSpan = null, Style style = null, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = RowColumnFieldSpec) Closure closure) {
        tableVisitor.visitRowColumn(colSpan, rowSpan, style)
        closure.delegate = new RowColumnFieldSpec(tableVisitor)
        closure.call()
        tableVisitor.visitRowColumnEnd()
    }
}
