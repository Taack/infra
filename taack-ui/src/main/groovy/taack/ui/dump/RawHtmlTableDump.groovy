package taack.ui.dump

import grails.util.Pair
import groovy.transform.CompileStatic
import org.grails.datastore.gorm.GormEntity
import taack.ast.type.FieldInfo
import taack.ast.type.GetMethodReturn
import taack.render.TaackUiEnablerService
import taack.render.TaackUiService
import taack.ui.dsl.UiMenuSpecifier
import taack.ui.dsl.common.ActionIcon
import taack.ui.dsl.common.Style
import taack.ui.dsl.table.IUiTableVisitor
import taack.ui.dump.common.BlockLog
import taack.ui.dump.html.element.*
import taack.ui.dump.html.form.BootstrapForm
import taack.ui.dump.html.style.DisplayBlock
import taack.ui.dump.html.style.DisplayNone
import taack.ui.dump.html.table.*

import java.text.DecimalFormat
import java.text.SimpleDateFormat

import static taack.render.TaackUiService.tr

@CompileStatic
final class RawHtmlTableDump implements IUiTableVisitor {

    final String blockId
    final Parameter parameter
    final ThemableTable themableTable

    private int indent = -1
    private boolean rowIndentIsExpended = false
    int colCount = 0
    boolean isInCol = false
    Style rowStyle = null
    int stripped = 0
    boolean isInHeader = false
    int level = 0
    boolean firstInCol = false
    private final IHTMLElement initialForm
    private Pair<String, String> initalSortingOrder
    private final Map<String, HTMLInput> mapAdditionalHiddenParams = [:]
    private String selectColumnParamsKey

    protected final BlockLog blockLog

    RawHtmlTableDump(final BlockLog blockLog, final String id, final Parameter parameter) {
        this.blockLog = blockLog
        this.parameter = parameter
        this.themableTable = new ThemableTable(parameter.uiThemeService.themeSelector.themeMode, parameter.uiThemeService.themeSelector.themeSize)
        this.blockId = id ?: '' + parameter.modalId
        this.initialForm = new HTMLForm("/${parameter.applicationTagLib.controllerName}/${parameter.applicationTagLib.actionName}")
    }

    static final <T> String dataFormat(T value, String format) {
        if (value == null) return ''
        switch (value.class) {
            case BigDecimal:
                DecimalFormat df = new DecimalFormat(format ?: "#,###.00")
                return df.format(value)
            case Date:
                SimpleDateFormat sdf = new SimpleDateFormat(format ?: "yyyy-MM-dd")
                return sdf.format(value)
            case Enum:
                String i18n = tr("enum.value.${value.toString()}", null)
                return i18n != "enum.value.${value.toString()}" ? i18n : value.toString()
            case [Boolean, boolean]:
                return tr("default.boolean.${value.toString()}", null)
            default:
                return TaackUiEnablerService.sanitizeString(value.toString())
        }
    }

    static final IHTMLElement displayCell(final String cell, final Style style, final String url) {
        Style displayBlock = new Style(null, 'display: block;')
        if (cell && style) {
            displayBlock += style
        }
        HTMLTxtContent cellHTML = new HTMLTxtContent(cell ?: "<br>")
        if (!url) return new HTMLSpan().builder
                .setStyle(displayBlock)
                .addChildren(cellHTML).build()
        return new HTMLAnchor(true, url).builder.addChildren(cellHTML).build()
    }

    static final IHTMLElement displayCell(final FieldInfo fieldInfo, final Style style, Long id = null) {
        Style displayBlock = new Style(null, 'display: block;')
        if (fieldInfo && style) {
            displayBlock += style
        }
        HTMLTxtContent cellHTML = new HTMLTxtContent(TaackUiEnablerService.sanitizeString(fieldInfo.value.toString()) ?: "<br>")

        UiMenuSpecifier menu = TaackUiService.contextualMenuClosureFromField(fieldInfo)
        IHTMLElement.HTMLElementBuilder htmlBuilder = new HTMLSpan().builder.setStyle(displayBlock).addChildren(cellHTML)
        if (menu && fieldInfo.value) {
            String ident = fieldInfo.value.toString()
            String className = fieldInfo.fieldConstraint.field.type.simpleName
            if (GormEntity.isAssignableFrom(fieldInfo.value?.class))
                ident = (fieldInfo.value as GormEntity).ident()
            else if (id) {
                ident = id
                className = fieldInfo.fieldConstraint.field.declaringClass.simpleName
            }
            return htmlBuilder.putAttribute('taackContextualMenu', className + ';' + fieldInfo.fieldName + ';' + ident)
                        .build()
        }
        return htmlBuilder.build()
    }

    @Override
    void visitTableEnd() {
        initialForm.builder.addChildren(mapAdditionalHiddenParams.values() as IHTMLElement[])
        blockLog.exitBlock('visitTableEnd')
        blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.TABLE)

        if (initalSortingOrder) {
            blockLog.topElement.children[0].getBuilder().putAttribute("initialSortField", initalSortingOrder.aValue)
        }
    }

    @Override
    void visitColumn(Integer colSpan, Integer rowSpan) {
        blockLog.enterBlock('visitColumn')
        colCount++
        isInCol = true
        if (isInHeader) {
            HTMLTh th = new HTMLTh(colSpan, rowSpan)
            th.setTaackTag(TaackTag.TABLE_COL)
            blockLog.topElement.builder.addChildren(th)
            blockLog.topElement = th
        } else {
            HTMLTd th = new HTMLTd(colSpan, rowSpan)
            th.setTaackTag(TaackTag.TABLE_COL)
            blockLog.topElement.builder.addChildren(th)
            blockLog.topElement = th
        }
    }

    @Override
    void visitHeader() {
        blockLog.enterBlock('visitHeader')
        isInHeader = true
        HTMLTr tr = new HTMLTr()
        tr.addClasses('align-middle')
        blockLog.topElement.builder.addChildren(
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
        blockLog.topElement.builder.addChildren(tb)
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
        if (style?.cssClassesString) tr.addClasses(style.cssClassesString)
        if (style?.cssStyleString) tr.attributes.put('style', style.cssStyleString)
        if (indent >= 0) {
            tr.attributes.put('taackTableRowGroup', indent.toString())
            tr.attributes.put('taackTableRowGroupHasChildren', hasChildren.toString())
            if (hasChildren) {
                tr.attributes.put('taackTableRowIsExpended', rowIndentIsExpended.toString())
            }
        }
        if (indent > 0 && !rowIndentIsExpended && parameter.target == Parameter.RenderingTarget.WEB) {
            tr.setStyleDescriptor(new DisplayNone())
        }
        blockLog.topElement.builder.addChildren(tr)
        blockLog.topElement = tr
        firstInCol = true
    }

    @Override
    void visitRowEnd() {
        blockLog.exitBlock('visitRowEnd')
        rowStyle = null
        blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.TABLE_ROW)
    }

    @Override
    void visitRowIndent(Boolean isExpended = false) {
        blockLog.enterBlock('visitRowIndent')
        indent++
        rowIndentIsExpended = isExpended
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
        blockLog.topElement.builder.addChildren(
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
        if (style?.cssClassesString) td.addClasses(style.cssClassesString)
        if (style?.cssStyleString) td.attributes.put('style', style.cssStyleString)
        if (firstInCol) td.addClasses('firstCellInGroup', "firstCellInGroup-${indent}")
        firstInCol = false
        blockLog.topElement.builder.addChildren(td)
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

//        List<HTMLInput> inputList = []

        parameter.paramsToKeep.each {
            mapAdditionalHiddenParams.put(it.key, new HTMLInput(InputType.HIDDEN, it.value, it.key))
        }

        if (parameter.sort) mapAdditionalHiddenParams.put 'sort', new HTMLInput(InputType.HIDDEN, parameter.sort, 'sort')
        if (parameter.order) mapAdditionalHiddenParams.put 'order', new HTMLInput(InputType.HIDDEN, parameter.order, 'order')
        if (parameter.offset) mapAdditionalHiddenParams.put 'offset', new HTMLInput(InputType.HIDDEN, parameter.offset, 'offset')
        if (parameter.max) mapAdditionalHiddenParams.put 'max', new HTMLInput(InputType.HIDDEN, parameter.max, 'max')
        if (parameter.beanId) mapAdditionalHiddenParams.put 'id', new HTMLInput(InputType.HIDDEN, parameter.beanId, 'id')
        if (parameter.applicationTagLib.params['grouping']) mapAdditionalHiddenParams.put 'max', new HTMLInput(InputType.HIDDEN, parameter.applicationTagLib.params['grouping'], 'grouping')
        if (parameter.fieldName) mapAdditionalHiddenParams.put 'max', new HTMLInput(InputType.HIDDEN, parameter.fieldName, 'fieldName')
        if (parameter.tabIndex != null) mapAdditionalHiddenParams.put 'tabIndex', new HTMLInput(InputType.HIDDEN, parameter.tabIndex, 'tabIndex')

        initialForm.builder.addClasses('filter', 'rounded-3').putAttribute('taackFilterId', blockId).build()

        blockLog.topElement.builder.addChildren(initialForm)
        blockLog.topElement = table
    }


    @Override
    void visitSortableFieldHeader(String i18n, FieldInfo[] fields) {
        i18n ?= parameter.trField(fields)
        boolean addColumn = !isInCol
        if (addColumn) visitColumn(null, null)
        blockLog.topElement.builder.addChildren(
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
        blockLog.topElement.builder.addChildren(
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
    void visitRowField(final FieldInfo fieldInfo,Long id = null, final String format, final Style style) {
        if (TaackUiService.contextualMenuClosureFromField(fieldInfo) && fieldInfo.value) {
            boolean addColumn = !isInCol
            if (addColumn) visitColumn(null, null)
            blockLog.topElement.builder.addChildren(displayCell(fieldInfo, style, id ?: parameter.params.long('id')))//, firstInCol, isInCol))
            if (addColumn) visitColumnEnd()
        } else {
            visitRowField(dataFormat(fieldInfo?.value, format), style)
        }
    }

    @Override
    void visitRowField(final GetMethodReturn fieldInfo, final String format, final Style style) {
        visitRowField(dataFormat(fieldInfo?.value, format), style)
    }

    @Override
    void visitRowField(final String value, final Style style) {
        boolean addColumn = !isInCol
        if (addColumn) visitColumn(null, null)
        blockLog.topElement.builder.addChildren(displayCell(TaackUiEnablerService.sanitizeString(value), style, null))
//, firstInCol, isInCol))
        if (addColumn) visitColumnEnd()
    }

    @Override
    void visitRowFieldRaw(final String value, final Style style) {
        boolean addColumn = !isInCol
        if (addColumn) visitColumn(null, null)
        blockLog.topElement.builder.addChildren(displayCell(value, style, null))//, firstInCol, isInCol))
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
        if (max != -1 && count > max) {
            blockLog.topElement.builder.addChildren(new HTMLDiv().builder
                    .addClasses('taackTablePaginate')
                    .putAttribute('taackMax', max?.toString())
                    .putAttribute('taackOffset', parameter.params.long('offset')?.toString() ?: "0")
                    .putAttribute('taackCount', count?.toString())
                    .build()
            )
        }
    }

    @Override
    void setSortingOrder(Pair<String, String> sortingOrderParam) {
        initalSortingOrder = sortingOrderParam
        setSortingOrder(sortingOrderParam.aValue, sortingOrderParam.bValue)
    }

    @Override
    Pair<String, String> getSortingOrder() {
        return initalSortingOrder
    }

    void setSortingOrder(String sort, String order) {
        if (!parameter.order || !parameter.sort) {
            mapAdditionalHiddenParams.put 'sort', new HTMLInput(InputType.HIDDEN, sort, 'sort')
            mapAdditionalHiddenParams.put 'order', new HTMLInput(InputType.HIDDEN, order, 'order')
        }
    }

    @Override
    void visitColumnSelect(String paramsKey) {
        blockLog.enterBlock('visitColumnSelect')
        isInCol = true
        selectColumnParamsKey = paramsKey ?: 'selectedItems'
        BootstrapForm f = new BootstrapForm(blockLog).builder.putAttribute('onsubmit', "return this.querySelector('input[type=\\'hidden\\']').value !== ''").addChildren(
                new HTMLInput(InputType.HIDDEN, parameter.applicationTagLib.params[selectColumnParamsKey]?.toString(), selectColumnParamsKey)
        ).build() as BootstrapForm
        HTMLTh th = new HTMLTh().builder.setTaackTag(TaackTag.TABLE_COL).setStyle(Style.LABEL_WIDTH_AUTO_MIN).addChildren(
                new HTMLDiv().builder.addChildren(
                        new HTMLInput(InputType.CHECK, null, null).builder.putAttribute('paramsKey', selectColumnParamsKey).putAttribute('selectAll', 'true').build(),
                        new HTMLButton(null).builder
                                .addClasses('dropdown-toggle', 'btn')
                                .setStyle(new Style(null, 'margin-left: .25em;padding: 0 .1em;background: lightgrey;'))
                                .putAttribute('data-bs-toggle', 'dropdown')
                                .putAttribute('aria-expanded', 'false')
                                .putAttribute('data-bs-auto-close', 'outside')
                                .build(),
                        new HTMLDiv().builder.addClasses('dropdown-menu').addChildren(
                                f
                        ).build()
                ).build()
        ).build() as HTMLTh
        blockLog.topElement.builder.addChildren(th)
        blockLog.topElement = f

        // keep paramsKey in case of tableWithNoFilter
        mapAdditionalHiddenParams.put(selectColumnParamsKey, new HTMLInput(InputType.HIDDEN, parameter.applicationTagLib.params[selectColumnParamsKey]?.toString(), selectColumnParamsKey))
    }

    @Override
    void visitColumnSelectButton(final String buttonText, final String controller, final String action, Map<String, ?> params, final Boolean isAjax) {
        HTMLButton b
        if (controller && action) {
            b = new HTMLButton(buttonText).builder
                    .addClasses('btn')
                    .setStyle(new Style(null, 'display: block; width: 100%;'))
                    .putAttribute('type', 'submit')
                    .putAttribute('formaction', parameter.urlMapped(controller, action, null, params, isAjax))
                    .build() as HTMLButton
        } else {
            b = new HTMLButton(buttonText).builder
                    .addClasses('btn')
                    .setStyle(new Style(null, 'display: block; width: 100%; color: lightgrey; cursor: default;'))
                    .build() as HTMLButton
        }
        blockLog.topElement.addChildren(b)
    }

    @Override
    void visitColumnSelectEnd() {
        blockLog.exitBlock('visitColumnSelectEnd')
        isInCol = false
        blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.TABLE_COL)
    }

    @Override
    void visitRowSelect(final String value, final boolean isSelectable) {
        boolean addColumn = !isInCol
        if (addColumn) visitColumn(null, null)
        blockLog.topElement.builder.addChildren(
                HTMLInput.inputCheck(value, null, (parameter.applicationTagLib.params[selectColumnParamsKey]?.toString() ?: '').split(",").contains(value), !isSelectable)
                        .builder.putAttribute('paramsKey', selectColumnParamsKey).build()
        )
        if (addColumn) visitColumnEnd()
    }

    @Override
    String getSelectColumnParamsKey() {
        return selectColumnParamsKey
    }
}
