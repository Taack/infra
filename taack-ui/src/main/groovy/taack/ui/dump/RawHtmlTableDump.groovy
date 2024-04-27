package taack.ui.dump

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.ast.type.FieldInfo
import taack.ast.type.GetMethodReturn
import taack.ui.base.common.ActionIcon
import taack.ui.base.common.Style
import taack.ui.base.helper.Utils
import taack.ui.base.table.IUiTableVisitor
import taack.ui.style.EnumStyle

import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat

@CompileStatic
final class RawHtmlTableDump implements IUiTableVisitor {
    final private ByteArrayOutputStream out
    final private Parameter parameter

    final String blockId
    private static Integer currentFormId = 0
    private Object currentObject

    private int indent = -1
    private int colCount = 0
    private boolean isInCol = false
    private boolean isInHeader = false
    private Object[] latestGroups = null
    int level = 0
    private Style rowStyle = null
    int stripped = 0

    RawHtmlTableDump(final String id, final ByteArrayOutputStream out, final Parameter parameter) {
        this.out = out
        this.parameter = parameter
        this.blockId = id ?: '' + parameter.modalId
        currentFormId++
    }

    @Override
    void visitTable() {
        out << """
                <div style='overflow: auto;'><form><table class='pure-table taackTable' taackTableId='${blockId}'>
               """
    }

    @Override
    void visitTableEnd() {
        out << "</table></div></form>\n"
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
    void visitColumnEnd() {
        isInCol = false
        out << "</th>"
    }

    private void fieldHeader() {
        if (!isInCol) {
            colCount++
            out << (isInHeader ? "<th>" : "<td>")
        }
    }

    private void fieldFooter() {
        if (!isInCol) out << (isInHeader ? "</th>" : "</td>")
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
    void visitRow(Object current, Style style, boolean hasChildren) {
        rowStyle = style
        currentObject = current
        stripped++
        out << """
            <tr class="taackTableRow ${stripped % 2 == 1 ? "pure-table-odd" : ""}" ${indent > -1 ? "${indent > 0 ? "style='display: none'" : ""}; taackTableRowGroup=$indent taackTableRowGroupHasChildren='${hasChildren}'" : ""}>
        """
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
            <span class="sortable sortColumn taackSortableColumn " property="${RawHtmlFilterDump.getQualifiedName(fields)}" formid="${fields.first().fieldConstraint.field.declaringClass.simpleName}_Filter"><a>${i18n}</a></span>
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
        isInCol = false
        out << "</td>"
    }

    @Override
    void visitRowColumn(Integer colSpan, Integer rowSpan, Style style) {
        isInCol = true
        if (colSpan) {
            out << """<td colspan="${colSpan}" ${rowSpan ? "rowSpan='${rowSpan}'" : ""} ${style ? "style='${style.cssStyleString}'" : (rowStyle ? "style='${rowStyle.cssStyleString}'" : "")}>"""
        } else {
            out << """<td ${style ? "style='${style.cssStyleString}'" : (rowStyle ? "style='${rowStyle.cssStyleString}'" : "")}>"""
        }
    }

    void visitRowFieldCommon(Class type, Object value, final String format = null, final Style style, final String controller = null, final String action = null, final Long id = null) {
        switch (type) {
            case Long:
            case Integer:
                visitRowField((Long)value, style, controller, action, id)
                break
            case Double:
            case Float:
            case BigDecimal:
                visitRowField((BigDecimal)value, format, style, controller, action, id)
                break
            case Date:
                visitRowField((Date)value, format, style, controller, action, id)
                break
            default:
                visitRowField((String)value, style, controller, action, id)
        }

    }

    @Override
    void visitRowField(final FieldInfo fieldInfo, final String format = null, final Style style, final String controller = null, final String action = null, final Long id = null) {
        visitRowFieldCommon (fieldInfo.fieldConstraint.field.type, fieldInfo.value, format, style, controller, action, id)
    }


    @Override
    void visitRowField(final GetMethodReturn fieldInfo, final Style style, final String controller = null, final String action = null, final Long id = null) {
        visitRowFieldCommon (fieldInfo.getMethod().returnType, fieldInfo.value, null, style, controller, action, id)
    }

    private static String surroundCell(final String cell, final Style style = null, final String url = null) {
        if (style) {
            if (!cell || cell.empty) return ""
            return """
                <div class="${style.cssClassesString ?: ''}" style="${style.cssStyleString ?: ''}">
                    ${url ? "<a class='link' href='${url}'>${cell ?: ''}</a>" : "${cell ?: ''}"}
                </div>
            """
        } else return "${cell && !cell.empty ? "${url ? "<a class='link' href='${url}'>${cell ?: ''}</a>" : "${cell}"} <br>" : ''}"
    }

    @Override
    void visitRowField(final String value, final Style style, final String controller = null, final String action = null, final Long id = null) {
        fieldHeader()
        out << surroundCell(value, style, controller ? parameter.urlMapped(controller, action, id) : null)
        fieldFooter()
    }

    @Override
    void visitRowField(final Long value, final Style style, final String controller = null, final String action = null, final Long id = null) {
        fieldHeader()
        out << surroundCell(value?.toString(), style)
        fieldFooter()
    }

    @Override
    void visitRowField(final BigDecimal value, final String format = null, final Style style, final String controller = null, final String action = null, final Long id = null) {
        DecimalFormat df = new DecimalFormat(format ?: "#,###.00")
        fieldHeader()
        if (value) out << surroundCell(df.format(value), style ?: new Style(null, "text-align: right;"), controller ? parameter.urlMapped(controller, action, id) : null)
        fieldFooter()
    }

    @Override
    void visitRowField(final Date value, final String format = null, final Style style, final String controller = null, final String action = null, final Long id = null) {
        SimpleDateFormat sdf = new SimpleDateFormat(format ?: "yyyy-MM-dd")
        fieldHeader()
        out << surroundCell(value ? sdf.format(value) : "", style, controller ? parameter.urlMapped(controller, action, id) : null)
        fieldFooter()
    }

    @Override
    void visitRowField(Map value, Style style, String controller = null, String action = null, Long id = null) {
        String display = value?.entrySet()?.findAll {
            it.value != null
        }?.collect { "${it.key}: ${it.value}" }?.join(', ')
        fieldHeader()
        out << surroundCell(display ?: "", null, controller ? parameter.urlMapped(controller, action, id) : null)
        fieldFooter()
    }

    @Override
    void visitRowField(EnumStyle value, Style style, String controller = null, String action = null, Long id = null) {
        if (value?.getStyle()) {
            if (style) style = value.getStyle() + style
            else style = value.getStyle()
        }
        fieldHeader()
        out << surroundCell(value?.getName(), style, controller ? parameter.urlMapped(controller, action, id) : null)
        fieldFooter()
    }

    @Override
    void visitRowField(BigDecimal value, NumberFormat numberFormat, Style style, String controller = null, String action = null, Long id = null) {
        if (!numberFormat) numberFormat = parameter.nf
        fieldHeader()
        if (value) out << surroundCell(numberFormat.format(value), style ?: new Style(null, "text-align: right;"), controller ? parameter.urlMapped(controller, action, id) : null)
        fieldFooter()
    }

    @Override
    void visitRowLink(String i18n, ActionIcon actionIcon, Long id, String label, Map<String, ?> params, Boolean isAjax) {
        visitRowLink(i18n, actionIcon, 'progress', 'echoSelect', id, [label: label], isAjax)
    }

    @Override
    void visitRowLink(String i18n, final ActionIcon actionIcon, final String controller, final String action, final Long id, Map<String, ? extends Object> params, final Boolean isAjax) {
        i18n ?= parameter.trField(controller, action)

        fieldHeader()
        params ?= [:]
        if (parameter.map.containsKey('recordState')) {
            params.put('recordState', parameter.map['recordState'])
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
    void visitFooterButton(String i18n, String controller, String action, Long id, Map<String, ?> additionalParams) {
        i18n ?= parameter.trField(controller, action)

        additionalParams?.each {
            out << """<input type="hidden" name="${it.key}" value="${it.value}">"""
        }
        out << """
            <button type="submit" class="pure-button pure-button-secondary " formaction="${parameter.urlMapped(controller, action, id)}">${i18n}</button>
        """
    }

    @Override
    void visitPaginate(Number max, Number count) {
        if (max != 0)
            out << """<div class="taackTablePaginate" taackMax="$max" taackOffset="${parameter.params.long('offset')}" taackCount="$count"></div>"""
    }

    @Override
    void visitSortableFieldHeader(final String i18n, String controller, String action, Map<String, ?> parameters, Map<String, ?> additionalParams) {
        fieldHeader()
        String direction = additionalParams["order"] ?: ''
        def p = parameters
        p.putAll(additionalParams)
        out << """
                <span class="sortable sortColumn ${direction} ">
                <a class='link' href="${parameter.urlMapped(controller, action, p)}">
                    ${i18n}
                </a></span>
            """
        fieldFooter()
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
    void visitGroupFieldHeader(FieldInfo[] fields) {
        visitGroupFieldHeader(parameter.trField(fields), fields)
    }

    @Override
    void visitGroupFieldHeader(String i18n, FieldInfo[] fields) {
        i18n ?= parameter.trField(fields)

        String name = RawHtmlFilterDump.getQualifiedName(fields)
        out << """
            <span class="sortable sortColumn taackGroupableColumn" property="${name}" formid="${name}_Filter"><a  style="display: inline;">${i18n}</a><input type="checkbox"/></span><br>
        """
    }

    @Override
    void visitRowGroupHeader(Object groups, MethodClosure show, Long id) {
        String groupString
        if (groups.class.isArray()) {
            Object[] gs = groups as Object[]
            groupString = gs*.toString().join(" - ")
            level = 0
            if (latestGroups) {
                for (int i = 0; i < Math.min(gs.size(), latestGroups.size()); i++) {
                    if (gs[i] != latestGroups[i]) break
                    level++
                }
            }
            latestGroups = gs
        } else {
            groupString = groups.toString()
        }
        stripped = 0
        if (!show) {
            out << """
                <tr class="taackRowGroupHeader taackRowGroupHeader-$level"><td colspan="${colCount}"><em>$groupString</em></td></tr>
            """
        } else {
            out << """
                <tr class="taackRowGroupHeader taackRowGroupHeader-$level"><td colspan="${colCount}"><a href="${parameter.urlMapped(Utils.getControllerName(show), show.method, id)}"><em>$groupString</em></a></td></tr>
            """
        }
    }

    @Override
    void visitRowGroupFooter(String content) {
        out << """
            <tr class="taackRowGroupFooter taackRowGroupFooter-$level"><td colspan="${colCount}">$content</td></tr>
        """
    }
}
