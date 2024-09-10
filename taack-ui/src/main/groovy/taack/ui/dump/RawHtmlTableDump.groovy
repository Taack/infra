package taack.ui.dump

import groovy.transform.CompileStatic
import taack.ast.type.FieldInfo
import taack.ast.type.GetMethodReturn
import taack.ui.dsl.common.ActionIcon
import taack.ui.dsl.common.Style
import taack.ui.dsl.table.IUiTableVisitor
import taack.ui.dump.common.BlockLog
import taack.ui.dump.html.element.*
import taack.ui.dump.html.style.DisplayBlock
import taack.ui.dump.html.style.DisplayNone
import taack.ui.dump.html.table.*

import java.text.DecimalFormat
import java.text.SimpleDateFormat

@CompileStatic
final class RawHtmlTableDump implements IUiTableVisitor {

    final String blockId
    final Parameter parameter
    final ThemableTable themableTable

    private int indent = -1
    int colCount = 0
    boolean isInCol = false
    Style rowStyle = null
    int stripped = 0
    boolean isInHeader = false
    int level = 0
    boolean firstInCol = false

    protected final BlockLog blockLog

    RawHtmlTableDump(final BlockLog blockLog, final String id, final Parameter parameter) {
        this.blockLog = blockLog
        this.parameter = parameter
        this.themableTable = new ThemableTable(parameter.uiThemeService.themeSelector.themeMode, parameter.uiThemeService.themeSelector.themeSize)
        this.blockId = id ?: '' + parameter.modalId
    }

    static final <T> String dataFormat(T value, String format) {
        if (!value) return ''
        switch (value.class) {
            case BigDecimal:
                DecimalFormat df = new DecimalFormat(format ?: "#,###.00")
                return df.format(value)
            case Date:
                SimpleDateFormat sdf = new SimpleDateFormat(format ?: "yyyy/MM/dd")
                return sdf.format(value)
            default:
                return value.toString()
        }
    }

    static final IHTMLElement displayCell(final String cell, final Style style, final String url, boolean firstInCol, boolean isInCol) {
        Style displayBlock = new Style(null, 'display: block;')
        if (style) {
            displayBlock += style
        }
        if (!url) return new HTMLSpan().builder
                .setStyle(displayBlock)
                .addChildren(new HTMLTxtContent(cell)).build()
        return new HTMLAnchor(true, url).builder.addChildren(
                new HTMLTxtContent(cell)
        ).build()
    }

    @Override
    void visitTableEnd() {
        blockLog.exitBlock('visitTableEnd')
        blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.TABLE)
    }

    @Override
    void visitColumn(Integer colSpan, Integer rowSpan) {
        blockLog.enterBlock('visitColumn')
        colCount++
        isInCol = true
        if (isInHeader) {
            HTMLTh th = new HTMLTh(colSpan, rowSpan)
            th.setTaackTag(TaackTag.TABLE_COL)
            blockLog.topElement.addChildren(th)
            blockLog.topElement = th
        } else {
            HTMLTd th = new HTMLTd(colSpan, rowSpan)
            th.setTaackTag(TaackTag.TABLE_COL)
            blockLog.topElement.addChildren(th)
            blockLog.topElement = th
        }
    }

    @Override
    void visitHeader() {
        blockLog.enterBlock('visitHeader')
        isInHeader = true
        HTMLTr tr = new HTMLTr()
        tr.addClasses('align-middle')
        blockLog.topElement.addChildren(
                new HTMLTHead().builder.setTaackTag(TaackTag.TABLE_HEAD).addChildren(
                        tr
                ).build()
        )
        blockLog.topElement = tr
    }

    @Override
    void visitHeaderEnd() {
        blockLog.exitBlock('visitHeaderEnd')
        isInHeader = false
        blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.TABLE_HEAD)
        HTMLTBody tb = new HTMLTBody().builder.setTaackTag(TaackTag.TABLE_HEAD).build() as HTMLTBody
        blockLog.topElement.addChildren(tb)
        blockLog.topElement = tb
    }

    @Override
    void visitColumnEnd() {
        blockLog.exitBlock('visitColumnEnd')
        isInCol = false
        blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.TABLE_COL)
    }

    @Override
    void visitRow(Style style, boolean hasChildren) {
        blockLog.enterBlock('visitRow')
        rowStyle = style
        stripped++
        HTMLTr tr = new HTMLTr()
        tr.taackTag = TaackTag.TABLE_ROW
        if (indent >= 0) {
            //tr.styleDescriptor = new DisplayNone()
            tr.attributes.put('taackTableRowGroup', indent.toString())
            tr.attributes.put('taackTableRowGroupHasChildren', hasChildren.toString())
        }
        if (indent > 0 && parameter.target == Parameter.RenderingTarget.WEB) {
            tr.setStyleDescriptor(new DisplayNone())
        }
        blockLog.topElement.addChildren(tr)
        blockLog.topElement = tr
        firstInCol = true
    }

    void visitRowRO(Style style, boolean hasChildren) {
        visitRow style, hasChildren
    }

    @Override
    void visitRowEnd() {
        blockLog.exitBlock('visitRowEnd')
        rowStyle = null
        blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.TABLE_ROW)
    }

    @Override
    void visitRowIndent() {
        blockLog.enterBlock('visitRowIndent')
        indent++
    }

    @Override
    void visitRowIndentEnd() {
        blockLog.exitBlock('visitRowIndentEnd')
        indent--
    }

    @Override
    void visitRowAction(String i18n, final ActionIcon actionIcon, final String controller, final String action, final Long id, Map<String, ?> params, final Boolean isAjax) {
        i18n ?= parameter.trField(controller, action, id != null || params.containsKey('id'))

        params ?= [:]
        blockLog.topElement.addChildren(
                new HTMLDiv().builder.addChildren(
                        new HTMLAnchor(isAjax, parameter.urlMapped(controller, action, id, params)).builder.addChildren(
                                new HTMLTxtContent(actionIcon.getHtml(i18n))
                        ).build()
                ).build()
        )
    }

    @Override
    void visitRowAction(String linkText, final String controller, final String action, final Long id, Map<String, ?> params, final Boolean isAjax) {
        params ?= [:]
        blockLog.topElement.addChildren(
                new HTMLDiv().builder.addChildren(
                        new HTMLAnchor(isAjax, parameter.urlMapped(controller, action, id, params)).builder.addChildren(
                                new HTMLTxtContent(linkText)
                        ).addClasses('table-link').build()
                ).build()
        )
    }

    @Override
    void visitRowColumn(Integer colSpan, Integer rowSpan, Style style) {
        blockLog.enterBlock('visitRowColumn')
        isInCol = true
        HTMLTd td = new HTMLTd(colSpan, rowSpan)
        if (firstInCol) td.addClasses('firstCellInGroup', "firstCellInGroup-${indent}")
        firstInCol = false
        blockLog.topElement.addChildren(td)
        blockLog.topElement = td
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
                    .putAttribute('taackOffset', parameter.params.long('offset')?.toString() ?: "0")
                    .putAttribute('taackCount', count?.toString())
                    .build()
            )
        }
    }
}
