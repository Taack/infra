package taack.ui.dump

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.ast.type.FieldInfo
import taack.ast.type.GetMethodReturn
import taack.ui.base.common.ActionIcon
import taack.ui.base.common.Style
import taack.ui.base.helper.Utils
import taack.ui.dump.common.CommonRawHtmlTableDump

@CompileStatic
final class RawHtmlTableDump extends CommonRawHtmlTableDump {

    final String blockId
    private static Integer currentFormId = 0


    private Object[] latestGroups = null

    RawHtmlTableDump(final String id, final ByteArrayOutputStream out, final Parameter parameter) {
        super(out, parameter)
        this.blockId = id ?: '' + parameter.modalId
        currentFormId++
    }

    @Override
    void visitTable() {
        out << """
                <div style='overflow: auto;'><table class='${tableTheme.getTableClasses()} taackTable' taackTableId='${blockId}'>
               """
    }

    @Override
    void visitTableWithoutFilter() {
        out << """
                <form style="display: none;" id="formId${currentFormId}" action="/${parameter.applicationTagLib.controllerName}/${parameter.applicationTagLib.actionName}" name="${currentFormId}_Filter" class="pure-form pure-form-aligned filter" taackFilterId="${blockId}">
                    <input type="hidden" name="sort" value="${parameter.applicationTagLib.params['sort'] ?: ''}">
                    <input type="hidden" name="order" value="${parameter.applicationTagLib.params['order'] ?: ''}">
                    <input type="hidden" name="grouping" value="${parameter.applicationTagLib.params['grouping'] ?: ''}">
                    <input type="hidden" name="offset" value="${parameter.offset ?: '0'}">
                    <input type="hidden" name="max" value="${parameter.max ?: '20'}">
                    ${parameter.beanId ? '<input type="hidden" name="id" value=' + parameter.beanId + '>' : ''}
                </form>
            """

        out << "<div class='table-div' style='overflow: auto;'><table class='pure-table taackTable' taackTableId='${blockId}'>\n"
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
    void visitPaginate(Number max, Number count) {
        if (max != 0)
            out << """<div class="taackTablePaginate" taackMax="$max" taackOffset="${parameter.params.long('offset')?:0}" taackCount="$count"></div>"""
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
    void visitRowGroupHeader(String groups, MethodClosure show, long id) {

        stripped = 0
        if (!show) {
            out << """
                <tr class="taackRowGroupHeader taackRowGroupHeader-$level"><td colspan="${colCount}"><em>$groups</em></td></tr>
            """
        } else {
            out << """
                <tr class="taackRowGroupHeader taackRowGroupHeader-$level"><td colspan="${colCount}"><a href="${parameter.urlMapped(Utils.getControllerName(show), show.method, id)}"><em>$groups</em></a></td></tr>
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
