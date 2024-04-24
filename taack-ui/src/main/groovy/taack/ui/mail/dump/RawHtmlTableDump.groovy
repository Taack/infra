package taack.ui.mail.dump

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.ast.type.FieldInfo
import taack.ast.type.GetMethodReturn
import taack.ui.base.common.ActionIcon
import taack.ui.base.common.Style
import taack.ui.base.helper.Utils
import taack.ui.base.table.IUiTableVisitor
import taack.ui.dump.Parameter
import taack.ui.style.EnumStyle

import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat

@CompileStatic
final class RawHtmlTableDump implements IUiTableVisitor {
    final private ByteArrayOutputStream out
    final Parameter parameter

    private boolean hasRowAction = false
    private List<String> fieldNames = []
    private static Integer currentFormId = 0
    private Object currentObject

    private int indent = -1
    private int colCount = 0
    private Object[] latestGroups = null
    int level = 0
    private Style rowStyle = null


    RawHtmlTableDump(final String id, final ByteArrayOutputStream out, final Parameter parameter) {
        this.parameter = parameter
        this.out = out
        currentFormId++
    }

    @Override
    void visitTable() {
        out << "<table class='pure-table pure-table-horizontal taackTable' style='repeat-header: yes;'>\n"
    }

    @Override
    void visitTableEnd() {
        out << "</tbody></table>\n"
    }

    @Override
    void visitColumn(Integer colSpan, Integer rowSpan) {
        colCount++
        if (colSpan) {
            out << """<th style="font-size: 12px !important;" colspan="${colSpan}" ${rowSpan ?"rowSpan='${rowSpan}'":""}>"""
        } else {
            out << """<th style="font-size: 12px !important">"""
        }
    }

    @Override
    void visitFooterButton(String i18n, String controller, String action, Long id, Map<String, ?> additionalParams) {

    }

    @Override
    void visitColumnEnd() {
        out << "</th>"
    }

    @Override
    void visitHeader() {
        out << """\n<thead style="background-color: #add8e6; vertical-align: middle;"><tr>"""
    }

    @Override
    void visitHeaderEnd() {
        out << "</tr></thead><tbody>\n"
    }

    @Override
    void visitRow(Object current, Style style, boolean hasChildren) {
        rowStyle = style
        currentObject = current
        out << """
            <tr class="taackTableRow" ${indent > -1 ? "${indent > 0 ? "style='display: none'" : ""}; taackTableRowGroup=$indent taackTableRowGroupHasChildren='${hasChildren}'" : ""}>
        """
        if (hasRowAction) {
            currentFormId++
            this.hasRowAction = true
        }
    }

    @Override
    void visitRowEnd() {
        rowStyle = null
        out << "</tr>\n"
        if (hasRowAction) {
            Set<String> additionalFieldName = Utils.getAdditionalFields(currentObject, null, fieldNames).keySet()
            out << """
                    ${Utils.getAdditionalInputs(currentObject, null, fieldNames, "formId${currentFormId}")}
                    <input form="formId${currentFormId}" type="hidden" name="fieldName" value="${(fieldNames + additionalFieldName).join(',')}">
            """
            fieldNames = []
            hasRowAction = false
        }
    }

    @Override
    void visitSortableFieldHeader(String i18n, FieldInfo[] fields) {
        visitFieldHeader(i18n)
    }

    @Override
    void visitFieldHeader(final String i18n) {
        out << " ${i18n} "
    }

    @Override
    void visitFieldHeader(FieldInfo[] fields) {
        out << " ${parameter.trField(fields)} <br>"
    }

    @Override
    void visitRowColumnEnd() {
        out << "</td>"
    }

    @Override
    void visitRowColumn(Integer colSpan, Integer rowSpan, Style style) {
        if (colSpan) {
            out << """<td colspan="${colSpan}" ${rowSpan ?"rowSpan='${rowSpan}'":""} ${style ? "style='${style.cssStyleString}'" : (rowStyle ? "style='${rowStyle.cssStyleString}'" : "")}>"""
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

    private static String surroundCell(final String cell, final Style style = null, String controller = null, String action = null, Long id = null) {
        if (style) {
            if (!cell || cell.empty) return ""
            return """
                <div class="${style.cssClassesString?:''}" style="${style.cssStyleString?:''}">${cell ?: ''}</div>
            """
        } else return "${cell && !cell.empty?cell + "<br>": ''}"
    }

    @Override
    void visitRowField(final String value, final Style style, String controller = null, String action = null, Long id = null) {
        out << surroundCell(value, style)
    }

    @Override
    void visitRowField(final Long value, final Style style, String controller = null, String action = null, Long id = null) {
        out << surroundCell(value?.toString(), style)
    }

    @Override
    void visitRowField(final BigDecimal value, final String format = null, final Style style, String controller = null, String action = null, Long id = null) {
        DecimalFormat df = new DecimalFormat(format ?: "#,###.00")
        if (value) out << surroundCell(df.format(value), style ?: new Style(null, "text-align: right;"))
    }

    @Override
    void visitRowField(final Date value, final String format = null, final Style style, String controller = null, String action = null, Long id = null) {
        SimpleDateFormat sdf = new SimpleDateFormat(format ?: "yyyy-MM-dd")
        out << surroundCell(value ? sdf.format(value) : "", style)
    }

    @Override
    void visitRowField(Map value, Style style, String controller = null, String action = null, Long id = null) {
        String display = value.entrySet().findAll {
            it.value != null
        }.collect { "${it.key}: ${it.value}" }.join(', ')
        out << surroundCell(display ?: "")
    }

    @Override
    void visitRowField(EnumStyle value, Style style, String controller = null, String action = null, Long id = null) {
        if (value?.getStyle()) {
            if (style) style = value.getStyle() + style
            else style = value.getStyle()
        }
        out << surroundCell(value?.getName(), style)
    }

    @Override
    void visitRowField(BigDecimal value, NumberFormat numberFormat, Style style, String controller = null, String action = null, Long id = null) {
        if (value) out << surroundCell(numberFormat.format(value), style ?: new Style(null, "text-align: right;"))
    }

    @Override
    void visitSortableFieldHeader(final String i18n, String controller, String action, Map<String, ?> parameters, Map<String, ?> additionalParams) {
        String direction = additionalParams["order"] ?: ''
        out << """
                <span class="sortable sortColumn ${direction} ">
                    ${i18n}
                </span>
            """
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
    void visitGroupFieldHeader(String i18n, FieldInfo[] fields) {
        out << """
            <span class="sortable sortColumn taackGroupableColumn" property="${fields*.fieldName.join('.')}" formid="${fields.first().fieldConstraint.field.declaringClass.simpleName}_Filter"><a  style="display: inline;">${i18n}</a><input type="checkbox"/></span><br>
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
        out << """
            <tr class="taackRowGroupHeader taackRowGroupHeader-$level"><td colspan="${colCount}"><em>$groupString</em></td></tr>
        """
    }

    @Override
    void visitRowGroupFooter(String content) {
        out << """
            <tr class="taackRowGroupFooter taackRowGroupFooter-$level"><td colspan="${colCount}">$content</td></tr>
        """
    }

    @Override
    void visitRowLink(String i18n, ActionIcon actionIcon, String controller, String action, Long id, Map<String, ?> params, Boolean isAjax) {

    }

    @Override
    void visitRowLink(String i18n, ActionIcon actionIcon, Long id, String label, Map<String, ?> params, Boolean isAjax) {

    }
}
