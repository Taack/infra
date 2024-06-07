package taack.ui.dump.mail

import groovy.transform.CompileStatic
import taack.ast.type.FieldInfo
import taack.ast.type.GetMethodReturn
import taack.ui.base.common.ActionIcon
import taack.ui.base.common.Style
import taack.ui.dump.Parameter
import taack.ui.dump.common.CommonRawHtmlTableDump

@CompileStatic
final class RawHtmlTableDump extends CommonRawHtmlTableDump {
    final private ByteArrayOutputStream out
    final private Parameter parameter

    private Object[] latestGroups = null
    int level = 0
    private boolean firstInCol = false

    RawHtmlTableDump(final ByteArrayOutputStream out, final Parameter parameter) {
        super(out, parameter)
    }

    @Override
    void visitTable() {
        out << """
                <div>
                <div style='overflow: auto;'><form><table class='taackTable'>
               """
    }

    @Override
    void visitTableWithoutFilter() {
        out << "<div>\n"
        out << "<div class='table-div' style='overflow: auto;'><table class='taackTable'>\n"
    }

    @Override
    void visitRow(Style style, boolean hasChildren) {
        visitRowRO(style, hasChildren)
    }

    @Override
    void visitRowEnd() {
        rowStyle = null
        out << "</tr>\n"
    }

    @Override
    void visitSortableFieldHeader(String i18n, FieldInfo[] fields) {
        i18n ?= parameter.trField(fields)
        fieldHeader()
        out << """
            <span class="sortable sortColumn "><a>${i18n}</a></span>
        """
        fieldFooter()
    }

    @Override
    void visitFieldHeader(final String i18n) {
        fieldHeader()
        out << " ${i18n} <br>"
        fieldFooter()
    }

    @Override
    void visitFieldHeader(FieldInfo[] fields) {
        fieldHeader()
        out << parameter.trField(fields) + ' <br>'
        fieldFooter()
    }

    @Override
    void visitRowColumnEnd() {
        firstInCol = false
        isInCol = false
        out << "</td>"
    }

    @Override
    void visitRowField(final FieldInfo fieldInfo, final String format, final Style style) {
        visitRowField(dataFormat(fieldInfo.value, format), style)
    }


    @Override
    void visitRowField(final GetMethodReturn fieldInfo, final String format, final Style style) {
        visitRowField(dataFormat(fieldInfo.value, format), style)
    }

    @Override
    void visitRowField(final String value, final Style style) {
        fieldHeader()
        out << displayCell(value, style, null, firstInCol, isInCol)
        firstInCol = false
        fieldFooter()
    }

    @Override
    void visitRowAction(String i18n, ActionIcon actionIcon, Long id, String label, Map<String, ?> params, Boolean isAjax) {
        visitRowAction(i18n, actionIcon, 'progress', 'echoSelect', id, [label: label], isAjax)
    }

    @Override
    void visitRowAction(String i18n, final ActionIcon actionIcon, final String controller, final String action, final Long id, Map<String, ? extends Object> params, final Boolean isAjax) {
        super.visitRowAction(i18n, actionIcon, controller, action, id, params, false)
    }

    @Override
    void visitGroupFieldHeader(FieldInfo[] fields) {
        visitGroupFieldHeader(parameter.trField(fields), fields)
    }

    @Override
    void visitGroupFieldHeader(String i18n, FieldInfo[] fields) {
        i18n ?= parameter.trField(fields)

        out << """
            <span class="sortable sortColumn taackGroupableColumn"><a  style="display: inline;">${i18n}</a><input type="checkbox"/></span><br>
        """
    }

    @Override
    void visitRowGroupFooter(String content) {
        out << """
            <tr class="taackRowGroupFooter taackRowGroupFooter-$level"><td colspan="${colCount}">$content</td></tr>
        """
    }
}
