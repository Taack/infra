package taack.ui.dump

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import org.grails.datastore.gorm.GormEntity
import taack.ast.type.FieldInfo
import taack.ui.IEnumOption
import taack.ui.IEnumOptions
import taack.ui.dsl.*
import taack.ui.dsl.block.BlockSpec
import taack.ui.dsl.block.IUiBlockVisitor
import taack.ui.dsl.common.ActionIcon
import taack.ui.dsl.common.Style
import taack.ui.dsl.filter.IUiFilterVisitor
import taack.ui.dsl.helper.Utils
import taack.ui.dsl.kanban.IUiKanbanVisitor
import taack.ui.dsl.menu.MenuSpec
import taack.ui.dsl.table.IUiTableVisitor
import taack.ui.dump.common.BlockLog
import taack.ui.dump.html.block.*
import taack.ui.dump.html.element.*
import taack.ui.dump.html.layout.HTMLEmpty
import taack.ui.dump.html.menu.BootstrapMenu

@CompileStatic
final class RawHtmlBlockDump implements IUiBlockVisitor {
    final String modalId
    String futurCurrentAjaxBlockId

    Map<String, byte[]> mailAttachment = [:]

    int poll = 0

    private int tabOccurrence = 0

    final BootstrapBlock block
    final Parameter parameter
    final RenderDecision renderDecision

    private final BlockLog blockLog

    void simpleLog(String toPrint) {
        blockLog.simpleLog(RawHtmlBlockDump.simpleName + '::' + toPrint)
    }

    void logEnterBlock(String toPrint) {
        blockLog.logEnterBlock(RawHtmlBlockDump.simpleName + '::' + toPrint)
    }

    void logExitBlock(String toPrint) {
        blockLog.logExitBlock(RawHtmlBlockDump.simpleName + '::' + toPrint)
    }

    RawHtmlBlockDump(final Parameter parameter) {
        this.parameter = parameter
        this.modalId = modalId
        blockLog = new BlockLog(parameter.uiThemeService.themeSelector)
        renderDecision = new RenderDecision(parameter, blockLog)
        block = new BootstrapBlock(blockLog, this.parameter)
        blockLog.topElement = new HTMLEmpty()
        blockLog.topElement.setTaackTag(TaackTag.BLOCK)
    }

    @Override
    boolean doRenderElement(String id = null) {
        renderDecision.shouldRender(id)
    }

    @Override
    boolean doRenderLayoutElement() {
        renderDecision.shouldRenderLayout()
    }

    @Override
    void setRenderTab(boolean isRender) {
        renderDecision.renderTab = isRender || parameter.target == Parameter.RenderingTarget.MAIL
    }

    @Override
    void visitBlock() {
        logEnterBlock('visitBlock')
        blockLog.savePosition()
        blockLog.topElement = block.block(blockLog.topElement, "${parameter.applicationTagLib.controllerName}-${parameter.applicationTagLib.actionName}")
    }

    @Override
    void visitBlockEnd() {
        logExitBlock('visitBlockEnd')
        blockLog.restorePosition()
    }

    @Override
    void visitBlockHeader() {
        logEnterBlock('visitBlockHeader')
        blockLog.savePosition()
        blockLog.topElement.setTaackTag(TaackTag.MENU_BLOCK)
        blockLog.topElement = block.blockHeader(blockLog.topElement)
    }

    @Override
    void visitBlockHeaderEnd() {
        logExitBlock('visitBlockHeaderEnd')
        blockLog.restorePosition()
    }

    @Override
    void visitCol(final BlockSpec.Width width) {
        logEnterBlock('visitCol')
        blockLog.savePosition()
        blockLog.topElement.setTaackTag(TaackTag.COL)
        blockLog.topElement = block.col(blockLog.topElement, width)
    }

    @Override
    void visitColEnd() {
        logExitBlock('visitColEnd')
        blockLog.restorePosition()
    }

    /**
     * 2 modes of rendering:
     * * either we transmit to the browser which blocks that should be updated
     * * either we transmit a div, placeholder for the subsequent updates
     *
     * @param id
     */
    @Override
    void visitAjaxBlock(String id) {
        logEnterBlock('visitAjaxBlock id: ' + id + ' parameter.isAjaxRendering: ' + parameter.isAjaxRendering + ' parameter.ajaxBlockId: ' + parameter.ajaxBlockId)
        blockLog.savePosition()
        if (!id) id = parameter.ajaxBlockId
        blockLog.topElement.setTaackTag(TaackTag.AJAX_BLOCK)

        // doAjaxRendering == true : to ajax refresh the content of an existing block
        boolean doAjaxRendering = parameter.isAjaxRendering // must be in ajaxMode
        if ((id != renderDecision.explicitAjaxBlockId || renderDecision.isModal) && id != renderDecision.currentAjaxBlockId) {
            // if many blocks in the same response, only redraw current block
            doAjaxRendering = false
        }
        if (parameter.tabIndex != null && !parameter.ajaxBlockId) {
            // insert content to an empty tab, so not for the refresh of an existing block
            doAjaxRendering = false
        }
        if (renderDecision.isModal && !renderDecision.isRefreshing) {
            // Open a new modal (Not refreshing an existing modal), so not for the refresh of an existing block
            doAjaxRendering = false
        }
        if (!doAjaxRendering && parameter.targetAjaxBlockId) {
            // a highest priority interface to force the refresh of a target block
            id = parameter.targetAjaxBlockId
            doAjaxRendering = true
        }
        if (renderDecision.poke) doAjaxRendering = true
        blockLog.topElement = block.blockAjax(blockLog.topElement, id, doAjaxRendering)
    }

    @Override
    void visitAjaxBlockEnd() {
        logExitBlock('visitAjaxBlockEnd')
        renderDecision.currentAjaxBlockId = null
        blockLog.restorePosition()
    }

    @Override
    void visitForm(UiFormSpecifier formSpecifier) {
        blockLog.logStayBlock('visitForm')
        formSpecifier.visitForm(new RawHtmlFormDump(blockLog, parameter))
    }


    @Override
    void visitShow(final UiShowSpecifier uiShowSpecifier) {
        blockLog.logStayBlock('visitShow')
        ByteArrayOutputStream out = new ByteArrayOutputStream(4096)
        if (uiShowSpecifier) uiShowSpecifier.visitShow(new RawHtmlShowDump(id, out, parameter))
        blockLog.topElement.addChildren(new HTMLOutput(out))
    }

    @Override
    void visitCloseModalAndUpdateBlock() {
        logEnterBlock('visitCloseModalAndUpdateBlock')
        blockLog.topElement = new HTMLAjaxCloseModal()
    }

    @Override
    void visitCloseModalAndUpdateBlockEnd() {
        logExitBlock('visitCloseModalAndUpdateBlockEnd')
    }

    @Override
    void visitHtmlBlock(String html, Style style) {
        blockLog.logStayBlock('visitHtmlBlock')
        blockLog.topElement.addChildren(
                new HTMLDiv().builder.addClasses(style?.cssClassesString).putAttribute('style', style?.cssStyleString).addChildren(
                        new HTMLTxtContent(html)
                ).build()
        )
    }

    @Override
    void visitIframe(String url, String cssHeight) {
        blockLog.logStayBlock('visitIframe')
        blockLog.topElement.addChildren(
                new HTMLIFrame(url, cssHeight)
        )
    }

    @Override
    void visitPoll(int millis, MethodClosure polledAction) {
        logEnterBlock('visitPoll ' + millis + ' ms ' + polledAction.toString())
        poll = millis
        HTMLAjaxPoll poll = new HTMLAjaxPoll(millis, parameter.urlMapped(polledAction))
        blockLog.topElement.addChildren(poll)
        blockLog.topElement.setTaackTag(TaackTag.POLL)

    }

    @Override
    void visitPollEnd() {
        logEnterBlock('visitPollEnd')
        blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.MODAL)
        poll = 0
    }

    @Override
    void visitTable(String id, final UiTableSpecifier tableSpecifier) {
        blockLog.logStayBlock('visitTable ' + id)
        tableSpecifier.visitTableWithNoFilter(new RawHtmlTableDump(blockLog, id, parameter))
    }

    @Override
    void visitTableFilter(final String id,
                          final UiFilterSpecifier filterSpecifier,
                          final UiTableSpecifier tableSpecifier) {
        blockLog.logStayBlock('visitTableFilter ' + id)
        visitCol(BlockSpec.Width.QUARTER)
        IUiTableVisitor tableVisitor = new RawHtmlTableDump(blockLog, id, parameter)
        IUiFilterVisitor filterVisitor = new RawHtmlFilterDump(blockLog, id, parameter)
        filterSpecifier.visitFilter(filterVisitor)
        visitColEnd()
        visitCol(BlockSpec.Width.THREE_QUARTER)
        tableSpecifier.visitTable(tableVisitor)
        if (tableVisitor.getSortingOrder() && (!parameter.order || !parameter.sort)) {
            filterVisitor.setAdditionalParams('sort', tableVisitor.getSortingOrder().aValue)
            filterVisitor.setAdditionalParams('order', tableVisitor.getSortingOrder().bValue)
        }
        if (tableVisitor.getLastReadingDateString()) {
            filterVisitor.setAdditionalParams('lastReadingDate', tableVisitor.getLastReadingDateString())
            filterVisitor.setAdditionalParams('readingDateFieldString', tableVisitor.getReadingDateFieldString())
        }
        if (tableVisitor.getSelectColumnParamsKey()) {
            String paramsKey = tableVisitor.getSelectColumnParamsKey()
            filterVisitor.setAdditionalParams(paramsKey, parameter.applicationTagLib.params[paramsKey]?.toString())
        }
        filterVisitor.addHiddenInputs()
        visitColEnd()
    }

    @Override
    void visitKanban(String id, UiKanbanSpecifier kanbanSpecifier) {
        blockLog.logStayBlock('visitKanban ' + id)
        kanbanSpecifier.visitKanbanWithoutFilter(new RawHtmlKanbanDump(blockLog, id, parameter))
    }

    @Override
    void visitKanbanFilter(final String id,
                           final UiFilterSpecifier filterSpecifier,
                           final UiKanbanSpecifier kanbanSpecifier) {
        blockLog.logStayBlock('visitKanbanFilter ' + id)
        visitCol(BlockSpec.Width.QUARTER)
        IUiKanbanVisitor kanbanVisitor = new RawHtmlKanbanDump(blockLog, id, parameter)
        IUiFilterVisitor filterVisitor = new RawHtmlFilterDump(blockLog, id, parameter)
        filterSpecifier.visitFilter(filterVisitor)
        visitColEnd()
        visitCol(BlockSpec.Width.THREE_QUARTER)
        kanbanSpecifier.visitKanban(kanbanVisitor)
        filterVisitor.addHiddenInputs()
        visitColEnd()
    }

//    @Override
//    void visitChart(final UiChartSpecifier chartSpecifier) {
//        blockLog.stayBlock('visitChart')
//        ByteArrayOutputStream out = new ByteArrayOutputStream(4096)
//        chartSpecifier.visitChart(new RawHtmlChartDump(out, 'ajaxBlockId'))
//        blockLog.topElement.addChildren(new HTMLOutput(out))
//    }

    @Override
    void visitDiagramFilter(final UiDiagramSpecifier diagramSpecifier, final UiFilterSpecifier filterSpecifier) {
        blockLog.logStayBlock('visitDiagramFilter')
        visitCol(BlockSpec.Width.QUARTER)
        IUiFilterVisitor filterVisitor = new RawHtmlFilterDump(blockLog, id, parameter)
        filterSpecifier.visitFilter(filterVisitor)
        visitColEnd()
        visitCol(BlockSpec.Width.THREE_QUARTER)

        visitDiagram(diagramSpecifier)

        filterVisitor.addHiddenInputs()
        visitColEnd()
    }

    @Override
    void visitDiagram(final UiDiagramSpecifier diagramSpecifier) {
        blockLog.logStayBlock('visitDiagram')
        if (parameter.target == Parameter.RenderingTarget.MAIL) {
            RawHtmlDiagramDump diagramDump = new RawHtmlDiagramDump(new ByteArrayOutputStream(4096), blockLog, mailAttachment)
            diagramSpecifier.visitDiagram(diagramDump, UiDiagramSpecifier.DiagramBase.PNG)
        } else
            diagramSpecifier.visitDiagram(new RawHtmlDiagramDump(new ByteArrayOutputStream(4096), blockLog), UiDiagramSpecifier.DiagramBase.SVG)
    }

    @Override
    void visitCloseModal(final Map<String, String> idValueMap, FieldInfo[] fields = null) {
        blockLog.logStayBlock('visitCloseModal')
        blockLog.topElement.addChildren(new HTMLAjaxCloseLastModal(idValueMap))
        for (FieldInfo fi : fields) {
            String label
            if (fi.value == null) {
                label = ''
            } else if (parameter.nf && fi.value instanceof Number) {
                label = parameter.nf.format(fi.value)
            } else {
                label = fi.value.toString()
            }
            blockLog.topElement.addChildren(new HTMLFieldInfo(fi.fieldName, label)) // for normal field
            blockLog.topElement.addChildren(new HTMLFieldInfo(fi.fieldName + 'String', label)) // for ajaxField whose first input is to display object label
            blockLog.topElement.addChildren(new HTMLFieldInfo(fi.fieldName + 'Id', fi.value && fi.value instanceof GormEntity ? (fi.value as GormEntity).ident().toString() : label)) // for ajaxField whose second input is hidden to transfer object key value
        }
    }

    @Override
    void visitBlockTab(final String i18n) {
        logEnterBlock('visitBlockTab')
        blockLog.savePosition()
        currentTabNames << i18n
        blockLog.topElement.setTaackTag(TaackTag.TAB)
        blockLog.topElement = block.tab(blockLog.topElement, tabOccurrence++)
    }

    @Override
    void visitBlockTabEnd() {
        logExitBlock('visitBlockTabEnd')
        blockLog.restorePosition()
    }

    private List<String> currentTabNames = []

    @Override
    void visitBlockTabs() {
        logEnterBlock('visitBlockTabs')
        blockLog.savePosition()
        blockLog.topElement.setTaackTag(TaackTag.TABS)
        blockLog.topElement = new HTMLEmpty()
    }

    @Override
    void visitBlockTabsEnd() {
        logExitBlock('visitBlockTabsEnd')
        IHTMLElement tabsContent = blockLog.topElement
        IHTMLElement savedParent = blockLog.peekPosition()
        Map<String, Object> p = parameter.params.sort()
        if (p) p.remove('tabIndex')
        blockLog.topElement = block.tabs(savedParent, currentTabNames, parameter.urlMapped(parameter.applicationTagLib.controllerName, parameter.applicationTagLib.actionName, parameter.beanId, p))
        blockLog.topElement.addChildren(tabsContent)
        blockLog.restorePosition()
    }

    @Override
    void visitBlockPoke(boolean update) {
        renderDecision.poke = renderDecision.poke || update
    }

    @Override
    void visitBlockPokeEnd() {
        renderDecision.poke = false
    }

    @Override
    void visitCustom(final String html, Style style) {
        blockLog.logStayBlock('visitCustom')
        visitHtmlBlock(html, style)
    }

    @Override
    void visitModal(boolean reloadWhenClose) {
        logEnterBlock('visitModal modalId:' + modalId + ' isModalRefresh:' + renderDecision.isRefreshing)
        blockLog.savePosition()
        renderDecision.isModal = true
        parameter.isModal = true
        HTMLAjaxModal modal = new HTMLAjaxModal(renderDecision.isRefreshing, reloadWhenClose)
        blockLog.topElement.addChildren(modal)
        blockLog.topElement.setTaackTag(TaackTag.MODAL)
        blockLog.topElement = modal
    }

    @Override
    void visitModalEnd() {
        logExitBlock('visitModalEnd')
        renderDecision.isModal = false
        blockLog.restorePosition()
    }

    @Override
    String getExplicitAjaxBlockId() {
        renderDecision.explicitAjaxBlockId
    }

    @Override
    void setExplicitAjaxBlockId(String id) {
        renderDecision.explicitAjaxBlockId = id
    }

    @Override
    Map getParameterMap() {
        parameter.applicationTagLib.params
    }

    @Override
    void visitRow() {
        logEnterBlock('visitRow')
        blockLog.savePosition()
        blockLog.topElement.setTaackTag(TaackTag.ROW)
        blockLog.topElement = block.row(blockLog.topElement)
    }

    @Override
    void visitRowEnd() {
        logExitBlock('visitRowEnd')
        blockLog.restorePosition()
    }

    BootstrapMenu menu
    boolean insideMenuSection = false

    private boolean menuLabelSaved = false

    @Override
    void visitMenuLabel(String i18n, boolean hasClosure) {
        if (hasClosure) {
            blockLog.savePosition()
            menuLabelSaved = true
            logEnterBlock('visitMenuLabel ' + i18n)
        } else {
            menuLabelSaved = false
            blockLog.logStayBlock('visitMenuLabel ' + i18n)
        }
        blockLog.topElement.setTaackTag(TaackTag.LABEL)
        blockLog.topElement = menu.label(blockLog.topElement, i18n, hasClosure)
    }

    @Override
    void visitMenuLabelEnd() {
        logExitBlock('visitMenuLabelEnd')
        if (menuLabelSaved) {
            blockLog.restorePosition()
        } else {
            blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.LABEL)
        }
    }

    @Override
    void visitMenuStart(MenuSpec.MenuMode menuMode, String ajaxBlockId) {
        logEnterBlock('visitMenuStart futurCurrentAjaxBlockId: ' + ajaxBlockId)
        blockLog.savePosition()
        futurCurrentAjaxBlockId = ajaxBlockId
        blockLog.topElement.setTaackTag(TaackTag.MENU)
        menu = new BootstrapMenu(parameter.target == Parameter.RenderingTarget.MAIL, blockLog)
        blockLog.topElement = menu.menuStart(blockLog.topElement)
    }

    @Override
    void visitMenuStartEnd() {
        logExitBlock('visitMenuStartEnd')
        futurCurrentAjaxBlockId = null
        blockLog.restorePosition()
    }

    private void splitMenu() {
        if (!blockLog.topElement.testParentTaackTag(TaackTag.MENU_SPLIT)) {
            blockLog.logStayBlock('splitMenu +++')
            blockLog.topElement = menu.splitMenuStart(blockLog.topElement.parent)
            blockLog.topElement.setTaackTag(TaackTag.MENU_SPLIT)
            blockLog.logStayBlock('splitMenu ---')
        } else {
            blockLog.logStayBlock('splitMenu -+-+-+-')
        }

    }

    private void splitMenuEnd() {
        if (blockLog.topElement.testParentTaackTag(TaackTag.MENU_SPLIT)) {
            blockLog.logStayBlock('splitMenuEnd TRUE')
            blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.MENU_SPLIT)
        } else {
            blockLog.logStayBlock('splitMenuEnd FALSE')
        }
    }

    @Override
    void visitMenu(String controller, String action, Map<String, ?> params) {
        blockLog.logStayBlock('visitMenu')
        visitLabeledSubMenu(null, controller, action, params)
    }


    @Override
    void visitSubMenu(String controller, String action, Map<String, ?> params) {
        blockLog.logStayBlock('visitSubMenu')
        visitLabeledSubMenu(null, controller, action, params)
    }

    void visitLabeledSubMenu(String i18n, String controller, String action, Map<String, ?> params) {
        i18n ?= parameter.trField(controller, action, params?.containsKey('id'))

        blockLog.logStayBlock('visitLabeledSubMenu ' + i18n)
        Map cp = parameter.params
        if (params) {
            if (cp.containsKey('lang') && !params.containsKey('lang')) cp.remove('lang')
            if (cp.containsKey('action')) cp.remove('action')
            if (cp.containsKey('controller')) cp.remove('controller')
            if (futurCurrentAjaxBlockId && parameter.controllerName == controller && parameter.actionName == action) {
                params.put('ajaxBlockId', futurCurrentAjaxBlockId)
                params.put('refresh', 'true')
                if (parameter.tabId && parameter.tabIndex) {
                    params.put('tabId', parameter.tabId)
                    params.put('tabIndex', parameter.tabIndex)
                }
            }
        }

        boolean isParamsEquals = true

        if (params) {
            Map<String, ?> p = [:]
            cp?.each {
                if (it.key != 'isAjax' && it.value != null && !it.value.toString().empty)
                    p.put(it.key as String, it.value)
            }
            params.each {
                p.remove(it.key, it.value?.toString())
            }
            isParamsEquals = p.isEmpty() && !cp.isEmpty()
        }

        blockLog.topElement = menu.menu(blockLog.topElement, i18n, futurCurrentAjaxBlockId?.size() > 0, futurCurrentAjaxBlockId, parameter.urlMapped(controller, action, params), controller == parameter.controllerName && action == parameter.actionName && isParamsEquals, insideMenuSection)
    }

    @Override
    void visitMenuSection(String i18n, MenuSpec.MenuPosition position) {
        logEnterBlock('visitMenuSection ' + i18n)
        insideMenuSection = true
        menu.section(blockLog.topElement, i18n)
    }

    @Override
    void visitMenuSectionEnd() {
        logExitBlock('visitMenuSectionEnd')
        insideMenuSection = false
    }

    @Override
    void visitSubMenuIcon(String i18n, ActionIcon actionIcon, String controller, String action, Map<String, ?> params, boolean isModal = false) {
        i18n ?= parameter.trField(controller, action, params?.containsKey('id'))
        blockLog.logStayBlock('visitSubMenuIcon ' + i18n)
        if (!blockLog.topElement.testParentTaackTag(TaackTag.MENU_SPLIT, TaackTag.MENU_COL)) {
            splitMenu()
        }

        if (parameter.target != Parameter.RenderingTarget.MAIL)
            menu.menuIcon(blockLog.topElement, actionIcon.getHtml(i18n, 24), parameter.urlMapped(controller, action, params, isModal), isModal)
        else
            menu.menu(blockLog.topElement, i18n, false, null, parameter.urlMapped(controller, action, params, isModal))
    }

    @Override
    void visitMenuIconWithClosure(String i18n, ActionIcon actionIcon) {
        logEnterBlock('visitMenuIconWithClosure ' + i18n)
        blockLog.savePosition()
        blockLog.topElement.setTaackTag(TaackTag.MENU_COL)

        blockLog.topElement = menu.label(blockLog.topElement, actionIcon.getHtml(i18n, 24), true).builder.build()
    }

    @Override
    void visitMenuIconWithClosureEnd(Style style) {
        logExitBlock('visitMenuIconWithClosureEnd')
        blockLog.restorePosition()
        if (style) blockLog.topElement.children.last().setStyleDescriptor(style)
    }

    @Override
    void visitMenuSelect(String paramName, IEnumOptions enumOptions, Map<String, ?> params) {
        logEnterBlock('visitMenuSelect')
        String valueSelected = params[paramName]
        IEnumOption enumSelected = enumOptions.getOptions().find { it.key == valueSelected }
        String controller = params['controller'] as String
        String action = params['action'] as String
        visitLabeledSubMenu(enumSelected.value, controller, action, params)
        for (def eo in enumOptions.getOptions()) {
            params.put(paramName, eo.key)
            visitLabeledSubMenu(eo.value, controller, action, params)
        }
    }

    @Override
    void visitMenuSearch(MethodClosure action, String q, Class<? extends GormEntity>[] aClasses) {
        String i18n = parameter.trField(Utils.getControllerName(action), action.method.toString(), false)

        logEnterBlock('visitMenuSearch')
        splitMenu()
        menu.menuSearch(blockLog.topElement, i18n, q?.replace('"', '&quot;'), parameter.urlMapped(Utils.getControllerName(action), action.method))
        splitMenuEnd()
        logExitBlock('visitMenuSearch')
    }

    @Override
    void visitMenuOptions(IEnumOptions enumOptions) {
        logEnterBlock('visitMenuOptions')
        splitMenu()
        String selectedOptionKey = parameter.params[enumOptions.paramKey]

        IEnumOption currentOption = selectedOptionKey ? (enumOptions.options.find { it.key == selectedOptionKey }) : enumOptions.currents?.first() as IEnumOption
        String selectedOptionValue = currentOption ? currentOption.value : selectedOptionKey
        String img = currentOption && currentOption.asset? parameter.applicationTagLib.img(file: currentOption.asset, width: 20, style: 'padding: .5em 0em;') : ''

        blockLog.savePosition()
        blockLog.topElement = menu.menuOptions(blockLog.topElement, img, selectedOptionValue)

        String controller = parameter.controllerName
        String action = parameter.actionName

        final IEnumOption[] options = enumOptions.options
        final int im = options.size()

        int i = 0
        for (i; i < im;) {
            IEnumOption option = options[i++]
            parameter.params.put(enumOptions.paramKey, option.key)
            img = option.asset ? parameter.applicationTagLib.img(file: option.asset, width: 20, style: 'padding: .5em 0em;') : null
            if (option.isSection()) {
                menu.menuOptionSection(blockLog.topElement, img, option.value)
            } else {
                String url = parameter.urlMapped(controller, action, parameter.params as Map)
                menu.menuOption(blockLog.topElement, img, option.value, url)
            }
        }
        parameter.params.remove(enumOptions.paramKey)
        blockLog.restorePosition()
        splitMenuEnd()
        logExitBlock('visitMenuOptions')
    }

    // --- Accordion ---

    private int accordionItemIndex = 0
    private IHTMLElement currentAccordionElement = null

    @Override
    void visitBlockAccordion() {
        logEnterBlock('visitBlockAccordion')
        blockLog.savePosition()
        blockLog.topElement.setTaackTag(TaackTag.ACCORDION)
        currentAccordionElement = block.accordion(blockLog.topElement)
        blockLog.topElement = currentAccordionElement
        accordionItemIndex = 0
    }

    @Override
    void visitBlockAccordionEnd() {
        logExitBlock('visitBlockAccordionEnd')
        currentAccordionElement = null
        blockLog.restorePosition()
    }

    @Override
    void visitBlockAccordionItem(String i18n, boolean openByDefault) {
        logEnterBlock('visitBlockAccordionItem ' + i18n)
        blockLog.savePosition()
        blockLog.topElement.setTaackTag(TaackTag.ACCORDION_ITEM)
        blockLog.topElement = block.accordionItem(currentAccordionElement, i18n, accordionItemIndex++, openByDefault)
    }

    @Override
    void visitBlockAccordionItemEnd() {
        logExitBlock('visitBlockAccordionItemEnd')
        blockLog.restorePosition()
    }

    // --- Card ---

    private IHTMLElement cardHeaderElement = null
    private IHTMLElement cardDivElement = null

    @Override
    void visitBlockCard(String title, boolean hasMenu) {
        logEnterBlock('visitBlockCard ' + title)
        blockLog.savePosition()
        blockLog.topElement.setTaackTag(TaackTag.CARD)
        cardHeaderElement = block.cardStart(blockLog.topElement, title, hasMenu)
        // cardStart adds the card div as last child of topElement
        cardDivElement = blockLog.topElement.children.last()
        blockLog.topElement = cardHeaderElement
    }

    @Override
    void visitBlockCardBody() {
        blockLog.logStayBlock('visitBlockCardBody')
        blockLog.topElement = block.cardBody(cardDivElement)
    }

    @Override
    void visitBlockCardEnd() {
        logExitBlock('visitBlockCardEnd')
        cardHeaderElement = null
        cardDivElement = null
        blockLog.restorePosition()
    }

    // --- ScrollPanel ---

    @Override
    void visitBlockScrollPanel(String maxHeight) {
        logEnterBlock('visitBlockScrollPanel')
        blockLog.savePosition()
        blockLog.topElement.setTaackTag(TaackTag.SCROLL_PANEL)
        blockLog.topElement = block.scrollPanel(blockLog.topElement, maxHeight)
    }

    @Override
    void visitBlockScrollPanelEnd() {
        logExitBlock('visitBlockScrollPanelEnd')
        blockLog.restorePosition()
    }

    @Override
    void getOutput(OutputStream out) {
        blockLog.topElement.getOutput(out)
    }
}
