package taack.ui.dump

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.ast.type.FieldInfo
import taack.ast.type.GetMethodReturn
import taack.render.TaackUiEnablerService
import taack.render.TaackUiService
import taack.ui.dsl.common.ActionIcon
import taack.ui.dsl.common.Style
import taack.ui.dsl.helper.Utils
import taack.ui.dsl.kanban.IUiKanbanVisitor
import taack.ui.dump.common.BlockLog
import taack.ui.dump.html.element.*

import java.text.NumberFormat

@CompileStatic
final class RawHtmlKanbanDump implements IUiKanbanVisitor {

    final String blockId
    final Parameter parameter
    private final IHTMLElement initialForm
    private final Map<String, HTMLInput> mapAdditionalHiddenParams = [:]
    protected final BlockLog blockLog

    RawHtmlKanbanDump(final BlockLog blockLog, final String id, final Parameter parameter) {
        this.blockLog = blockLog
        this.parameter = parameter
        this.blockId = id ?: '' + parameter.modalId
        this.initialForm = new HTMLForm("/${parameter.applicationTagLib.controllerName}/${parameter.applicationTagLib.actionName}")
    }

    static IHTMLElement displayContent(final String content, final Style style, final String url) {
        RawHtmlTableDump.displayCell(content, style, url)
    }

    static IHTMLElement displayContent(final FieldInfo fieldInfo, final Style style, Long id = null) {
        RawHtmlTableDump.displayCell(fieldInfo, style, id)
    }

    @Override
    void visitKanban() {
        blockLog.enterBlock('visitKanban')
        HTMLDiv kanbanDiv = new HTMLDiv().builder.addClasses('row gx-2').setTaackTag(TaackTag.KANBAN).putAttribute('taackKanbanId', blockId).build() as HTMLDiv
        blockLog.topElement.builder.addChildren(kanbanDiv)
        blockLog.topElement = kanbanDiv
    }

    @Override
    void visitKanbanEnd() {
        blockLog.exitBlock('visitKanbanEnd')
        initialForm.builder.addChildren(mapAdditionalHiddenParams.values() as IHTMLElement[])
        blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.KANBAN)
    }

    @Override
    void visitColumn(MethodClosure action, Map<String, ? extends Object> params) {
        blockLog.enterBlock('visitColumn')
        HTMLDiv columnDiv = new HTMLDiv().builder.addClasses('kanban-column col m-2')
                .putAttribute('taackDropAction', action ? parameter.urlMapped(Utils.getControllerName(action), action.method.toString(), params) : null)
                .setTaackTag(TaackTag.KANBAN_COL).build() as HTMLDiv
        blockLog.topElement.builder.addChildren(columnDiv)
        blockLog.topElement = columnDiv
    }

    @Override
    void visitColumnEnd() {
        blockLog.exitBlock('visitColumnEnd')
        blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.KANBAN_COL).parent
    }

    @Override
    void visitColumnHeader(String i18n, Style style) {
        IHTMLElement.HTMLElementBuilder chBuilder = new HTMLDiv().builder
        chBuilder.addClasses(style?.cssClassesString ?: 'kanban-column-header')
        if (style?.cssStyleString) chBuilder.putAttribute('style', style.cssStyleString)
        blockLog.topElement.builder.addChildren(chBuilder.addChildren(new HTMLTxtContent(i18n)).build())
    }

    @Override
    void visitCard(FieldInfo cardId) {
        blockLog.enterBlock('visitCard')
        String cardIdValue = null
        if (cardId?.value != null) {
            cardIdValue = cardId.fieldName == 'selfObject' ? cardId.value['id']?.toString() : cardId.value.toString()
        }
        HTMLDiv cardDiv = new HTMLDiv().builder.addClasses('kanban-card').setTaackTag(TaackTag.KANBAN_CARD)
                .putAttribute('cardId', cardIdValue ?: '')
                .putAttribute('draggable', 'true').build() as HTMLDiv
        blockLog.topElement.builder.addChildren(cardDiv)
        blockLog.topElement = cardDiv
    }

    @Override
    void visitCardEnd() {
        blockLog.exitBlock('visitCardEnd')
        blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.KANBAN_CARD).parent
    }

    @Override
    void visitCardFieldRaw(String value, Style style) {
        blockLog.topElement.builder.addChildren(displayContent(value, style, null))
    }

    @Override
    void visitCardField(String value, Style style) {
        blockLog.topElement.builder.addChildren(displayContent(TaackUiEnablerService.sanitizeString(value), style, null))
    }

    @Override
    void visitCardField(FieldInfo fieldInfo, Long id = null, String format, Style style) {
        if (TaackUiService.contextualMenuClosureFromField(fieldInfo) && fieldInfo.value) {
            blockLog.topElement.builder.addChildren(displayContent(fieldInfo, style, id ?: parameter.params.long('id')))
        } else {
            visitCardField(RawHtmlTableDump.dataFormat(fieldInfo?.value, format, parameter.lcl), style)
        }
    }

    @Override
    void visitCardField(GetMethodReturn fieldInfo, String format, Style style) {
        visitCardField(RawHtmlTableDump.dataFormat(fieldInfo?.value, format, parameter.lcl), style)
    }

    @Override
    void visitCardField(Number value, NumberFormat locale, Style style) {
        visitCardField(locale.format(value), style)
    }

    @Override
    void visitCardAction(String i18n, ActionIcon actionIcon, String controller, String action, Long id, Map<String, ?> params, Boolean isAjax) {
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
    void visitCardAction(String linkText, String controller, String action, Long id, Map<String, ?> params, Boolean isAjax) {
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
    void visitCardAction(String i18n, ActionIcon actionIcon, String key, String label) {
        visitCardAction(i18n, actionIcon, 'progress', 'echoSelect', null, [key: key, label: label], true)
    }
}
