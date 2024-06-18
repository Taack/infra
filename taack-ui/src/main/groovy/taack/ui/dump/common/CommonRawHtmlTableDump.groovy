package taack.ui.dump.common

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.ui.dsl.common.ActionIcon
import taack.ui.dsl.common.Style
import taack.ui.dsl.table.IUiTableVisitor
import taack.ui.dump.Parameter
import taack.ui.dump.html.element.*
import taack.ui.dump.html.style.DisplayBlock
import taack.ui.dump.html.style.DisplayNone
import taack.ui.dump.html.table.*

import java.text.DecimalFormat
import java.text.SimpleDateFormat

@CompileStatic
abstract class CommonRawHtmlTableDump implements IUiTableVisitor {

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

    CommonRawHtmlTableDump(final BlockLog blockLog, final Parameter parameter) {
        this.blockLog = blockLog
        this.parameter = parameter
        this.themableTable = new ThemableTable(parameter.uiThemeService.themeSelector.themeMode, parameter.uiThemeService.themeSelector.themeSize)
    }

    static final <T> String dataFormat(T value, String format) {
        switch (value.class) {
            case BigDecimal:
                DecimalFormat df = new DecimalFormat(format ?: "#,###.00")
                return df.format(value)
            case Date:
                SimpleDateFormat sdf = new SimpleDateFormat(format ?: "yyyy/MM/dd")
                return sdf.format(value)
            default:
                return value?.toString()
        }
    }

    static final IHTMLElement displayCell(final String cell, final Style style, final String url, boolean firstInCol, boolean isInCol) {
        if (!url) return new HTMLSpan().builder
                .setStyle(new DisplayBlock())
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
        if (indent > 0) {
            tr.styleDescriptor = new DisplayNone()
            tr.attributes.put('taackTableRowGroup', indent.toString())
            tr.attributes.put('taackTableRowGroupHasChildren', hasChildren.toString())
        }
        blockLog.topElement.addChildren(tr)
        blockLog.topElement = tr
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
    void visitPaginate(Number max, Number count) {
    }

    @Override
    void visitRowGroupHeader(String label) {
        stripped = 0
        blockLog.topElement.addChildren(
                new HTMLTr(colCount).builder.addClasses('taackRowGroupHeader', "taackRowGroupHeader-$level").addChildren(
                        new HTMLTd(colCount).builder.addChildren(
                                new HTMLTxtContent("<em>$label</em>")
                        ).build()
                ).build()
        )
    }

    @Override
    void visitRowAction(String i18n, final ActionIcon actionIcon, final String controller, final String action, final Long id, Map<String, ? extends Object> params, final Boolean isAjax) {
        i18n ?= parameter.trField(controller, action)

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
    void visitRowColumn(Integer colSpan, Integer rowSpan, Style style) {
        blockLog.enterBlock('visitRowColumn')
        isInCol = true
        HTMLTd td = new HTMLTd(colSpan, rowSpan)
        blockLog.topElement.addChildren(td)
        blockLog.topElement = td

        firstInCol = true
    }

    @Override
    void visitRowGroupHeader(String groups, MethodClosure show, long id) {
        visitRowGroupHeader groups
    }
}