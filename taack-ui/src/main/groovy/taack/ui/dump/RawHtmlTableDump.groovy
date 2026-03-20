package taack.ui.dump

import grails.util.Pair
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import org.grails.datastore.gorm.GormEntity
import taack.ast.type.FieldInfo
import taack.ast.type.GetMethodReturn
import taack.ast.type.WidgetKind
import taack.render.TaackUiEnablerService
import taack.render.TaackUiService
import taack.ui.EnumOptions
import taack.ui.IEnumOptions
import taack.ui.dsl.UiMenuSpecifier
import taack.ui.dsl.common.ActionIcon
import taack.ui.dsl.common.Style
import taack.ui.dsl.table.IUiTableVisitor
import taack.ui.dsl.table.TableOption
import taack.ui.dump.common.BlockLog
import taack.ui.dump.html.element.*
import taack.ui.dump.html.form.BootstrapForm
import taack.ui.dump.html.form.BootstrapTableEdit
import taack.ui.dump.html.style.DisplayBlock
import taack.ui.dump.html.style.DisplayNone
import taack.ui.dump.html.style.Height2p8rem
import taack.ui.dump.html.table.*

import java.text.DateFormat
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat

import static taack.render.TaackUiService.tr

@CompileStatic
final class RawHtmlTableDump implements IUiTableVisitor {

    private static final Style DISPLAY_BLOCK_STYLE = new Style(null, 'display: block;')
    private static final DisplayBlock DISPLAY_BLOCK_DESCRIPTOR = new DisplayBlock()

    final String blockId
    final Parameter parameter
    final ThemableTable themableTable

    private int indent = -1
    private Map<Integer, Boolean> rowIndentIsExpended = [:]
    int colCount = 0
    boolean isInCol = false
    Style rowStyle = null
    int stripped = 0
    boolean isInHeader = false
    int level = 0
    boolean firstInCol = false
    private final IHTMLElement initialForm
    private Pair<String, String> initialSortingOrder
    private Pair<Date, String> initialLastReadingDate
    private final Map<String, HTMLInput> mapAdditionalHiddenParams = [:]
    private String selectColumnParamsKey
    private TableOption tableOption
    private TableOption cellOption
    protected final BlockLog blockLog
    final BootstrapTableEdit formThemed
    private boolean isQuickEdit = false
    private int formId = 0
    private IHTMLElement quickEditSubmitPlace = null

    RawHtmlTableDump(final BlockLog blockLog, final String id, final Parameter parameter) {
        this.blockLog = blockLog
        this.parameter = parameter
        this.themableTable = new ThemableTable(parameter.uiThemeService.themeSelector.themeMode, parameter.uiThemeService.themeSelector.themeSize)
        this.blockId = id ?: '' + parameter.modalId
        this.initialForm = new HTMLForm("/${parameter.applicationTagLib.controllerName}/${parameter.applicationTagLib.actionName}")
        formThemed = new BootstrapTableEdit(blockLog)

    }

    static final <T> String dataFormat(T value, String format, Locale locale = null) {
        if (value == null) return ''
        switch (value.class) {
            case BigDecimal:
                DecimalFormat df = new DecimalFormat(format ?: '#,###.00')
                return df.format(value)
            case Date:
                SimpleDateFormat sdf = new SimpleDateFormat(format ?: 'yyyy-MM-dd')
                return sdf.format(value)
            case Enum:
                String i18n = tr("enum.value.${value.toString()}", locale)
                return i18n != "enum.value.${value.toString()}" ? i18n : value.toString()
            case [Boolean, boolean]:
                return tr("default.boolean.${value.toString()}", locale)
            default:
                return TaackUiEnablerService.sanitizeString(value.toString())
        }
    }

    static final IHTMLElement displayCell(final String cell, final Style style, final String url) {
        Style displayBlock = (cell && style) ? DISPLAY_BLOCK_STYLE + style : DISPLAY_BLOCK_STYLE
        HTMLTxtContent cellHTML = new HTMLTxtContent(cell ?: '<br/>')
        if (!url) {
            HTMLSpan speedSpan = new HTMLSpan()
            speedSpan.addChildren(cellHTML)
            speedSpan.styleDescriptor = displayBlock
            return speedSpan
        }
        return new HTMLAnchor(true, url).builder.addChildren(cellHTML).build()
    }

    static final IHTMLElement displayCell(final FieldInfo fieldInfo, final Style style, Long id = null) {
        Style displayBlock = new Style('text-truncate', 'display: block;max-with: 256px;')
        if (fieldInfo && style) {
            displayBlock += style
        }
        String content = TaackUiEnablerService.sanitizeString(fieldInfo.value.toString())
        HTMLTxtContent cellHTML = new HTMLTxtContent(content ?: '<br/>')

        UiMenuSpecifier menu = TaackUiService.contextualMenuClosureFromField(fieldInfo)
        IHTMLElement.HTMLElementBuilder htmlBuilder = new HTMLSpan().builder.setStyle(displayBlock).putAttribute('title', content).addChildren(cellHTML)
        if (menu && fieldInfo.value) {
            String ident = fieldInfo.value.toString()
            String className = fieldInfo.fieldConstraint.field.type.simpleName
            if (GormEntity.isAssignableFrom(fieldInfo.value?.class))
                ident = (fieldInfo.value as GormEntity).ident()
            else if (id) {
                ident = id
                className = fieldInfo.fieldConstraint.field.declaringClass.simpleName
            }
            htmlBuilder.putAttribute('taackContextualMenu', className + ';' + fieldInfo.fieldName + ';' + ident)
        }
        return htmlBuilder.build()
    }

    @Override
    void visitTableEnd() {
        initialForm.builder.addChildren(mapAdditionalHiddenParams.values() as IHTMLElement[])
        blockLog.logExitBlock('visitTableEnd')
        blockLog.restorePosition()

        if (initialSortingOrder) {
            blockLog.topElement.children[0].getBuilder().putAttribute('initialSortField', initialSortingOrder.aValue)
        }
    }

    @Override
    void visitColumn(Integer colSpan, Integer rowSpan) {
        blockLog.logEnterBlock('visitColumn')
        blockLog.savePosition()
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
        blockLog.logEnterBlock('visitHeader')
        blockLog.savePosition()
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
        blockLog.logExitBlock('visitHeaderEnd')
        isInHeader = false
        blockLog.restorePosition()
        HTMLTBody tb = new HTMLTBody().builder.setTaackTag(TaackTag.TABLE_HEAD).build() as HTMLTBody
        blockLog.topElement.builder.addChildren(tb)
        blockLog.topElement = tb
    }

    @Override
    void visitColumnEnd() {
        blockLog.logExitBlock('visitColumnEnd')
        isInCol = false
        blockLog.restorePosition()
    }

    @Override
    void visitRow(Style style, boolean hasChildren) {
        blockLog.logEnterBlock('visitRow')
        blockLog.savePosition()
        rowStyle = style
        stripped++
        HTMLTr tr = new HTMLTr()
        tr.taackTag = TaackTag.TABLE_ROW
        if (style?.cssClassesString) tr.addClasses(style.cssClassesString)
        if (style?.cssStyleString) tr.putAttr('style', style.cssStyleString)
        if (indent >= 0) {
            tr.putAttr('taackTableRowGroup', indent.toString())
            tr.putAttr('taackTableRowGroupHasChildren', hasChildren.toString())
            if (hasChildren) {
                tr.putAttr('taackTableRowIsExpended', rowIndentIsExpended[indent].toString())
            }
            if (rowIndentIsExpended[indent - 1] == false && parameter.target == Parameter.RenderingTarget.WEB) {
                tr.setStyleDescriptor(new DisplayNone())
            }
        }
        blockLog.topElement.builder.addChildren(tr)
        blockLog.topElement = tr
        firstInCol = true
    }

    @Override
    void visitRowEnd() {
        blockLog.logExitBlock('visitRowEnd')
        rowStyle = null
        blockLog.restorePosition()
    }

    @Override
    void visitRowIndent(Boolean isExpended = false) {
        blockLog.logEnterBlock('visitRowIndent')
        indent++
        rowIndentIsExpended[indent] = rowIndentIsExpended[indent - 1] == false ? false : isExpended
    }

    @Override
    void visitRowIndentEnd() {
        blockLog.logExitBlock('visitRowIndentEnd')
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
        blockLog.logEnterBlock('visitRowColumn')
        blockLog.savePosition()
        isInCol = true
        IHTMLElement.HTMLElementBuilder tdBuilder = new HTMLTd(colSpan, rowSpan).builder
        if (style?.cssClassesString) tdBuilder.addClasses(style.cssClassesString)
        if (style?.cssStyleString) tdBuilder.putAttribute('style', style.cssStyleString)
        if (cellOption) {
            if (cellOption.uploadFileAction) {
                tdBuilder.putAttribute('taackDropAction', new Parameter().urlMapped(cellOption.uploadFileAction, cellOption.uploadFileActionParams))
            }
        }
        if (firstInCol) tdBuilder.addClasses('firstCellInGroup', "firstCellInGroup-${indent}")
        firstInCol = false
        tdBuilder.taackTag = TaackTag.TABLE_COL
        HTMLTd td = tdBuilder.build() as HTMLTd
        blockLog.topElement.builder.addChildren(td)
        blockLog.topElement = td
    }

    @Override
    void visitRowColumnEnd() {
        this.cellOption = null
        blockLog.logExitBlock('visitRowColumnEnd')
        isInCol = false
        blockLog.restorePosition()
    }

    @Override
    void visitTable() {
        blockLog.logEnterBlock('visitTable')
        blockLog.savePosition()
        blockLog.topElement.setTaackTag(TaackTag.TABLE)
        blockLog.topElement = themableTable.table(blockLog.topElement, blockId, tableOption)
    }

    @Override
    void visitTableWithoutFilter() {
        blockLog.logEnterBlock('visitTableWithoutFilter')
        blockLog.savePosition()
        IHTMLElement table = themableTable.table(blockLog.topElement, blockId, tableOption)
        blockLog.topElement.setTaackTag(TaackTag.TABLE)

//        List<HTMLInput> inputList = []

        parameter.paramsToKeep.each {
            if (it.value instanceof Collection || it.value instanceof String[]) {
                it.value.eachWithIndex { v, i ->
                    mapAdditionalHiddenParams.put(it.key + "[$i]", new HTMLInput(InputType.HIDDEN, v, it.key + "[$i]"))
                }
            } else {
                mapAdditionalHiddenParams.put(it.key, new HTMLInput(InputType.HIDDEN, it.value, it.key))
            }
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
                new HTMLSpan().builder.addClasses('sortColumn').setStyle(DISPLAY_BLOCK_DESCRIPTOR).putAttribute('sortField', RawHtmlFilterDump.getQualifiedName(fields)).addChildren(
                        new HTMLTxtContent(i18n)
                ).build()
        )
        if (addColumn) visitColumnEnd()

    }

    @Override
    void visitFieldHeader(final String i18n) {
        boolean addColumn = !isInCol
        if (addColumn) visitColumn(null, null)
        blockLog.topElement.builder.addChildren(
                new HTMLSpan().builder.setStyle(DISPLAY_BLOCK_DESCRIPTOR).addChildren(
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
    void visitRowField(final FieldInfo fieldInfo, Long id = null, final String format, final Style style) {
        if (TaackUiService.contextualMenuClosureFromField(fieldInfo) && fieldInfo.value) {
            boolean addColumn = !isInCol
            if (addColumn) visitColumn(null, null)
            blockLog.topElement.builder.addChildren(displayCell(fieldInfo, style, id ?: parameter.params.long('id')))
//, firstInCol, isInCol))
            if (addColumn) visitColumnEnd()
        } else {
            appendRowField(dataFormat(fieldInfo?.value, format, parameter.lcl), style, false)
        }
    }

    @Override
    void visitRowField(final GetMethodReturn fieldInfo, final String format, final Style style) {
        appendRowField(dataFormat(fieldInfo?.value, format, parameter.lcl), style, false)
    }

    private appendRowField(final String value, final Style style, final boolean sanitize) {
        boolean addColumn = !isInCol
        if (addColumn) visitColumn(null, null)
        if (sanitize) blockLog.topElement.builder.addChildren(displayCell(TaackUiEnablerService.sanitizeString(value), style, null))
        else blockLog.topElement.builder.addChildren(displayCell(value, style, null))
//, firstInCol, isInCol))
        if (addColumn) visitColumnEnd()

    }

    @Override
    void visitRowField(final String value, final Style style) {
        appendRowField(value, style, true)
    }

    @Override
    void visitRowField(Number value, NumberFormat locale, Style style) {
        appendRowField(locale.format(value), style, false)
    }

    @Override
    void visitRowField(Date value, DateFormat locale, Style style) {
        appendRowField(locale.format(value), style, false)
    }

    @Override
    void visitRowFieldRaw(final String value, final Style style) {
        appendRowField(value, style, false)
    }

    @Override
    void visitRowAction(String i18n, ActionIcon actionIcon, String key, String label) {
        boolean addColumn = !isInCol
        if (addColumn) visitColumn(null, null)
        visitRowAction(i18n, actionIcon, 'progress', 'echoSelect', null, [key: key, label: label], true)
        if (addColumn) visitColumnEnd()
    }

    @Override
    void visitPaginate(Number max, Number count) {
        if (max != -1 && count > max) {
            blockLog.topElement.builder.addChildren(new HTMLDiv().builder
                    .addClasses('taackTablePaginate')
                    .setStyle(new Height2p8rem())
                    .putAttribute('taackMax', max?.toString())
                    .putAttribute('taackOffset', parameter.params.long('offset')?.toString() ?: '0')
                    .putAttribute('taackCount', count?.toString())
                    .build()
            )
        }
    }

    @Override
    void setSortingOrder(Pair<String, String> sortingOrderParam) {
        initialSortingOrder = sortingOrderParam
        setSortingOrder(sortingOrderParam.aValue, sortingOrderParam.bValue)
    }

    @Override
    Pair<String, String> getSortingOrder() {
        return initialSortingOrder
    }

    void setSortingOrder(String sort, String order) {
        if (!parameter.order || !parameter.sort) {
            mapAdditionalHiddenParams.put 'sort', new HTMLInput(InputType.HIDDEN, sort, 'sort')
            mapAdditionalHiddenParams.put 'order', new HTMLInput(InputType.HIDDEN, order, 'order')
        }
    }

    @Override
    void setLastReadingDate(Pair<Date, String> lastReadingDate) {
        initialLastReadingDate = lastReadingDate
        if (!parameter.lastReadingDate) {
            mapAdditionalHiddenParams.put 'lastReadingDate', new HTMLInput(InputType.HIDDEN, getLastReadingDateString(), 'lastReadingDate')
        }
        if (!parameter.readingDateFieldString) {
            mapAdditionalHiddenParams.put 'readingDateFieldString', new HTMLInput(InputType.HIDDEN, lastReadingDate.bValue, 'readingDateFieldString')
        }
    }

    String getLastReadingDateString() {
        if (parameter.lastReadingDate)
            return parameter.lastReadingDate
        if (initialLastReadingDate)
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(initialLastReadingDate.aValue)
        return null
    }

    Date getLastReadingDate() {
        if (parameter.lastReadingDate)
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(parameter.lastReadingDate)
        if (initialLastReadingDate)
            return initialLastReadingDate.aValue
        return null
    }

    String getReadingDateFieldString() {
        if (parameter.readingDateFieldString)
            return parameter.readingDateFieldString
        if (initialLastReadingDate)
            return initialLastReadingDate.bValue
        return null
    }

    @Override
    void visitColumnSelect(String paramsKey) {
        blockLog.logEnterBlock('visitColumnSelect')
        blockLog.savePosition()
        isInCol = true
        selectColumnParamsKey = paramsKey ?: 'selectedItems'
        BootstrapForm f = new BootstrapForm(blockLog).builder.addChildren(
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
        blockLog.logExitBlock('visitColumnSelectEnd')
        isInCol = false
        blockLog.restorePosition()
    }

    @Override
    void visitRowSelect(final String value, final boolean isSelectable) {
        boolean addColumn = !isInCol
        if (addColumn) visitColumn(null, null)
        blockLog.topElement.builder.addChildren(
                HTMLInput.inputCheck(value, null, (parameter.applicationTagLib.params[selectColumnParamsKey]?.toString() ?: '').split(',').contains(value), !isSelectable)
                        .builder.putAttribute('paramsKey', selectColumnParamsKey).build()
        )
        if (addColumn) visitColumnEnd()
    }

    @Override
    void visitTableOption(TableOption tableOption) {
        this.tableOption = tableOption
    }

    @Override
    void visitRowDropAction(MethodClosure dropAction, Map<String, ? extends Serializable> parameters) {

    }

    @Override
    void visitCellDropAction(MethodClosure dropAction, Map<String, ? extends Serializable> parameters) {
        cellOption = new TableOption()
        cellOption.uploadFileAction = dropAction
        cellOption.uploadFileActionParams = parameters
    }

    @Override
    String getSelectColumnParamsKey() {
        return selectColumnParamsKey
    }

    @Override
    void visitRowFieldEdit(FieldInfo field, String format, Style style, IEnumOptions eos = null) {
        // TODO
//        boolean addColumn = !isInCol
//        if (addColumn) visitColumn(null, null)
//        blockLog.topElement.builder.addChildren(displayCell(TaackUiEnablerService.sanitizeString(value), style, null))
////, firstInCol, isInCol))
//        if (addColumn) visitColumnEnd()

        final String trI18n = ''
        final String qualifiedName = field.fieldName
        final Class type = field.fieldConstraint.field.type
        final boolean isBoolean = type == boolean || type == Boolean

        final boolean isEnum = field.fieldConstraint.field.type.isEnum()
        final boolean isListOrSet = Collection.isAssignableFrom(type)
        final boolean isDate = Date.isAssignableFrom(type)
        final boolean isNullable = field.fieldConstraint.nullable
        final boolean isFieldDisabled = !isQuickEdit //TODO: isDisabled(field)
        final String formId = 'form' + this.formId
        boolean addColumn = !isInCol
        if (addColumn) visitColumn(null, null)

        if (isBoolean) {
            blockLog.topElement = formThemed.booleanInput(formId, blockLog.topElement, qualifiedName, trI18n, isFieldDisabled, false, field.value as boolean)
        } else if (eos) {
            blockLog.topElement = formThemed.selects(formId, blockLog.topElement, qualifiedName, trI18n, eos, isListOrSet, isFieldDisabled, isNullable)
        } else if (isEnum || isListOrSet) {
            if (isEnum) {
                blockLog.topElement = formThemed.selects(formId, blockLog.topElement, qualifiedName, trI18n, new EnumOptions(field.fieldConstraint.field.type as Class<Enum>, qualifiedName, field.value as Enum), isListOrSet, isFieldDisabled, field.fieldConstraint.nullable)
            } else if (isListOrSet) {
                // Not yet
            }
        } else if (isDate) {
            blockLog.topElement = formThemed.dateInput(formId, blockLog.topElement, qualifiedName, trI18n, isFieldDisabled, isNullable, field.value as Date, field.fieldConstraint.widget == WidgetKind.DATETIME.name)
        } else {
            String valueString = RawHtmlFormDump.inputEscape(field.value?.toString())
            if (field.value instanceof Number) {
                valueString = parameter.nf.format(field.value)
            }
            blockLog.topElement = formThemed.normalInput(formId, blockLog.topElement, qualifiedName, trI18n, isFieldDisabled, isNullable, valueString)

        }
        quickEditSubmitPlace = blockLog.topElement
        if (addColumn) visitColumnEnd()
    }

    @Override
    void visitRowQuickEdit(Long id, MethodClosure apply) {
        blockLog.logEnterBlock('visitRowQuickEdit')
        HTMLForm f = new HTMLForm(parameter.urlMapped(apply, [id: id, isAjax: true])).builder.addClasses('taackTableInlineForm').addChildren(
                new HTMLInput(InputType.HIDDEN, parameter.applicationTagLib.controllerName, 'originController'),
                new HTMLInput(InputType.HIDDEN, parameter.applicationTagLib.actionName, 'originAction'),
                new HTMLInput(InputType.HIDDEN, parameter.brand, 'originBrand')
        ).build() as HTMLForm
//        f.builder.addChildren(new HTMLInput(InputType.HIDDEN, true, 'isAjax'))
        f.id = 'form' + formId
//        f.setTaackTag(TaackTag.TABLE_QUICK_EDIT)
        blockLog.topElement.builder.addChildren(f)
//        blockLog.topElement = f
        isQuickEdit = true
    }

    @Override
    void visitRowQuickEditEnd() {
        blockLog.logExitBlock('visitRowQuickEditEnd')
//        blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.TABLE_QUICK_EDIT).parent
        HTMLInput b = HTMLInput.inputSubmit('s', 'form' + formId)
        quickEditSubmitPlace.addChildren(b)
        isQuickEdit = false
        formId++
    }
}
