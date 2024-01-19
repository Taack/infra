package taack.ui.base


import groovy.transform.CompileStatic
import org.grails.datastore.gorm.GormEntity
import taack.ast.type.FieldInfo
import taack.ui.base.table.ColumnHeaderFieldSpec
import taack.ui.base.table.IUiTableVisitor
import taack.ui.base.table.TableSpec
/**
 * Class for creating table. Those tables could be used with a filter.
 *
 * <p>A simple table is created with:
 * {@link taack.ui.base.block.BlockSpec#table(taack.ui.base.UiTableSpecifier)}
 *
 * <p>A table along with a filter is created with:
 * {@link taack.ui.base.block.BlockSpec#tableFilter(java.lang.String, taack.ui.base.UiFilterSpecifier, java.lang.String, taack.ui.base.UiTableSpecifier)}
 */
@CompileStatic
final class UiTableSpecifier<T extends GormEntity> {

    Closure closure
    Class<T> aClass
    SelectMode selectMode

    enum SelectMode {
        NONE,
        SINGLE,
        MULTIPLE
    }

    ColumnHeaderFieldSpec.SortableDirection sortableDirection
    Integer max
    UiFilterSpecifier filterSpecifier
    T tInstance
    Collection<Long> idsFilter

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
     *              footerButton("Merge", Bp2Controller.&mergeValuesSave as MethodClosure, valueToMerge.id)
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
    UiTableSpecifier ui(final Class<T> aClass, SelectMode selectMode = SelectMode.NONE, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = TableSpec) final Closure closure) {
        this.closure = closure
        this.aClass = aClass
        this.selectMode = selectMode
        this
    }

    UiTableSpecifier sortColumnAsc(FieldInfo fieldInfo) {
        sortableDirection = new ColumnHeaderFieldSpec.SortableDirection(fieldInfo, ColumnHeaderFieldSpec.DefaultSortingDirection.ASC)
        this
    }

    UiTableSpecifier sortColumnDesc(FieldInfo fieldInfo) {
        sortableDirection = new ColumnHeaderFieldSpec.SortableDirection(fieldInfo, ColumnHeaderFieldSpec.DefaultSortingDirection.DESC)
        this
    }

    UiTableSpecifier filter(UiFilterSpecifier filterSpecifier) {
        this.filterSpecifier = filterSpecifier
        this
    }

    UiTableSpecifier max(Integer max) {
        this.max = max
        this
    }

    UiTableSpecifier instanceFilter(T instance) {
        this.tInstance = instance
        this
    }

    UiTableSpecifier idsFilter(Collection<Long> ids) {
        this.idsFilter = ids
        this
    }

    /**
     * Allow to visit the table.
     *
     * @param tableVisitor
     */
    void visitTable(final IUiTableVisitor tableVisitor) {
        if (tableVisitor && closure) {
            tableVisitor.visitTable(aClass, selectMode)
            closure.delegate = new TableSpec(tableVisitor)
            closure.call()
            tableVisitor.visitTableEnd()
        }
    }

    void visitTableWithNoFilter(final IUiTableVisitor tableVisitor) {
        if (tableVisitor && closure) {
            tableVisitor.visitTableWithoutFilter(aClass, selectMode)
            closure.delegate = new TableSpec(tableVisitor)
            closure.call()
            tableVisitor.visitTableEnd()
        }
    }

}