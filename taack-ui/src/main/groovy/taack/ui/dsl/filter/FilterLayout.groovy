package taack.ui.dsl.filter

import groovy.transform.CompileStatic

/**
 * Where a {@code tableFilter} places its filter relative to its table, when the page asks.
 * The row that holds both carries the choice as {@code data-filter-layout="toolbar"} or
 * {@code data-filter-layout="side"}, which {@code taack.css} and themes style. A
 * {@code tableFilter} without a layout carries no attribute and keeps the filter in a
 * column beside the table.
 */
@CompileStatic
enum FilterLayout {
    /** A full-width bar above the table: the fields in a wrapping row, the Filter button at its end. */
    TOOLBAR,
    /** A column beside the table, for a filter too long for a toolbar. */
    SIDE
}
