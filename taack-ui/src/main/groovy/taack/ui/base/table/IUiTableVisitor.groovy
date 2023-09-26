package taack.ui.base.table

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.ast.type.FieldInfo
import taack.ast.type.GetMethodReturn
import taack.ui.base.UiTableSpecifier.SelectMode
import taack.ui.base.common.ActionIcon
import taack.ui.base.common.Style
import taack.ui.base.table.ColumnHeaderFieldSpec.DefaultSortingDirection
import taack.ui.style.EnumStyle

import java.text.NumberFormat

@CompileStatic
interface IUiTableVisitor {
    void visitTable(Class aClass, SelectMode selectMode)

    void visitTableWithoutFilter(Class aClass, SelectMode selectMode)

    void visitTableEnd()

    void visitColumnEnd()

    void visitHeader()

    void visitHeaderEnd()

    void visitRow(Object current, Style style, boolean hasChildren)

    void visitRowEnd()

    void visitSortableFieldHeader(String i18n, FieldInfo fieldInfo, DefaultSortingDirection direction)

    void visitSortableFieldHeader(String i18n, FieldInfo[] fields, DefaultSortingDirection direction)

    void visitFieldHeader(String i18n)

    void visitRowColumnEnd()

    void visitRowColumn(Integer colSpan, Integer rowSpan, Style style)

    void visitRowField(FieldInfo fieldInfo, String format, final Style style)

    void visitRowField(GetMethodReturn fieldInfo, final Style style)

    void visitRowField(String value, final Style style)

    void visitRowField(Long value, final Style style)

    void visitRowField(BigDecimal value, String format, final Style style)

    void visitRowField(Date value, String format, final Style style)

    void visitRowLink(String i18n, ActionIcon actionIcon, String controller, String action, Long id, Map<String, ?> params, Boolean isAjax)

    void visitSortableFieldHeader(String i18n, String controller, String action, Map<String, ?> params, Map<String, ?> additionalParams)

    void visitPaginate(Number max, Number offset, Number count)

    void visitRowIndent()

    void visitRowIndentEnd()

    void visitGroupFieldHeader(String i18n, FieldInfo field)

    void visitRowGroupHeader(Object groups, MethodClosure show, Long id)

    void visitRowGroupFooter(String content)

    void visitRowField(Map value, Style style)

    void visitRowField(EnumStyle value, Style style)

    void visitRowField(BigDecimal value, NumberFormat numberFormat, Style style)

    void visitColumn(Integer colSpan, Integer rowSpan)

    void visitFooterButton(String i18n, String controller, String action, Long id, Map<String, ?> additionalParams)
}