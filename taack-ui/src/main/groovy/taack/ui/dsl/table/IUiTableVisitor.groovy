package taack.ui.dsl.table

import grails.util.Pair
import groovy.transform.CompileStatic
import taack.ast.type.FieldInfo
import taack.ast.type.GetMethodReturn
import taack.ui.dsl.common.ActionIcon
import taack.ui.dsl.common.Style

@CompileStatic
interface IUiTableVisitor {
    void visitTable()

    void visitTableWithoutFilter()

    void visitTableEnd()

    void visitHeader()

    void visitHeaderEnd()

    void visitColumn(Integer colSpan, Integer rowSpan)

    void visitColumnEnd()

    void visitRow(Style style, boolean hasChildren)

    void visitRowEnd()

    void visitSortableFieldHeader(String i18n, FieldInfo[] fields)

    void visitFieldHeader(String i18n)

    void visitFieldHeader(FieldInfo[] fields)

    void visitRowColumn(Integer colSpan, Integer rowSpan, Style style)

    void visitRowColumnEnd()

    void visitRowField(FieldInfo fieldInfo, Long rowId, String format, final Style style)

    void visitRowField(FieldInfo fieldInfo, String format, final Style style)

    void visitRowField(GetMethodReturn fieldInfo, String format, final Style style)

    void visitRowField(String value, final Style style)

    void visitRowFieldRaw(String value, final Style style)

    void visitRowAction(String i18n, ActionIcon actionIcon, String key, String label)

    void visitRowAction(String i18n, ActionIcon actionIcon, String controller, String action, Long id, Map<String, ?> params, Boolean isAjax)

    void visitRowAction(String i18n, String controller, String action, Long id, Map<String, ?> params, Boolean isAjax)

    void visitRowIndent(Boolean isExpended)

    void visitRowIndentEnd()
    
    void visitPaginate(Number max, Number count)

    void setSortingOrder(Pair<String, String>sortingOrder)

    Pair<String, String> getSortingOrder()

    void visitColumnSelect(String paramsKey)

    void visitColumnSelectButton(String buttonText, String controller, String action, Map<String, ?> params, Boolean isAjax)

    void visitColumnSelectEnd()

    void visitRowSelect(String value, boolean isSelectable)

    String getSelectColumnParamsKey()
}