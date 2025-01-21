package taack.ui.dsl.table

import groovy.transform.CompileStatic

/**
 * {@link TableSpec#header(groovy.lang.Closure)} delegated class.
 *
 * <p>A column can contains many fields, it is optional if only one field is present.
 */
@CompileStatic
final class ColumnHeaderSpec extends ColumnHeaderFieldSpec {

    ColumnHeaderSpec(IUiTableVisitor tableVisitor) {
        super(tableVisitor)
    }

    /**
     * Allow to group fields to display in the same column
     *
     * @param closure Field list
     */
    void column(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = ColumnHeaderFieldSpec) Closure closure) {
        tableVisitor.visitColumn(null, null)
        closure.delegate = new ColumnHeaderFieldSpec(tableVisitor)
        closure.call()
        tableVisitor.visitColumnEnd()
    }

    /**
     * Same as {@link #column(groovy.lang.Closure)} but with colspan and rowspan params
     *
     * @param colSpan
     * @param rowSpan
     * @param closure
     */
    void column(Integer colSpan, Integer rowSpan = null, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = ColumnHeaderFieldSpec) Closure closure) {
        tableVisitor.visitColumn(colSpan, rowSpan)
        closure.delegate = new ColumnHeaderFieldSpec(tableVisitor)
        closure.call()
        tableVisitor.visitColumnEnd()
    }

    /**
     * build a column with checkbox to select all wanted rows and to transfer them to specific actions
     *
     * @param paramsKey: the parameter name which will stock the values from all selected rows
     * @param closure: clarify actions that receive the transferred values
     */
    void columnSelect(String paramsKey, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = ColumnHeaderSelectSpec) Closure closure) {
        tableVisitor.visitColumnSelect(paramsKey)
        closure.delegate = new ColumnHeaderSelectSpec(tableVisitor)
        closure.call()
        tableVisitor.visitColumnSelectEnd()
    }
}
