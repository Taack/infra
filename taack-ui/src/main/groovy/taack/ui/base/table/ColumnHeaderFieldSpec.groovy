package taack.ui.base.table

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

    /**
     * Allow to define default sorting order direction.
     *
     * See {@link taack.base.TaackSimpleFilterService#list(java.lang.Class, taack.ui.base.table.ColumnHeaderFieldSpec.SortableDirection)}
     */
    enum DefaultSortingDirection {
        ASC("taackDefaultAsc"), DESC("taackDefaultDesc")

        DefaultSortingDirection(final String className) {
            this.className = className
        }
        final String className
    }

    /**
     * Helper class that store necessary info to specify the sort of the table. See
     * {@link taack.base.TaackSimpleFilterService#list(java.lang.Class, taack.ui.base.table.ColumnHeaderFieldSpec.SortableDirection)}
     *
     */
    final static class SortableDirection {
        final DefaultSortingDirection defaultSortingDirection
        final FieldInfo[] fields
        final FieldInfo field

        SortableDirection(final FieldInfo field, final DefaultSortingDirection defaultDirection) {
            this.field = field
            this.fields = null
            this.defaultSortingDirection = defaultDirection
        }

        SortableDirection(final FieldInfo[] fields, final DefaultSortingDirection defaultDirection) {
            this.field = null
            this.fields = fields
            this.defaultSortingDirection = defaultDirection
        }

        SortableDirection(final List<FieldInfo> fields, final DefaultSortingDirection defaultDirection) {
            this.field = null
            this.fields = fields as FieldInfo[]
            this.defaultSortingDirection = defaultDirection
        }
    }

    ColumnHeaderFieldSpec(IUiTableVisitor tableVisitor) {
        this.tableVisitor = tableVisitor
    }

    /**
     * Simple header with a label
     *
     * @param i18n The label
     */
    void fieldHeader(final String i18n) {
        tableVisitor.visitFieldHeader(i18n)
    }

    /**
     * Define an header that can be sorted.
     *
     * @param i18n Label
     * @param field Target field
     * @param defaultDirection (optional) Default direction when the table is displayed for the first time.
     * @return The {@link SortableDirection} to pass to {@link taack.base.TaackSimpleFilterService#list(java.lang.Class, taack.ui.base.table.ColumnHeaderFieldSpec.SortableDirection)}
     */
    SortableDirection sortableFieldHeader(final String i18n, final FieldInfo field, final DefaultSortingDirection defaultDirection = null) {
        tableVisitor.visitSortableFieldHeader(i18n, field, defaultDirection)
        new SortableDirection(field, defaultDirection)
    }

    /**
     * See {@link #sortableFieldHeader(java.lang.String, taack.ast.type.FieldInfo, DefaultSortingDirection)}. The label is automatically set.
     *
     * @param defaultDirection Default direction when the table is displayed for the first time
     * @param fields Target field pointing to the data to sort
     * @return The {@link SortableDirection} to pass to {@link taack.base.TaackSimpleFilterService#list(java.lang.Class, taack.ui.base.table.ColumnHeaderFieldSpec.SortableDirection)}
     */
    SortableDirection sortableFieldHeader(final DefaultSortingDirection defaultDirection, final FieldInfo... fields) {
        tableVisitor.visitSortableFieldHeader(null, fields, defaultDirection)
        new SortableDirection(fields, defaultDirection)
    }

    /**
     * See {@link #sortableFieldHeader(java.lang.String, taack.ast.type.FieldInfo, DefaultSortingDirection)}. The label is automatically set.
     *
     * @param fields Target field pointing to the data to sort
     */
    void sortableFieldHeader(final FieldInfo... field) {
        tableVisitor.visitSortableFieldHeader(null, field, null)
    }

    /**
     * Add a checkbox, if clicked, the table will group lines.
     * See {@link taack.base.TaackSimpleFilterService#listInGroup(java.lang.Object, java.lang.Class)}
     *
     * @param i18n Label of the column field
     * @param field Target field (has to be a direct field of the object)
     */
    void groupFieldHeader(final String i18n, final FieldInfo field) {
        tableVisitor.visitGroupFieldHeader(i18n, field)
    }

    /**
     * See {@link #sortableFieldHeader(java.lang.String, taack.ast.type.FieldInfo, DefaultSortingDirection)}.
     *
     * @param i18n
     * @param fields
     * @param defaultDirection
     * @return
     */
    SortableDirection sortableFieldHeader(final String i18n, final FieldInfo[] fields, final DefaultSortingDirection defaultDirection) {
        tableVisitor.visitSortableFieldHeader(i18n, fields, defaultDirection)
        new SortableDirection(fields, defaultDirection)
    }

    /**
     * See {@link #sortableFieldHeader(java.lang.String, taack.ast.type.FieldInfo, DefaultSortingDirection)}.
     *
     * @param i18n
     * @param fields
     * @param defaultDirection
     * @return
     */
    @Deprecated
    SortableDirection sortableFieldHeader(final String i18n, final List<FieldInfo> fields, final DefaultSortingDirection defaultDirection) {
        tableVisitor.visitSortableFieldHeader(i18n, fields as FieldInfo[], defaultDirection)
        new SortableDirection(fields as FieldInfo[], defaultDirection)
    }

    /**
     * See {@link #sortableFieldHeader(java.lang.String, taack.ast.type.FieldInfo, DefaultSortingDirection)}.
     *
     * @param i18n
     * @param fields
     * @return
     */
    SortableDirection sortableFieldHeader(final String i18n, final FieldInfo... fields) {
        tableVisitor.visitSortableFieldHeader(i18n, fields, null)
        new SortableDirection(fields, null)
    }

    /**
     * See {@link #sortableFieldHeader(java.lang.String, taack.ast.type.FieldInfo, DefaultSortingDirection)}.
     *
     * @param i18n
     * @param fields
     * @return
     */
    @Deprecated
    SortableDirection sortableFieldHeader(final String i18n, final List<FieldInfo> fields) {
        tableVisitor.visitSortableFieldHeader(i18n, fields as FieldInfo[], null)
        new SortableDirection(fields as FieldInfo[], null)
    }

    /**
     * See {@link #sortableFieldHeader(java.lang.String, taack.ast.type.FieldInfo, DefaultSortingDirection)}.
     *
     * @param i18n
     * @param controller
     * @param action
     * @param params
     * @param additionalParams
     */
    void sortableFieldHeader(final String i18n, final String controller, final String action, final Map<String, ? extends Object> params,
                             final Map<String, ? extends Object> additionalParams) {
        tableVisitor.visitSortableFieldHeader(i18n, controller, action, params, additionalParams)
    }

}
