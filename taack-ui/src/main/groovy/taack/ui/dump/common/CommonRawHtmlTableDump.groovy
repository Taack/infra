package taack.ui.dump.common

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.ui.dsl.common.ActionIcon
import taack.ui.dsl.common.Style
import taack.ui.dsl.table.IUiTableVisitor
import taack.ui.dump.Parameter
import taack.ui.dump.html.element.*
import taack.ui.dump.html.style.DisplayBlock
import taack.ui.dump.html.style.DisplayInlineBlock
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

    IHTMLElement topElement

    CommonRawHtmlTableDump(final IHTMLElement topElement, final Parameter parameter) {
        this.topElement = topElement
        this.parameter = parameter
        this.themableTable = new ThemableTable(parameter.uiThemeService.themeSelector.themeMode, parameter.uiThemeService.themeSelector.themeSize)
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
        topElement = topElement.toParentTaackTag(TaackTag.TABLE)
    }

    @Override
    void visitColumn(Integer colSpan, Integer rowSpan) {
        colCount++
        isInCol = true
        if (isInHeader) {
            HTMLTh th = new HTMLTh(colSpan, rowSpan)
            th.setTaackTag(TaackTag.TABLE_COL)
            topElement.addChildren(th)
            topElement = th
        } else {
            HTMLTd th = new HTMLTd(colSpan, rowSpan)
            th.setTaackTag(TaackTag.TABLE_COL)
            topElement.addChildren(th)
            topElement = th
        }
    }

    @Override
    void visitHeader() {
        isInHeader = true
        HTMLTr tr = new HTMLTr()
        tr.addClasses('align-middle')
        topElement.addChildren(
                new HTMLTHead().builder.setTaackTag(TaackTag.TABLE_HEAD).addChildren(
                        tr
                ).build()
        )
        topElement = tr
    }

    @Override
    void visitHeaderEnd() {
        isInHeader = false
        topElement = topElement.toParentTaackTag(TaackTag.TABLE_HEAD)
        HTMLTBody tb = new HTMLTBody().builder.setTaackTag(TaackTag.TABLE_HEAD).build() as HTMLTBody
        topElement.addChildren(tb)
        topElement = tb
    }

    @Override
    void visitColumnEnd() {
        isInCol = false
        topElement = topElement.toParentTaackTag(TaackTag.TABLE_COL)
    }

    @Override
    void visitRow(Style style, boolean hasChildren) {
        rowStyle = style
        stripped++
        HTMLTr tr = new HTMLTr()
        tr.taackTag = TaackTag.TABLE_ROW
        if (indent > 0) {
            tr.styleDescriptor = new DisplayNone()
            tr.attributes.put('taackTableRowGroup', indent.toString())
            tr.attributes.put('taackTableRowGroupHasChildren', hasChildren.toString())
        }
        topElement.addChildren(tr)
        topElement = tr
    }

    void visitRowRO(Style style, boolean hasChildren) {
        visitRow style, hasChildren
    }

    @Override
    void visitRowEnd() {
        rowStyle = null
        topElement = topElement.toParentTaackTag(TaackTag.TABLE_ROW)
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
        topElement.addChildren(
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
        topElement.addChildren(
                new HTMLDiv().builder.addChildren(
                        new HTMLAnchor(isAjax, parameter.urlMapped(controller, action, id, params)).builder.addChildren(
                                new HTMLTxtContent(actionIcon.getHtml(i18n))
                        ).build()
                ).build()
        )
    }

    @Override
    void visitRowColumn(Integer colSpan, Integer rowSpan, Style style) {
        isInCol = true
        HTMLTd td = new HTMLTd(colSpan, rowSpan)
        topElement.addChildren(td)
        topElement = td

        firstInCol = true
    }

    @Override
    void visitRowGroupHeader(String groups, MethodClosure show, long id) {
        visitRowGroupHeader groups
    }
}