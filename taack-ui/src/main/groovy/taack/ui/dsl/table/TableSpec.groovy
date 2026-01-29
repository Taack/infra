package taack.ui.dsl.table

import grails.util.Pair
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import org.grails.datastore.gorm.GormEntity
import taack.domain.TaackFilter
import taack.ui.dsl.common.Style
/**
 * Table Drawing DSL Spec. A table is composed of a header and rows.
 */
@CompileStatic
final class TableSpec {
    final IUiTableVisitor tableVisitor

    TableSpec(IUiTableVisitor tableVisitor, TableOption tableOption = null) {
        this.tableVisitor = tableVisitor
        if (tableOption)
            tableVisitor.visitTableOption(tableOption)
    }

    /**
     * Draw the header of the table. Should only contains:
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
    void rowIndent(final Boolean isExpended = false, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = RowIndentTreeSpec) Closure closure) {
        tableVisitor.visitRowIndent(isExpended)
        closure.delegate = new RowIndentTreeSpec(tableVisitor)
        closure.call()
        tableVisitor.visitRowIndentEnd()
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

    final<T extends GormEntity> Long iterate(TaackFilter<T> taackFilter, boolean showPaginate = true, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = RowColumnSpec) Closure c) {
        if (taackFilter.getSortString()) {
            tableVisitor.setSortingOrder(new Pair<String, String>(taackFilter.sortString, taackFilter.orderString))
        }
        if (taackFilter.getDateLastReading()) {
            tableVisitor.setLastReadingDate(new Pair<Date, String>(taackFilter.dateLastReading, taackFilter.readingDateFieldString))
        }
        c.delegate = new RowColumnSpec(tableVisitor)
        Pair<List<T>, Long> res = taackFilter.list()

        Date lastReadingDate = tableVisitor.getLastReadingDate()
        String readingDateField = tableVisitor.getReadingDateFieldString()
        for (T t in res.aValue) {
            if (lastReadingDate && readingDateField
                    && t.hasProperty(readingDateField) && t[readingDateField] instanceof Date
                    && (t[readingDateField] as Date) > lastReadingDate) {
                tableVisitor.visitRow(Style.BOLD, false) // mark the row as unread
            } else {
                tableVisitor.visitRow(null, false)
            }
            c.call(t)
            tableVisitor.visitRowEnd()
        }
        if (showPaginate) {
            tableVisitor.visitPaginate(taackFilter.max, res.bValue)
        }
        res.bValue
    }

    final<T extends GormEntity> Long iterate(Pair<List<T>, Long> builtFilterPair, boolean showPaginate = true, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = RowColumnSpec) Closure c) {
        c.delegate = new RowColumnSpec(tableVisitor)
        Date lastReadingDate = tableVisitor.getLastReadingDate()
        String readingDateField = tableVisitor.getReadingDateFieldString()
        for (T t in builtFilterPair.aValue) {
            if (lastReadingDate && readingDateField
                    && t.hasProperty(readingDateField) && t[readingDateField] instanceof Date
                    && (t[readingDateField] as Date) > lastReadingDate) {
                tableVisitor.visitRow(Style.BOLD, false) // mark the row as unread
            } else {
                tableVisitor.visitRow(null, false)
            }
            c.call(t)
            tableVisitor.visitRowEnd()
        }
        if (showPaginate) {
            tableVisitor.visitPaginate(builtFilterPair.aValue.size(), builtFilterPair.bValue)
        }
        builtFilterPair.bValue
    }
}
