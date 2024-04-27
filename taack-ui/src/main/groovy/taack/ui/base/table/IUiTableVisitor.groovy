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

    void visitColumnEnd()

    void visitHeader()

    void visitHeaderEnd()

    void visitRow(Object current, Style style, boolean hasChildren)

    void visitRowEnd()

    void visitSortableFieldHeader(String i18n, FieldInfo[] fields)

    void visitFieldHeader(String i18n)

    void visitFieldHeader(FieldInfo[] fields)

    void visitRowColumnEnd()

    void visitRowColumn(Integer colSpan, Integer rowSpan, Style style)

    void visitRowField(FieldInfo fieldInfo, String format, final Style style, final String controller, final String action, final Long id)

    void visitRowField(GetMethodReturn fieldInfo, final Style style, final String controller, final String action, final Long id)

    void visitRowField(String value, final Style style, final String controller, final String action, final Long id)

    void visitRowField(Long value, final Style style, final String controller, final String action, final Long id)

    void visitRowField(BigDecimal value, String format, final Style style, final String controller, final String action, final Long id)

    void visitRowField(Date value, String format, final Style style, final String controller, final String action, final Long id)

    void visitRowLink(String i18n, ActionIcon actionIcon, Long id, String label, Map<String, ?> params, Boolean isAjax)

    void visitRowLink(String i18n, ActionIcon actionIcon, String controller, String action, Long id, Map<String, ?> params, Boolean isAjax)

    void visitSortableFieldHeader(String i18n, String controller, String action, Map<String, ?> params, Map<String, ?> additionalParams)

    void visitRowIndent()

    void visitRowIndentEnd()

    void visitGroupFieldHeader(FieldInfo[] fields)

    void visitGroupFieldHeader(String i18n, FieldInfo[] fields)

    void visitRowGroupHeader(Object groups, MethodClosure show, Long id)

    void visitRowGroupFooter(String content)

    void visitRowField(Map value, Style style, String controller, String action, Long id)

    void visitRowField(EnumStyle value, Style style, String controller, String action, Long id)

    void visitRowField(BigDecimal value, NumberFormat numberFormat, Style style, String controller, String action, Long id)

    void visitColumn(Integer colSpan, Integer rowSpan)

    void visitFooterButton(String i18n, String controller, String action, Long id, Map<String, ?> additionalParams)

    void visitPaginate(Number max, Number count)
}