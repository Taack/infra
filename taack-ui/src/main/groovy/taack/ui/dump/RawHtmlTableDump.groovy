package taack.ui.dump

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.ast.type.FieldInfo
import taack.ast.type.GetMethodReturn
import taack.ui.dsl.common.ActionIcon
import taack.ui.dsl.common.Style
import taack.ui.dsl.helper.Utils
import taack.ui.dump.common.CommonRawHtmlTableDump
import taack.ui.dump.html.element.HTMLAnchor
import taack.ui.dump.html.element.HTMLDiv
import taack.ui.dump.html.element.HTMLForm
import taack.ui.dump.html.element.HTMLInput
import taack.ui.dump.html.element.HTMLSpan
import taack.ui.dump.html.element.HTMLTxtContent
import taack.ui.dump.html.element.IHTMLElement
import taack.ui.dump.html.element.InputType
import taack.ui.dump.html.element.TaackTag
import taack.ui.dump.html.style.DisplayInlineBlock
import taack.ui.dump.html.table.HTMLTd
import taack.ui.dump.html.table.HTMLTr

@CompileStatic
final class RawHtmlTableDump extends CommonRawHtmlTableDump {

    final String blockId
    private static Integer currentFormId = 0


    private Object[] latestGroups = null

    RawHtmlTableDump(final IHTMLElement topElement, final String id, final Parameter parameter) {
        super(topElement, parameter)
        this.blockId = id ?: '' + parameter.modalId
        currentFormId++
    }

    @Override
    void visitTable() {
        topElement = themableTable.table(topElement, blockId)
    }

    @Override
    void visitTableWithoutFilter() {
        IHTMLElement table = themableTable.table(topElement, blockId)
        topElement.addChildren(
                new HTMLDiv().builder.putAttribute('ajaxBlockId', blockId).addChildren(
                        new HTMLForm("/${parameter.applicationTagLib.controllerName}/${parameter.applicationTagLib.actionName}").builder.setTaackTag(TaackTag.FILTER).addClasses('filter', 'rounded-3').putAttribute('taackFilterId', parameter.modalId?.toString()).addChildren(
                                new HTMLInput(InputType.HIDDEN, parameter.sort, 'sort'),
                                new HTMLInput(InputType.HIDDEN, parameter.order, 'order'),
                                new HTMLInput(InputType.HIDDEN, parameter.offset, 'offset'),
                                new HTMLInput(InputType.HIDDEN, parameter.max, 'max'),
                                new HTMLInput(InputType.HIDDEN, parameter.beanId, 'id'),
                                new HTMLInput(InputType.HIDDEN, parameter.applicationTagLib.params['grouping'], 'grouping'),
                                new HTMLInput(InputType.HIDDEN, parameter.fieldName, 'fieldName'),
                        ).build(),
                        table
                ).build()
        )
        topElement = table
    }


    @Override
    void visitSortableFieldHeader(String i18n, FieldInfo[] fields) {
        i18n ?= parameter.trField(fields)
        fieldHeader()
        topElement.addChildren(
                new HTMLSpan().builder.addClasses('sortColumn').putAttribute('sortField', RawHtmlFilterDump.getQualifiedName(fields)).addChildren(
                        new HTMLTxtContent("<a>${i18n}</a>")
                ).build()
        )
        fieldFooter()
    }

    @Override
    void visitFieldHeader(final String i18n) {
        fieldHeader()
        topElement.addChildren(
                new HTMLSpan().builder.setStyle(new DisplayInlineBlock()).addChildren(
                        new HTMLTxtContent("${i18n}")
                ).build()
        )
        fieldFooter()
    }

    @Override
    void visitFieldHeader(FieldInfo[] fields) {
        visitFieldHeader parameter.trField(fields)
    }

    @Override
    void visitRowColumnEnd() {
        firstInCol = false
        isInCol = false
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
        displayCell(value, style, null, firstInCol, isInCol)
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
            topElement.addChildren(new HTMLDiv().builder
                    .addClasses('taackTablePaginate')
                    .putAttribute('taackMax', max?.toString())
                    .putAttribute('taackOffset', parameter.params.long('offset')?.toString())
                    .putAttribute('taackCount', count?.toString())
                    .build()
            )
    }

    @Override
    void visitGroupFieldHeader(FieldInfo[] fields) {
        visitGroupFieldHeader(parameter.trField(fields), fields)
    }

    @Override
    void visitGroupFieldHeader(String i18n, FieldInfo[] fields) {
        i18n ?= parameter.trField(fields)

        String name = RawHtmlFilterDump.getQualifiedName(fields)

        topElement.addChildren(
                new HTMLSpan().builder.addClasses('sortColumn', 'taackGroupableColumn')
                        .putAttribute('groupField', name).addChildren(
                        new HTMLTxtContent("""<a style="display: inline;">${i18n}</a><input type="checkbox"/>""")
                ).build()
        )
    }

    @Override
    void visitRowGroupHeader(String groups, MethodClosure show, long id) {

        stripped = 0

        topElement.addChildren(new HTMLTr().builder
                .addClasses('taackRowGroupHeader', "taackRowGroupHeader-$level")
                .addChildren(
                        new HTMLTd(colCount).builder.addChildren(
                                new HTMLAnchor(false, parameter.urlMapped(Utils.getControllerName(show), show.method, id)).builder.addChildren(
                                        new HTMLTxtContent("<em>$groups</em>")
                                ).build()
                        ).build()
                ).build()
        )
    }

    @Override
    void visitRowGroupFooter(String content) {
        topElement.addChildren(new HTMLTr().builder
                .addClasses('taackRowGroupFooter', "taackRowGroupFooter-$level")
                .addChildren(
                        new HTMLTd(colCount).builder.addChildren(
                                        new HTMLTxtContent(content)
                        ).build()
                ).build()
        )
    }
}
