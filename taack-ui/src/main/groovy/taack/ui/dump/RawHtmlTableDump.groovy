package taack.ui.dump

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.ast.type.FieldInfo
import taack.ast.type.GetMethodReturn
import taack.ui.dsl.common.ActionIcon
import taack.ui.dsl.common.Style
import taack.ui.dsl.helper.Utils
import taack.ui.dump.common.BlockLog
import taack.ui.dump.common.CommonRawHtmlTableDump
import taack.ui.dump.html.element.*
import taack.ui.dump.html.layout.HTMLEmpty
import taack.ui.dump.html.style.DisplayBlock
import taack.ui.dump.html.style.DisplayInlineBlock
import taack.ui.dump.html.table.HTMLTd
import taack.ui.dump.html.table.HTMLTr

@CompileStatic
final class RawHtmlTableDump extends CommonRawHtmlTableDump {

    final String blockId

    RawHtmlTableDump(final BlockLog blockLog, final String id, final Parameter parameter) {
        super(blockLog, parameter)
        this.blockId = id ?: '' + parameter.modalId
    }

    @Override
    void visitTable() {
        blockLog.enterBlock('visitTable')
        blockLog.topElement.setTaackTag(TaackTag.TABLE)
        blockLog.topElement = themableTable.table(blockLog.topElement, blockId)
    }

    @Override
    void visitTableWithoutFilter() {
        blockLog.enterBlock('visitTableWithoutFilter')
        IHTMLElement table = themableTable.table(blockLog.topElement, blockId)
        blockLog.topElement.setTaackTag(TaackTag.TABLE)
        blockLog.topElement.addChildren(
                new HTMLForm("/${parameter.applicationTagLib.controllerName}/${parameter.applicationTagLib.actionName}").builder.addClasses('filter', 'rounded-3').putAttribute('taackFilterId', blockId).addChildren(
                        new HTMLInput(InputType.HIDDEN, parameter.sort, 'sort'),
                        new HTMLInput(InputType.HIDDEN, parameter.order, 'order'),
                        new HTMLInput(InputType.HIDDEN, parameter.offset, 'offset'),
                        new HTMLInput(InputType.HIDDEN, parameter.max, 'max'),
                        new HTMLInput(InputType.HIDDEN, parameter.beanId, 'id'),
                        new HTMLInput(InputType.HIDDEN, parameter.applicationTagLib.params['grouping'], 'grouping'),
                        new HTMLInput(InputType.HIDDEN, parameter.fieldName, 'fieldName'),
                ).build(),
        )
        blockLog.topElement = table
    }


    @Override
    void visitSortableFieldHeader(String i18n, FieldInfo[] fields) {
        i18n ?= parameter.trField(fields)
        boolean addColumn = !isInCol
        if (addColumn) visitColumn(null, null)
        blockLog.topElement.addChildren(
                new HTMLSpan().builder.addClasses('sortColumn').setStyle(new DisplayBlock()).putAttribute('sortField', RawHtmlFilterDump.getQualifiedName(fields)).addChildren(
                        new HTMLTxtContent("<a>${i18n}</a>")
                ).build()
        )
        if (addColumn) visitColumnEnd()

    }

    @Override
    void visitFieldHeader(final String i18n) {
        boolean addColumn = !isInCol
        if (addColumn) visitColumn(null, null)
        blockLog.topElement.addChildren(
                new HTMLSpan().builder.setStyle(new DisplayBlock()).addChildren(
                        new HTMLTxtContent("${i18n}")
                ).build()
        )
        if (addColumn) visitColumnEnd()
    }

    @Override
    void visitFieldHeader(FieldInfo[] fields) {
        visitFieldHeader parameter.trField(fields)
    }

    @Override
    void visitRowColumnEnd() {
        blockLog.exitBlock('visitRowColumnEnd')
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
        boolean addColumn = !isInCol
        if (addColumn) visitColumn(null, null)
        blockLog.topElement.addChildren(displayCell(value, style, null, firstInCol, isInCol))
        if (addColumn) visitColumnEnd()
    }

    @Override
    void visitRowAction(String i18n, ActionIcon actionIcon, Long id, String label, Map<String, ?> params, Boolean isAjax) {
        boolean addColumn = !isInCol
        if (addColumn) visitColumn(null, null)
        visitRowAction(i18n, actionIcon, 'progress', 'echoSelect', id, [label: label], isAjax)
        if (addColumn) visitColumnEnd()
    }

    @Override
    void visitPaginate(Number max, Number count) {
        if (count > max) {
            blockLog.topElement.addChildren(new HTMLDiv().builder
                    .addClasses('taackTablePaginate')
                    .putAttribute('taackMax', max?.toString())
                    .putAttribute('taackOffset', parameter.params.long('offset')?.toString())
                    .putAttribute('taackCount', count?.toString())
                    .build()
            )
        }
    }

    @Override
    void visitGroupFieldHeader(FieldInfo[] fields) {
        visitGroupFieldHeader(parameter.trField(fields), fields)
    }

    @Override
    void visitGroupFieldHeader(String i18n, FieldInfo[] fields) {
        i18n ?= parameter.trField(fields)

        String name = RawHtmlFilterDump.getQualifiedName(fields)

        blockLog.topElement.addChildren(
                new HTMLSpan().builder.addClasses('sortColumn', 'taackGroupableColumn')
                        .putAttribute('groupField', name).addChildren(
                        new HTMLTxtContent("""<a style="display: inline;">${i18n}</a><input type="checkbox"/>""")
                ).build()
        )
    }

    @Override
    void visitRowGroupHeader(String groups, MethodClosure show, long id) {

        stripped = 0

        blockLog.topElement.addChildren(new HTMLTr().builder
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
        blockLog.topElement.addChildren(new HTMLTr().builder
                .addClasses('taackRowGroupFooter', "taackRowGroupFooter-$level")
                .addChildren(
                        new HTMLTd(colCount).builder.addChildren(
                                new HTMLTxtContent(content)
                        ).build()
                ).build()
        )
    }
}
