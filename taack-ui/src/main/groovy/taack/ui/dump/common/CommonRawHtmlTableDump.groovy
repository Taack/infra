package taack.ui.dump.common

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.ui.base.common.ActionIcon
import taack.ui.base.common.Style
import taack.ui.base.table.IUiTableVisitor
import taack.ui.dump.Parameter

import java.text.DecimalFormat
import java.text.SimpleDateFormat

@CompileStatic
abstract class CommonRawHtmlTableDump implements IUiTableVisitor {

    final ByteArrayOutputStream out
    final Parameter parameter

    private int indent = -1
    int colCount = 0
    boolean isInCol = false
    Style rowStyle = null
    int stripped = 0
    boolean isInHeader = false
    int level = 0
    boolean firstInCol = false

    CommonRawHtmlTableDump(final ByteArrayOutputStream out, final Parameter parameter) {
        this.out = out
        this.parameter = parameter
    }

    static final <T> String dataFormat(T value, String format) {
        if (!format) return value?.toString()
        switch (value.class) {
            case BigDecimal:
                DecimalFormat df = new DecimalFormat(format ?: "#,###.00")
                return df.format(value)
            case Date:
                SimpleDateFormat sdf = new SimpleDateFormat(format ?: "yyyy-MM-dd")
                return sdf.format(value)
            default:
                return value?.toString()
        }
    }

    static final String displayCell(final String cell, final Style style, final String url, boolean firstInCol, boolean isInCol) {
        if (style) {
            if (!cell || cell.empty) return ""
            return """
                <div class="${style.cssClassesString ?: ''}" style="${style.cssStyleString ?: ''}">
                    ${url ? "<a class='link' href='${url}'>${cell ?: ''}</a>" : "${cell ?: ''}"}
                </div>
            """
        } else {
            String ret = "${cell && !cell.empty ? "${url ? "<a class='link' href='${url}'>${cell ?: ''}</a>" : "${cell}"}" : ''}"
            ret = ret.empty ? '' : (((!firstInCol && isInCol) ? '<br>' : '') + ret)
            return ret
        }
    }

    void fieldHeader() {
        if (!isInCol) {
            colCount++
            out << (isInHeader ? "<th>" : "<td>")
        }
    }

    void fieldFooter() {
        if (!isInCol) out << (isInHeader ? "</th>" : "</td>")
    }

    @Override
    void visitTableEnd() {
        out << "</table></div>\n"
    }

    @Override
    void visitColumn(Integer colSpan, Integer rowSpan) {
        colCount++
        isInCol = true
        if (colSpan) {
            out << """<th colspan="${colSpan}" ${rowSpan ? "rowSpan='${rowSpan}'" : ""} ${rowStyle ? "style='${rowStyle.cssStyleString}' " : ""}>"""
        } else {
            out << "<th>"
        }
    }

    @Override
    void visitHeader() {
        isInHeader = true
        out << "\n<tr>"
    }

    @Override
    void visitHeaderEnd() {
        isInHeader = false
        out << "</tr>\n"
    }

    @Override
    void visitColumnEnd() {
        isInCol = false
        out << "</th>"
    }

    @Override
    void visitRow(Style style, boolean hasChildren) {
        rowStyle = style
        stripped++
        out << """
            <tr class="taackTableRow ${stripped % 2 == 1 ? "pure-table-odd" : ""}" ${indent > -1 ? "${indent > 0 ? "style='display: none'" : ""}; taackTableRowGroup=$indent taackTableRowGroupHasChildren='${hasChildren}'" : ""}>
        """
    }

    void visitRowRO(Style style, boolean hasChildren) {
        rowStyle = style
        stripped++
        out << """
            <tr class="taackTableRow ${stripped % 2 == 1 ? "pure-table-odd" : ""}" ${indent > -1 ? "taackTableRowGroup=$indent taackTableRowGroupHasChildren='${hasChildren}'" : ""}>
        """
    }

    @Override
    void visitRowEnd() {
        rowStyle = null
        out << "</tr>\n"
    }

    @Override
    void visitRowIndent() {
        indent++
    }

    @Override
    void visitRowIndentEnd() {
        indent--
    }

    @Override
    void visitPaginate(Number max, Number count) {
    }

    @Override
    void visitRowGroupHeader(String label) {
        stripped = 0
        out << """
                <tr class="taackRowGroupHeader taackRowGroupHeader-$level"><td colspan="${colCount}"><em>$label</em></td></tr>
            """
    }

    @Override
    void visitRowAction(String i18n, final ActionIcon actionIcon, final String controller, final String action, final Long id, Map<String, ? extends Object> params, final Boolean isAjax) {
        i18n ?= parameter.trField(controller, action)

        fieldHeader()
        params ?= [:]
        if (parameter.applicationTagLib.params.containsKey('recordState')) {
            println "AUOAUOOOOO1 ${parameter.applicationTagLib.params['recordState']}"
            println "AUOAUOOOOO2 ${parameter.applicationTagLib.params['recordStateDecoded']}"
            params.put('recordState', parameter.applicationTagLib.params['recordState'])
        }
        if (isAjax) {
            out << """
                 <div class='icon'>
                    <a class='taackAjaxRowLink' ajaxAction='${parameter.urlMapped(controller, action, id, params)}'>
                        ${actionIcon.getHtml(i18n)}
                    </a>
                 </div>
            """
        } else {
            out << """
                 <div class='icon'>
                    <a class='link' href="${parameter.urlMapped(controller, action, id, params)}">
                        ${actionIcon.getHtml(i18n)}
                    </a>
                 </div>
            """
        }
        fieldFooter()
    }

    @Override
    void visitRowColumn(Integer colSpan, Integer rowSpan, Style style) {
        isInCol = true
        if (colSpan) {
            out << """<td colspan="${colSpan}" ${rowSpan ? "rowSpan='${rowSpan}'" : ""} ${style ? "style='${style.cssStyleString}'" : (rowStyle ? "style='${rowStyle.cssStyleString}'" : "")}>"""
        } else {
            out << """<td ${style ? "style='${style.cssStyleString}'" : (rowStyle ? "style='${rowStyle.cssStyleString}'" : "")}>"""
        }
        firstInCol = true
    }

    @Override
    void visitRowGroupHeader(String groups, MethodClosure show, long id) {
        visitRowGroupHeader groups
    }
}