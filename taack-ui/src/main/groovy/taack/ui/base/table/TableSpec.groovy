package taack.ui.base.table

import grails.util.Pair
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import org.grails.datastore.gorm.GormEntity
import taack.domain.TaackFilter
import taack.ui.base.common.Style

/**
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
    void header(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = ColumnHeaderSpec) Closure closure) {
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
    void rowIndent(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = RowIndentTreeSpec) Closure closure) {

        tableVisitor.visitRowIndent()
        closure.delegate = new RowIndentTreeSpec(tableVisitor)
        closure.call()
        tableVisitor.visitRowIndentEnd()
    }

    /**
     * Display a row that has the width of the table and contains group as label.
     *
     * @param label Label to display
     */
    void rowGroupHeader(String label) {
        tableVisitor.visitRowGroupHeader(label)
    }

    void rowGroupHeader(String label, MethodClosure show, long id) {
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
    void row(final Style style = null, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = RowColumnSpec) Closure closure) {
        tableVisitor.visitRow(style, false)
        closure.delegate = new RowColumnSpec(tableVisitor)
        closure.call()
        tableVisitor.visitRowEnd()
    }

    final<T extends GormEntity> Long iterate(TaackFilter<T> taackFilter, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = RowColumnSpec) Closure c) {
        c.delegate = new RowColumnSpec(tableVisitor)
        Pair<List<T>, Long> res = taackFilter.list()
        for (T t in res.aValue) {
            tableVisitor.visitRow(null, false)
            c.call(t)
            tableVisitor.visitRowEnd()
        }
        tableVisitor.visitPaginate(taackFilter.max, res.bValue)
        res.bValue
    }

}
