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
    private String currentAjaxBlockId = null
    final String modalId
    String futurCurrentAjaxBlockId
    String theCurrentExplicitAjaxBlockId

    Map<String, byte[]> mailAttachment = [:]

    boolean isModal = false
    boolean isRefreshing = false
    boolean renderTab = false
    int poll = 0

    private boolean poke = false

    private int tabOccurrence = 0

    final BootstrapBlock block
    final Parameter parameter

    private final BlockLog blockLog
    
    void simpleLog(String toPrint) {
        blockLog.simpleLog(RawHtmlBlockDump.simpleName + '::' + toPrint)
    }

    void enterBlock(String toPrint) {
        blockLog.enterBlock(RawHtmlBlockDump.simpleName + '::' + toPrint)
    }

    void exitBlock(String toPrint) {
        blockLog.exitBlock(RawHtmlBlockDump.simpleName + '::' + toPrint)
    }

    RawHtmlBlockDump(final Parameter parameter) {
        this.parameter = parameter
        this.modalId = modalId
        if (parameter.params.boolean('refresh'))
            isRefreshing = true
        blockLog = new BlockLog(parameter.uiThemeService.themeSelector)
        block = new BootstrapBlock(blockLog, this.parameter)
//        menu = new BootstrapMenu(blockLog)
        blockLog.topElement = new HTMLEmpty()
        blockLog.topElement.setTaackTag(TaackTag.BLOCK)
    }

    /**
     * Check if a block DSL element has to be rendered. If a page contains multiple block, and user click on
     * a table to sort, only the table will be rendered when processing the DSL.
     *
     * * If the page is not ajax, everything is rendered
     * * else
     * * * If no explicit ajaxBlock, only current or target ajaxBlock is rendered
     * * * If explicit ajaxBlocks are specified (via {@link #setExplicitAjaxBlockId(String id) setAjaxBlockId}),
     *     only then will be rendered if in ajax mode (parameter.isAjaxRendering == true)
     *
     * @param id
     * @return
     */
    @Override
    boolean doRenderElement(String id = null) {
        // if many blocks in the same response, only redraw current block
        // further the first block must be in ajaxMode until current block ends
        if (parameter.target == Parameter.RenderingTarget.MAIL) return true
        if (poke) return true
        simpleLog("doRenderElement0 :> renderTab: $renderTab, id: $id, theCurrentExplicitAjaxBlockId: ${theCurrentExplicitAjaxBlockId}, isModal: ${isModal}, params: ${parameter.params}")

        if (!parameter.isAjaxRendering) {
            simpleLog('doRenderElement01: true')
            return true
        }
        if (renderTab && parameter.isAjaxRendering) {
            simpleLog('doRenderElement02: true')
           return true
        }

//        if (parameter.isAjaxRendering && theCurrentExplicitAjaxBlockId == parameter.ajaxBlockId) return true
//        else if (parameter.isAjaxRendering && parameter.ajaxBlockId && theCurrentExplicitAjaxBlockId != parameter.ajaxBlockId) return false

        if ((!id && (!parameter.isAjaxRendering && !isModal) || theCurrentExplicitAjaxBlockId != null)) {
            boolean ret = !parameter.tabId && !parameter.ajaxBlockId || parameter.ajaxBlockId == theCurrentExplicitAjaxBlockId
            simpleLog("doRenderElement1 return ${ret}, because NOT (AJAX OR MODAL) OR AJAX BUT ANOTHER id")
            return ret
        } else if (!id && !parameter.ajaxBlockId) {
            simpleLog("doRenderElement2 return isModal($isModal && $poke)")
            return isModal
        }

        if (parameter.isAjaxRendering && currentAjaxBlockId == null) {
            currentAjaxBlockId = parameter.ajaxBlockId
        }

        if (parameter.targetAjaxBlockId) {
            currentAjaxBlockId = parameter.targetAjaxBlockId
        }

        simpleLog("doRenderElement3 :> currentAjaxBlockId = ${currentAjaxBlockId}, targetAjaxBlockId = ${parameter.targetAjaxBlockId}, ajaxBlockId = ${parameter.ajaxBlockId}")

        boolean doRender = (currentAjaxBlockId == id && (isModal || isRefreshing)) || (!currentAjaxBlockId && isModal) //&& !parameter.isRefresh) // || parameter.targetAjaxBlockId //|| (parameter.isAjaxRendering && currentAjaxBlockId == parameter.ajaxBlockId)
        simpleLog("doRenderElement4 => doRender = $doRender && $poke")
        return doRender
    }

    @Override
    boolean doRenderLayoutElement() {
        simpleLog("doRenderLayoutElement ${parameter.isAjaxRendering}, ${parameter.ajaxBlockId} $currentAjaxBlockId ${parameter.targetAjaxBlockId}")
        return !parameter.isAjaxRendering
    }

    @Override
    void setRenderTab(boolean isRender) {
        renderTab = isRender || parameter.target == Parameter.RenderingTarget.MAIL
    }

    @Override
    void visitBlock() {
        enterBlock('visitBlock')
        blockLog.topElement = block.block(blockLog.topElement, "${parameter.applicationTagLib.controllerName}-${parameter.applicationTagLib.actionName}")
    }

    @Override
    void visitBlockEnd() {
        exitBlock('visitBlockEnd')
        blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.BLOCK)
    }

    @Override
    void visitBlockHeader() {
        enterBlock('visitBlockHeader')
        blockLog.topElement.setTaackTag(TaackTag.MENU_BLOCK)
        blockLog.topElement = block.blockHeader(blockLog.topElement)
    }

    @Override
    void visitBlockHeaderEnd() {
        exitBlock('visitBlockHeaderEnd')
        blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.MENU_BLOCK)
    }

    @Override
    void visitCol(final BlockSpec.Width width) {
        enterBlock('visitCol')
        blockLog.topElement.setTaackTag(TaackTag.COL)
        blockLog.topElement = block.col(blockLog.topElement, width)
    }

    @Override
    void visitColEnd() {
        exitBlock('visitColEnd')
        blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.COL)
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
        enterBlock('visitAjaxBlock id: ' + id + ' parameter.isAjaxRendering: ' + parameter.isAjaxRendering + ' parameter.ajaxBlockId: ' + parameter.ajaxBlockId)
        if (!id) id = parameter.ajaxBlockId
        blockLog.topElement.setTaackTag(TaackTag.AJAX_BLOCK)

        // doAjaxRendering == true : to ajax refresh the content of an existing block
        boolean doAjaxRendering = parameter.isAjaxRendering // must be in ajaxMode
        if ((id != theCurrentExplicitAjaxBlockId || isModal) && id != currentAjaxBlockId) {
            // if many blocks in the same response, only redraw current block
            doAjaxRendering = false
        }
        if (parameter.tabIndex != null && !parameter.ajaxBlockId) {
            // insert content to an empty tab, so not for the refresh of an existing block
            doAjaxRendering = false
        }
        if (isModal && !isRefreshing) {
            // Open a new modal (Not refreshing an existing modal), so not for the refresh of an existing block
            doAjaxRendering = false
        }
        if (!doAjaxRendering && parameter.targetAjaxBlockId) {
            // a highest priority interface to force the refresh of a target block
            id = parameter.targetAjaxBlockId
            doAjaxRendering = true
        }
        if (poke) doAjaxRendering = true
        blockLog.topElement = block.blockAjax(blockLog.topElement, id, doAjaxRendering)
    }

    @Override
    void visitAjaxBlockEnd() {
        exitBlock('visitAjaxBlockEnd')
        currentAjaxBlockId = null
        blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.AJAX_BLOCK)
    }

    @Override
    void visitForm(UiFormSpecifier formSpecifier) {
        blockLog.stayBlock('visitForm')
        formSpecifier.visitForm(new RawHtmlFormDump(blockLog, parameter))
    }


    @Override
    void visitShow(final UiShowSpecifier uiShowSpecifier) {
        blockLog.stayBlock('visitShow')
        ByteArrayOutputStream out = new ByteArrayOutputStream(4096)
        if (uiShowSpecifier) uiShowSpecifier.visitShow(new RawHtmlShowDump(id, out, parameter))
        blockLog.topElement.addChildren(new HTMLOutput(out))
    }

    @Override
    void visitCloseModalAndUpdateBlock() {
        enterBlock('visitCloseModalAndUpdateBlock')
        blockLog.topElement = new HTMLAjaxCloseModal()
    }

    @Override
    void visitCloseModalAndUpdateBlockEnd() {
        exitBlock('visitCloseModalAndUpdateBlockEnd')
    }

    @Override
    void visitHtmlBlock(String html, Style style) {
        blockLog.stayBlock('visitHtmlBlock')
        blockLog.topElement.addChildren(
                new HTMLDiv().builder.addClasses(style?.cssClassesString).putAttribute('style', style?.cssStyleString).addChildren(
                        new HTMLTxtContent(html)
                ).build()
        )
    }

    @Override
    void visitIframe(String url, String cssHeight) {
        blockLog.stayBlock('visitIframe')
        blockLog.topElement.addChildren(
                new HTMLIFrame(url, cssHeight)
        )
    }

    @Override
    void visitPoll(int millis, MethodClosure polledAction) {
        enterBlock('visitPoll ' + millis + ' ms ' + polledAction.toString())
        poll = millis
        HTMLAjaxPoll poll = new HTMLAjaxPoll(millis, parameter.urlMapped(polledAction))
        blockLog.topElement.addChildren(poll)
        blockLog.topElement.setTaackTag(TaackTag.POLL)

    }

    @Override
    void visitPollEnd() {
        enterBlock('visitPollEnd')
        blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.MODAL)
        poll = 0
    }

    @Override
    void visitTable(String id, final UiTableSpecifier tableSpecifier) {
        blockLog.stayBlock('visitTable ' + id)
        tableSpecifier.visitTableWithNoFilter(new RawHtmlTableDump(blockLog, id, parameter))
    }

    @Override
    void visitTableFilter(final String id,
                          final UiFilterSpecifier filterSpecifier,
                          final UiTableSpecifier tableSpecifier) {
        blockLog.stayBlock('visitTableFilter ' + id)
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
        blockLog.stayBlock('visitKanban ' + id)
        kanbanSpecifier.visitKanban(new RawHtmlKanbanDump(blockLog, id, parameter))
    }

    @Override
    void visitKanbanFilter(final String id,
                           final UiFilterSpecifier filterSpecifier,
                           final UiKanbanSpecifier kanbanSpecifier) {
        blockLog.stayBlock('visitKanbanFilter ' + id)
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
        blockLog.stayBlock('visitDiagramFilter')
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
        blockLog.stayBlock('visitDiagram')
        if (parameter.target == Parameter.RenderingTarget.MAIL) {
            RawHtmlDiagramDump diagramDump = new RawHtmlDiagramDump(new ByteArrayOutputStream(4096), blockLog, mailAttachment)
            diagramSpecifier.visitDiagram(diagramDump, UiDiagramSpecifier.DiagramBase.PNG)
        } else
            diagramSpecifier.visitDiagram(new RawHtmlDiagramDump(new ByteArrayOutputStream(4096), blockLog), UiDiagramSpecifier.DiagramBase.SVG)
    }

    @Override
    void visitCloseModal(final Map<String, String> idValueMap, FieldInfo[] fields = null) {
        blockLog.stayBlock('visitCloseModal')
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
        enterBlock('visitBlockTab')
        currentTabNames << i18n
        blockLog.topElement.setTaackTag(TaackTag.TAB)
        blockLog.topElement = block.tab(blockLog.topElement, tabOccurrence++)
    }

    @Override
    void visitBlockTabEnd() {
        exitBlock('visitBlockTabEnd')
        blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.TAB)
    }

    private List<String> currentTabNames = []
    private IHTMLElement oldParent = null

    @Override
    void visitBlockTabs() {
        enterBlock('visitBlockTabs')
        oldParent = blockLog.topElement
        oldParent.setTaackTag(TaackTag.TABS)
        blockLog.topElement = new HTMLEmpty()
    }

    @Override
    void visitBlockTabsEnd() {
        exitBlock('visitBlockTabsEnd')
        IHTMLElement tabsContent = blockLog.topElement
        Map<String, Object> p = parameter.params.sort()
        if (p) p.remove('tabIndex')
        blockLog.topElement = block.tabs(oldParent, currentTabNames, parameter.urlMapped(parameter.applicationTagLib.controllerName, parameter.applicationTagLib.actionName, parameter.beanId, p))
        blockLog.topElement.addChildren(tabsContent)
        blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.TABS)
    }

    @Override
    void visitBlockPoke(boolean update) {
        poke = update
    }

    @Override
    void visitBlockPokeEnd() {
        poke = false
    }

    @Override
    void visitCustom(final String html, Style style) {
        blockLog.stayBlock('visitCustom')
        visitHtmlBlock(html, style)
    }

    @Override
    void visitModal(boolean reloadWhenClose) {
        enterBlock('visitModal modalId:' + modalId + ' isModalRefresh:' + isRefreshing)
        isModal = true
        parameter.isModal = true
        HTMLAjaxModal modal = new HTMLAjaxModal(isRefreshing, reloadWhenClose)
        blockLog.topElement.addChildren(modal)
        blockLog.topElement.setTaackTag(TaackTag.MODAL)
        blockLog.topElement = modal
    }

    @Override
    void visitModalEnd() {
        exitBlock('visitModalEnd')
        isModal = false
        blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.MODAL)
    }

    @Override
    String getExplicitAjaxBlockId() {
        theCurrentExplicitAjaxBlockId
    }

    /**
     * Set explicit ajaxBlockId to for ajax mode and for render.
     * (See {@link #visitAjaxBlock}
     *
     * @param id
     */
    @Override
    void setExplicitAjaxBlockId(String id) {
        theCurrentExplicitAjaxBlockId = id
    }

    @Override
    Map getParameterMap() {
        parameter.applicationTagLib.params
    }

    @Override
    void visitRow() {
        enterBlock('visitRow')
        blockLog.topElement.setTaackTag(TaackTag.ROW)
        blockLog.topElement = block.row(blockLog.topElement)
    }

    @Override
    void visitRowEnd() {
        exitBlock('visitRowEnd')
        blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.ROW)
    }

    BootstrapMenu menu
    boolean insideMenuSection = false

    @Override
    void visitMenuLabel(String i18n, boolean hasClosure) {
        if (hasClosure) enterBlock('visitMenuLabel ' + i18n)
        else blockLog.stayBlock('visitMenuLabel ' + i18n)
        blockLog.topElement.setTaackTag(TaackTag.LABEL)

        blockLog.topElement = menu.label(blockLog.topElement, i18n, hasClosure)
    }

    @Override
    void visitMenuLabelEnd() {
        exitBlock('visitMenuLabelEnd')
        blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.LABEL)
    }

    @Override
    void visitMenuStart(MenuSpec.MenuMode menuMode, String ajaxBlockId) {
        enterBlock('visitMenuStart futurCurrentAjaxBlockId: ' + ajaxBlockId)
        futurCurrentAjaxBlockId = ajaxBlockId
        blockLog.topElement.setTaackTag(TaackTag.MENU)
        menu = new BootstrapMenu(parameter.target == Parameter.RenderingTarget.MAIL, blockLog)
        blockLog.topElement = menu.menuStart(blockLog.topElement)
    }

    @Override
    void visitMenuStartEnd() {
        exitBlock('visitMenuStartEnd')
        futurCurrentAjaxBlockId = null
        blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.MENU)
    }

    private void splitMenu() {
        if (!blockLog.topElement.testParentTaackTag(TaackTag.MENU_SPLIT)) {
            blockLog.stayBlock('splitMenu +++')
            blockLog.topElement = menu.splitMenuStart(blockLog.topElement.parent)
            blockLog.topElement.setTaackTag(TaackTag.MENU_SPLIT)
            blockLog.stayBlock('splitMenu ---')
        } else {
            blockLog.stayBlock('splitMenu -+-+-+-')
        }

    }

    private void splitMenuEnd() {
        if (blockLog.topElement.testParentTaackTag(TaackTag.MENU_SPLIT)) {
            blockLog.stayBlock('splitMenuEnd TRUE')
            blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.MENU_SPLIT)
        } else {
            blockLog.stayBlock('splitMenuEnd FALSE')
        }
    }

    @Override
    void visitMenu(String controller, String action, Map<String, ?> params) {
        blockLog.stayBlock('visitMenu')
        visitLabeledSubMenu(null, controller, action, params)
    }


    @Override
    void visitSubMenu(String controller, String action, Map<String, ?> params) {
        blockLog.stayBlock('visitSubMenu')
        visitLabeledSubMenu(null, controller, action, params)
    }

    void visitLabeledSubMenu(String i18n, String controller, String action, Map<String, ?> params) {
        i18n ?= parameter.trField(controller, action, params?.containsKey('id'))

        blockLog.stayBlock('visitLabeledSubMenu ' + i18n)
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
        blockLog.topElement = menu.menu(blockLog.topElement, i18n, futurCurrentAjaxBlockId?.size() > 0, futurCurrentAjaxBlockId, parameter.urlMapped(controller, action, params), controller == parameter.controllerName && action == parameter.actionName && (!params || params.equals(cp)), insideMenuSection)
    }

    @Override
    void visitMenuSection(String i18n, MenuSpec.MenuPosition position) {
        enterBlock('visitMenuSection ' + i18n)
        insideMenuSection = true
        menu.section(blockLog.topElement, i18n)
    }

    @Override
    void visitMenuSectionEnd() {
        exitBlock('visitMenuSectionEnd')
        insideMenuSection = false
    }

    @Override
    void visitSubMenuIcon(String i18n, ActionIcon actionIcon, String controller, String action, Map<String, ?> params, boolean isModal = false) {
        i18n ?= parameter.trField(controller, action, params?.containsKey('id'))
        blockLog.stayBlock('visitSubMenuIcon ' + i18n)
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
        enterBlock('visitMenuIconWithClosure ' + i18n)
        blockLog.topElement.setTaackTag(TaackTag.MENU_COL)

        blockLog.topElement = menu.label(blockLog.topElement, actionIcon.getHtml(i18n, 24), true).builder.build()
    }

    @Override
    void visitMenuIconWithClosureEnd(Style style) {
        exitBlock('visitMenuIconWithClosureEnd')
        blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.MENU_COL)
        if (Style) blockLog.topElement.children.last().setStyleDescriptor(style)
    }

    @Override
    void visitMenuSelect(String paramName, IEnumOptions enumOptions, Map<String, ?> params) {
        enterBlock('visitMenuSelect')
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

        enterBlock('visitMenuSearch')
        splitMenu()
        menu.menuSearch(blockLog.topElement, i18n, q?.replace('"', '&quot;'), parameter.urlMapped(Utils.getControllerName(action), action.method))
        splitMenuEnd()
        exitBlock('visitMenuSearch')
    }

    @Override
    void visitMenuOptions(IEnumOptions enumOptions) {
        enterBlock('visitMenuOptions')
        splitMenu()
        String selectedOptionKey = parameter.params[enumOptions.paramKey]

        IEnumOption currentOption = selectedOptionKey ? (enumOptions.options.find { it.key == selectedOptionKey }) : enumOptions.currents?.first() as IEnumOption
        String selectedOptionValue = currentOption ? currentOption.value : selectedOptionKey
        String img = currentOption && currentOption.asset? parameter.applicationTagLib.img(file: currentOption.asset, width: 20, style: 'padding: .5em 0em;') : ''

        blockLog.topElement = menu.menuOptions(blockLog.topElement, img, selectedOptionValue)
        blockLog.topElement.setTaackTag(TaackTag.MENU_OPTION)

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
        blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.MENU_OPTION)
        splitMenuEnd()
        exitBlock('visitMenuOptions')
    }

    @Override
    void getOutput(OutputStream out) {
        blockLog.topElement.getOutput(out)
    }
}
