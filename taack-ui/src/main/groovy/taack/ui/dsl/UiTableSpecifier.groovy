package taack.ui.dsl

import groovy.transform.CompileStatic
import taack.ui.dsl.table.TableOption
import taack.ui.dsl.table.TableSpec
import taack.ui.dsl.table.IUiTableVisitor

/**
 * Class for creating table. Those tables could be used with a filter.
 *
 * <p>A simple table is created with:
 * {@link taack.ui.dsl.block.BlockSpec#table(UiTableSpecifier)}
 */
@CompileStatic
final class UiTableSpecifier {

    Closure closure
    TableOption tableOption

    /**
     * Table Specifier Builder
     *
     * See {@link TableSpec} for more information
     *
     * @param tableOption Global Table Options
     * @param closure The table specification
     * @return Itself
     */
    UiTableSpecifier ui(TableOption tableOption = null, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = TableSpec) final Closure closure) {
        this.closure = closure
        this.tableOption = tableOption
        this
    }

    /**
     * Allow to visit the table.
     *
     * @param tableVisitor
     */
    void visitTable(final IUiTableVisitor tableVisitor) {
        if (tableVisitor && closure) {
            closure.delegate = new TableSpec(tableVisitor, tableOption)
            tableVisitor.visitTable()
            closure.call()
            tableVisitor.visitTableEnd()
        }
    }

    void visitTableWithNoFilter(final IUiTableVisitor tableVisitor) {
        if (tableVisitor && closure) {
            closure.delegate = new TableSpec(tableVisitor, tableOption)
            tableVisitor.visitTableWithoutFilter()
            closure.call()
            tableVisitor.visitTableEnd()
        }
    }

}