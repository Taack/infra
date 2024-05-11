package taack.ui.base.table

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.ast.type.FieldInfo
import taack.ast.type.GetMethodReturn
import taack.ui.base.common.ActionIcon
import taack.ui.base.common.Style
import taack.ui.style.EnumStyle

import java.text.NumberFormat

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

    void visitRowField(FieldInfo fieldInfo, String format, final Style style)

    void visitRowField(GetMethodReturn fieldInfo, String format, final Style style)

    void visitRowField(String value, final Style style)

    void visitRowAction(String i18n, ActionIcon actionIcon, Long id, String label, Map<String, ?> params, Boolean isAjax)

    void visitRowAction(String i18n, ActionIcon actionIcon, String controller, String action, Long id, Map<String, ?> params, Boolean isAjax)

    void visitRowIndent()

    void visitRowIndentEnd()

    void visitGroupFieldHeader(FieldInfo[] fields)

    void visitGroupFieldHeader(String i18n, FieldInfo[] fields)

    void visitRowGroupHeader(String label)

    void visitRowGroupHeader(String groups, MethodClosure show, long id)

    void visitRowGroupFooter(String content)

    void visitPaginate(Number max, Number count)
}