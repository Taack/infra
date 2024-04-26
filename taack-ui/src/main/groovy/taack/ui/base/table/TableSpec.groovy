package taack.ui.base.table

import grails.util.Pair
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import org.grails.datastore.gorm.GormEntity
import taack.domain.TaackFilter
import taack.ui.base.common.Style

/**
 * {@link taack.ui.base.UiTableSpecifier#ui(java.lang.Class, groovy.lang.Closure)} delegated class
 *
 * <p>This class allows to draw a table. A table is composed of a header and rows.
 */
@CompileStatic
final class TableSpec {
    final IUiTableVisitor tableVisitor

    TableSpec(IUiTableVisitor tableVisitor) {
        this.tableVisitor = tableVisitor
    }

    /**
     * Draw the header of the table. Should only contains {@link ColumnHeaderFieldSpec#fieldHeader(java.lang.String)},
     * {@link ColumnHeaderFieldSpec#sortableFieldHeader(java.lang.String, taack.ast.type.FieldInfo[])} or
     * {@link ColumnHeaderSpec#column(groovy.lang.Closure)}
     *
     * @param Closure header content
     */
    void header(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = ColumnHeaderSpec) Closure closure) {
        tableVisitor.visitHeader()
        closure.delegate = new ColumnHeaderSpec(tableVisitor)
        closure.call()
        tableVisitor.visitHeaderEnd()
    }

    /**
     * Indent rows inside the closure
     *
     * @param Closure contain the list of {@link TableSpec#row(groovy.lang.Closure)}
     */
    void rowIndent(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = TableSpec) Closure closure) {
        tableVisitor.visitRowIndent()
        closure.delegate = this
        closure.call()
        tableVisitor.visitRowIndentEnd()
    }

    /**
     * Display a row that has the width of the table and contains group as label.
     *
     * @param label Label to display
     * @param show Action that point to a show
     * @param id The ID to pass to the show action
     */
    void rowGroupHeader(def label, MethodClosure show = null, Long id = null) {
        tableVisitor.visitRowGroupHeader(label, show, id)
    }

    /**
     * Display a row that has the width of the table.
     *
     * @param content
     */
    void rowGroupFooter(final String content) {
        tableVisitor.visitRowGroupFooter(content)
    }

    /**
     * Row container
     *
     * @param currentObject Mandatory if table is selectable
     * @param style
     * @param closure Contains columns
     */
    void row(Object currentObject = null, final Style style = null, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = RowColumnSpec) Closure closure) {
        tableVisitor.visitRow(currentObject, style, false)
        closure.delegate = new RowColumnSpec(tableVisitor)
        closure.call()
        tableVisitor.visitRowEnd()
    }

    /**
     * Row container that can be expended as a tree. Can be nested.
     *
     * @param hasChildren If true, the subsequent rows will be collapsed under the current row
     * @param closure Contains columns
     */
    void rowTree(boolean hasChildren, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = RowColumnSpec) Closure closure) {
        tableVisitor.visitRow(null, null, hasChildren)
        closure.delegate = new RowColumnSpec(tableVisitor)
        closure.call()
        tableVisitor.visitRowEnd()
    }

    final<T extends GormEntity> Long iterate(TaackFilter<T> taackFilter, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = TableSpec.class) Closure c) {
        c.delegate = new TableSpec(tableVisitor)
        Pair<List<T>, Long> res = taackFilter.list()
        tableVisitor.visitPaginate(taackFilter.max, res.bValue)
        for (T t in res.aValue)
            c.call(t)
        res.bValue
    }

}
