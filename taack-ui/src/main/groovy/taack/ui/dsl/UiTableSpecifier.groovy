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
     * Describe the table to display with an added column with select input per line.
     *
     * You must add the object to each row, like in the code sample below:
     *
     * <pre>{@code
     *      row o, {
     *          rowColumn {
     *              rowField o.dateCreated
     *              rowField o.lastUpdated
     *          }
     *      }
     * }</pre>
     *
     * <p>You also need a table footer to call an action on the selection:
     *
     * <pre>{@code
     *      row {
     *          rowColumn 4, {
     *              footerButton('Merge', Bp2Controller.&mergeValuesSave as MethodClosure, valueToMerge.id)
     *          }
     *      }
     * }</pre>
     *
     * See {@link TableSpec} for more information
     *
     * @param aClass Class of the object displayed in the table
     * @param action
     * @param selectMode
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