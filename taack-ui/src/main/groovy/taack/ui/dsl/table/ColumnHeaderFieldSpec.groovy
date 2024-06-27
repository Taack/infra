package taack.ui.dsl.table

import groovy.transform.CompileStatic
import taack.ast.type.FieldInfo

/**
 * Base class to define fields in a table header, that will be optionally disposed in columns.
 *
 * <p>The header columns can contain simple fieldHeader, not sortable or a sortableFieldHeader which provide
 * a bidirectional sortable field.
 */
@CompileStatic
class ColumnHeaderFieldSpec {
    final IUiTableVisitor tableVisitor

    ColumnHeaderFieldSpec(IUiTableVisitor tableVisitor) {
        this.tableVisitor = tableVisitor
    }

    /**
     * Simple header with a label
     *
     * @param i18n The label
     */
    void label(final String i18n) {
        tableVisitor.visitFieldHeader(i18n)
    }

    /**
     * Simple header with a label
     *
     * @param i18n The label
     */
    void label(final FieldInfo... fields) {
        tableVisitor.visitFieldHeader(fields)
    }

    /**
     *
     * @param defaultDirection Default direction when the table is displayed for the first time
     * @param fields Target field pointing to the data to sort
     */
    void sortableFieldHeader(final FieldInfo... fields) {
        tableVisitor.visitSortableFieldHeader(null, fields)
    }

    /**
     *
     * @param i18n
     * @param fields
     * @param defaultDirection
     * @return
     */
    void sortableFieldHeader(final String i18n, final FieldInfo... fields) {
        tableVisitor.visitSortableFieldHeader(i18n, fields)
    }

}
