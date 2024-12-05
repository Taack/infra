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
import taack.ui.dsl.menu.MenuSpec
import taack.ui.dsl.table.IUiTableVisitor
import taack.ui.dump.common.BlockLog
import taack.ui.dump.html.block.*
import taack.ui.dump.html.element.HTMLDiv
import taack.ui.dump.html.element.HTMLTxtContent
import taack.ui.dump.html.element.IHTMLElement
import taack.ui.dump.html.element.TaackTag
import taack.ui.dump.html.layout.HTMLEmpty
import taack.ui.dump.html.menu.BootstrapMenu

@CompileStatic
final class RawHtmlBlockDump implements IUiBlockVisitor {
    private String currentAjaxBlockId = null
    final String modalId
    String futurCurrentAjaxBlockId
    String theCurrentExplicitAjaxBlockId

    boolean isModal = false
    boolean isRefreshing = false
    int poll = 0

    private int tabOccurrence = 0

    final BootstrapBlock block
    final Parameter parameter

    private final BlockLog blockLog

    RawHtmlBlockDump(final Parameter parameter) {
        this.parameter = parameter
        this.modalId = modalId
        if (parameter.params.boolean('refresh'))
            isRefreshing = true
        blockLog = new BlockLog(parameter.uiThemeService.themeSelector)
        block = new BootstrapBlock(blockLog)
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

        blockLog.simpleLog("doRenderElement0 :> $id")
        if ((!id && (!parameter.isAjaxRendering && !isModal) || theCurrentExplicitAjaxBlockId != null)) {
            blockLog.simpleLog("doRenderElement1 return true, because NOT AJAX OR MODAL")
            return true
        } else if (!id && !parameter.ajaxBlockId) {
            blockLog.simpleLog("doRenderElement2 return isModal($isModal)")
            return isModal
        }

        if (parameter.isAjaxRendering && currentAjaxBlockId == null) {
            currentAjaxBlockId = parameter.ajaxBlockId
        }

        if (parameter.targetAjaxBlockId) {
            currentAjaxBlockId = parameter.targetAjaxBlockId
        }

        blockLog.simpleLog("doRenderElement3 :> currentAjaxBlockId = ${currentAjaxBlockId}, targetAjaxBlockId = ${parameter.targetAjaxBlockId}, ajaxBlockId = ${parameter.ajaxBlockId}")

        boolean doRender = !parameter.isAjaxRendering || (currentAjaxBlockId == id && (isModal || isRefreshing)) || (isModal && !parameter.isRefresh) || parameter.targetAjaxBlockId //|| (parameter.isAjaxRendering && currentAjaxBlockId == parameter.ajaxBlockId)
        blockLog.simpleLog("doRenderElement4 => doRender = $doRender")
        return doRender
    }

    @Override
    void visitBlock() {
        blockLog.enterBlock('visitBlock')
        blockLog.topElement = block.block(blockLog.topElement, "${parameter.applicationTagLib.controllerName}-${parameter.applicationTagLib.actionName}")
    }

    @Override
    void visitBlockEnd() {
        blockLog.exitBlock('visitBlockEnd')
        blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.BLOCK)
    }

    @Override
    void visitBlockHeader() {
        blockLog.enterBlock('visitBlockHeader')
        blockLog.topElement.setTaackTag(TaackTag.MENU_BLOCK)
        blockLog.topElement = block.blockHeader(blockLog.topElement)
    }

    @Override
    void visitBlockHeaderEnd() {
        blockLog.exitBlock('visitBlockHeaderEnd')
        blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.MENU_BLOCK)
    }

    @Override
    void visitCol(final BlockSpec.Width width) {
        blockLog.enterBlock('visitCol')
        blockLog.topElement.setTaackTag(TaackTag.COL)
        blockLog.topElement = block.col(blockLog.topElement, width)
    }

    @Override
    void visitColEnd() {
        blockLog.exitBlock('visitColEnd')
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
        blockLog.enterBlock('visitAjaxBlock id: ' + id + " parameter.isAjaxRendering: " + parameter.isAjaxRendering + " parameter.ajaxBlockId: " + parameter.ajaxBlockId)
        if (!id) id = parameter.ajaxBlockId
        blockLog.topElement.setTaackTag(TaackTag.AJAX_BLOCK)
        // if many blocks in the same response, only redraw current block
        // further the first block must be in ajaxMode until current block ends
        boolean doAjaxRendering = (parameter.isRefresh && isModal || !isModal) && parameter.isAjaxRendering && (id == theCurrentExplicitAjaxBlockId || id == currentAjaxBlockId)
        if (!doAjaxRendering && parameter.targetAjaxBlockId) {
            id = parameter.targetAjaxBlockId
            doAjaxRendering = true
        }
        blockLog.topElement = block.blockAjax(blockLog.topElement, id, doAjaxRendering)
    }

    @Override
    void visitAjaxBlockEnd() {
        blockLog.exitBlock('visitAjaxBlockEnd')
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
        blockLog.enterBlock('visitCloseModalAndUpdateBlock')
        blockLog.topElement = new HTMLAjaxCloseModal()
    }

    @Override
    void visitCloseModalAndUpdateBlockEnd() {
        blockLog.exitBlock('visitCloseModalAndUpdateBlockEnd')
    }

    @Override
    void visitHtmlBlock(String html, Style style) {
        blockLog.stayBlock('visitHtmlBlock')
        blockLog.topElement.addChildren(
                new HTMLDiv().builder.addClasses(style?.cssClassesString).putAttribute("style", style?.cssStyleString).addChildren(
                        new HTMLTxtContent(html)
                ).build()
        )
    }

    @Override
    void visitPoll(int millis, MethodClosure polledAction) {
        blockLog.enterBlock('visitPoll ' + millis + ' ms ' + polledAction.toString())
        poll = millis
        HTMLAjaxPoll poll = new HTMLAjaxPoll(millis, parameter.urlMapped(polledAction))
        blockLog.topElement.addChildren(poll)
        blockLog.topElement.setTaackTag(TaackTag.POLL)

    }

    @Override
    void visitPollEnd() {
        blockLog.enterBlock('visitPollEnd')
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
        filterVisitor.addHiddenInputs()
        visitColEnd()
    }

//    @Override
//    void visitChart(final UiChartSpecifier chartSpecifier) {
//        blockLog.stayBlock('visitChart')
//        ByteArrayOutputStream out = new ByteArrayOutputStream(4096)
//        chartSpecifier.visitChart(new RawHtmlChartDump(out, "ajaxBlockId"))
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
        diagramSpecifier.visitDiagram(new RawHtmlDiagramDump(new ByteArrayOutputStream(4096), blockLog), UiDiagramSpecifier.DiagramBase.SVG)
        filterVisitor.addHiddenInputs()
        visitColEnd()
    }

    @Override
    void visitDiagram(final UiDiagramSpecifier diagramSpecifier) {
        blockLog.stayBlock('visitDiagram')
        diagramSpecifier.visitDiagram(new RawHtmlDiagramDump(new ByteArrayOutputStream(4096), blockLog), UiDiagramSpecifier.DiagramBase.SVG)
    }

    @Override
    void visitCloseModal(final String id, final String value, FieldInfo[] fields = null) {
        blockLog.stayBlock('visitCloseModal')
        blockLog.topElement.addChildren(new HTMLAjaxCloseLastModal(id, value))
        for (FieldInfo fi : fields) {
            if (fi.value) {
                if (parameter.nf && fi.value instanceof Number)
                    blockLog.topElement.addChildren(new HTMLFieldInfo(fi.fieldName, parameter.nf.format(fi.value)))
                else {
                    blockLog.topElement.addChildren(new HTMLFieldInfo(fi.fieldName, fi.value.toString()))
                    if (GormEntity.isAssignableFrom(fi.fieldConstraint.field.type)) {
                        blockLog.topElement.addChildren(new HTMLFieldInfo(fi.fieldName + 'Id', (fi.value as GormEntity).ident().toString()))
                    }
                }
            }
        }
    }

    @Override
    void visitBlockTab(final String i18n) {
        blockLog.enterBlock('visitBlockTab')
        currentTabNames << i18n
        blockLog.topElement.setTaackTag(TaackTag.TAB)
        blockLog.topElement = block.tab(blockLog.topElement, tabOccurrence++)
    }

    @Override
    void visitBlockTabEnd() {
        blockLog.exitBlock('visitBlockTabEnd')
        blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.TAB)
    }

    private List<String> currentTabNames = []
    private IHTMLElement oldParent = null

    @Override
    void visitBlockTabs() {
        blockLog.enterBlock('visitBlockTabs')
        oldParent = blockLog.topElement
        oldParent.setTaackTag(TaackTag.TABS)
        blockLog.topElement = new HTMLEmpty()
    }

    @Override
    void visitBlockTabsEnd() {
        blockLog.exitBlock('visitBlockTabsEnd')
        IHTMLElement tabsContent = blockLog.topElement
        blockLog.topElement = block.tabs(oldParent, currentTabNames)
        blockLog.topElement.addChildren(tabsContent)
        blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.TABS)
    }

    @Override
    void visitCustom(final String html, Style style) {
        blockLog.stayBlock('visitCustom')
        visitHtmlBlock(html, style)
    }

    @Override
    void visitModal() {
        blockLog.enterBlock('visitModal modalId:' + modalId + ' isModalRefresh:' + isRefreshing)
        isModal = true
        HTMLAjaxModal modal = new HTMLAjaxModal(isRefreshing)
        blockLog.topElement.addChildren(modal)
        blockLog.topElement.setTaackTag(TaackTag.MODAL)
        blockLog.topElement = modal
    }

    @Override
    void visitModalEnd() {
        blockLog.exitBlock('visitModalEnd')
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
        blockLog.enterBlock('visitRow')
        blockLog.topElement.setTaackTag(TaackTag.ROW)
        blockLog.topElement = block.row(blockLog.topElement)
    }

    @Override
    void visitRowEnd() {
        blockLog.exitBlock('visitRowEnd')
        blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.ROW)
    }

    BootstrapMenu menu

    @Override
    void visitMenuLabel(String i18n, boolean hasClosure) {
        if (hasClosure) blockLog.enterBlock('visitMenuLabel ' + i18n)
        else blockLog.stayBlock('visitMenuLabel ' + i18n)
        blockLog.topElement.setTaackTag(TaackTag.LABEL)

        blockLog.topElement = menu.label(blockLog.topElement, i18n, hasClosure)
    }

    @Override
    void visitMenuLabelEnd() {
        blockLog.exitBlock('visitMenuLabelEnd')
        blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.LABEL)
    }

    @Override
    void visitMenuStart(MenuSpec.MenuMode menuMode, String ajaxBlockId) {
        blockLog.enterBlock('visitMenuStart futurCurrentAjaxBlockId: ' + ajaxBlockId)
        futurCurrentAjaxBlockId = ajaxBlockId
        blockLog.topElement.setTaackTag(TaackTag.MENU)
        menu = new BootstrapMenu(blockLog)
        blockLog.topElement = menu.menuStart(blockLog.topElement)
    }

    @Override
    void visitMenuStartEnd() {
        blockLog.exitBlock('visitMenuStartEnd')
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
//        if (futurCurrentAjaxBlockId) {
//            params ?= [:]
//            params.put('targetAjaxBlockId', futurCurrentAjaxBlockId)
//        }
        Map cp = parameter.params
        if (params) {
            if (cp.containsKey('lang') && !params.containsKey('lang')) cp.remove('lang')
            if (cp.containsKey('action')) cp.remove('action')
            if (cp.containsKey('controller')) cp.remove('controller')
        }
        blockLog.topElement = menu.menu(blockLog.topElement, i18n, futurCurrentAjaxBlockId != null && !futurCurrentAjaxBlockId.empty, parameter.urlMapped(controller, action, params), controller == parameter.controllerName && action == parameter.actionName && (!params || params.equals(cp)))
    }

    @Override
    void visitMenuSection(String i18n, MenuSpec.MenuPosition position) {
        blockLog.enterBlock('visitMenuSection ' + i18n)
        menu.section(blockLog.topElement, i18n)
    }

    @Override
    void visitMenuSectionEnd() {
        blockLog.exitBlock('visitMenuSectionEnd')

    }

    @Override
    void visitSubMenuIcon(String i18n, ActionIcon actionIcon, String controller, String action, Map<String, ?> params, boolean isModal = false) {
        i18n ?= parameter.trField(controller, action, params?.containsKey('id'))
        blockLog.stayBlock('visitSubMenuIcon ' + i18n)
        if (!blockLog.topElement.testParentTaackTag(TaackTag.MENU_SPLIT)) {
            splitMenu()
        }
        menu.menuIcon(blockLog.topElement, actionIcon.getHtml(i18n, 24), parameter.urlMapped(controller, action, params, isModal), isModal)
    }

    @Override
    void visitMenuSelect(String paramName, IEnumOptions enumOptions, Map<String, ?> params) {
        blockLog.enterBlock('visitMenuSelect')
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
        blockLog.enterBlock('visitMenuSearch')
        splitMenu()
        menu.menuSearch(blockLog.topElement, q?.replace('"', "&quot;"), parameter.urlMapped(Utils.getControllerName(action), action.method))
        splitMenuEnd()
        blockLog.exitBlock('visitMenuSearch')
    }

    @Override
    void visitMenuOptions(IEnumOptions enumOptions) {
        blockLog.enterBlock('visitMenuOptions')
        splitMenu()
        String selectedOptionKey = parameter.params[enumOptions.paramKey]

        IEnumOption currentOption = selectedOptionKey ? (enumOptions.options.find { it.key == selectedOptionKey }) : enumOptions.currents?.first() as IEnumOption
        String selectedOptionValue = currentOption ? currentOption.value : selectedOptionKey
        String img = currentOption && currentOption.asset? parameter.applicationTagLib.img(file: currentOption.asset, width: 20, style: "padding: .5em 0em;") : ''

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
            img = option.asset ? parameter.applicationTagLib.img(file: option.asset, width: 20, style: "padding: .5em 0em;") : null
            if (option.section) {
                menu.menuOptionSection(blockLog.topElement, img, option.value)
            } else {
                String url = parameter.urlMapped(controller, action, parameter.params as Map)
                menu.menuOption(blockLog.topElement, img, option.value, url)
            }
        }
        blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.MENU_OPTION)
        splitMenuEnd()
        blockLog.exitBlock('visitMenuOptions')
    }

    @Override
    String getOutput() {
        blockLog.topElement.output
    }
}
