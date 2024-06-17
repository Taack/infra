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
import taack.ui.dsl.helper.Utils
import taack.ui.dsl.menu.MenuSpec
import taack.ui.dump.common.BlockLog
import taack.ui.dump.html.block.*
import taack.ui.dump.html.element.HTMLDiv
import taack.ui.dump.html.element.HTMLTxtContent
import taack.ui.dump.html.element.IHTMLElement
import taack.ui.dump.html.element.TaackTag
import taack.ui.dump.html.layout.HTMLEmpty
import taack.ui.dump.html.menu.BootstrapMenu
import taack.ui.dump.html.theme.ThemeSelector

@CompileStatic
final class RawHtmlBlockDump implements IUiBlockVisitor {
    private String id
    final String modalId

    private String ajaxBlockId = null
    boolean isModal = false
    boolean isModalRefresh = false

    private int tabOccurrence = 0

    final BootstrapBlock block
    final Parameter parameter

    private final BlockLog blockLog

    RawHtmlBlockDump(final Parameter parameter, final String modalId = null) {
        if (modalId) isModal = true
        this.parameter = parameter
        this.modalId = modalId
        if (parameter.params.boolean('refresh'))
            isModalRefresh = true
        blockLog = new BlockLog(parameter.uiThemeService.themeSelector)
        block = new BootstrapBlock(blockLog)
//        menu = new BootstrapMenu(blockLog)
        blockLog.topElement = new HTMLEmpty()
        blockLog.topElement.setTaackTag(TaackTag.BLOCK)
    }

    @Override
    void visitBlock() {
        blockLog.enterBlock('visitBlock')
        if (!parameter.isAjaxRendering || isModal) {
            blockLog.topElement = block.block(blockLog.topElement, "${parameter.applicationTagLib.controllerName}-${parameter.applicationTagLib.actionName}")
        }
    }

    @Override
    void visitBlockEnd() {
        blockLog.exitBlock('visitBlockEnd')
        if (!parameter.isAjaxRendering || isModal) {
            blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.BLOCK)
        }
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

    @Override
    void visitAjaxBlock(final String id) {
        blockLog.enterBlock('visitAjaxBlock id: ' + id)
        if (!parameter.isAjaxRendering || isModal) {
            ajaxBlockId = id
        }
        if (isModalRefresh) {
            blockLog.topElement.setTaackTag(TaackTag.AJAX_BLOCK)
            blockLog.topElement = block.blockAjax(blockLog.topElement, id)
        }
    }

    @Override
    void visitAjaxBlockEnd() {
        blockLog.exitBlock('visitAjaxBlockEnd ajaxBlockId: ' + ajaxBlockId)
        if (!parameter.isAjaxRendering || isModal) ajaxBlockId = null
        if (isModalRefresh) blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.AJAX_BLOCK)
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
                new HTMLDiv().builder.addClasses(style?.cssClassesString).addChildren(
                        new HTMLTxtContent(html)
                ).build()
        )
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
        filterSpecifier.visitFilter(new RawHtmlFilterDump(blockLog, id, parameter))
        visitColEnd()
        visitCol(BlockSpec.Width.THREE_QUARTER)
        tableSpecifier.visitTable(new RawHtmlTableDump(blockLog, id, parameter))
        visitColEnd()
    }

    @Override
    void visitChart(final UiChartSpecifier chartSpecifier) {
        blockLog.stayBlock('visitChart')
        ByteArrayOutputStream out = new ByteArrayOutputStream(4096)
        chartSpecifier.visitChart(new RawHtmlChartDump(out, ajaxBlockId))
    }

    @Override
    void visitDiagramFilter(final UiDiagramSpecifier diagramSpecifier, final UiFilterSpecifier filterSpecifier) {
        blockLog.stayBlock('visitDiagramFilter')
        filterSpecifier.visitFilter(new RawHtmlFilterDump(blockLog, id, parameter))
    }

    @Override
    void visitDiagram(final UiDiagramSpecifier diagramSpecifier) {
        blockLog.stayBlock('visitDiagram')
        ByteArrayOutputStream out = new ByteArrayOutputStream(4096)
        diagramSpecifier.visitDiagram(new RawHtmlDiagramDump(out, ajaxBlockId, BlockSpec.Width.MAX), UiDiagramSpecifier.DiagramBase.SVG)
    }

    @Override
    void visitCloseModal(final String id, final String value, FieldInfo[] fields = null) {
        blockLog.stayBlock('visitCloseModal')
        blockLog.topElement.addChildren(new HTMLAjaxCloseLastModal(id, value))
        for (FieldInfo fi : fields) {
            if (fi.value) {
                if (parameter.nf && fi.value instanceof Number)
                    blockLog.topElement.addChildren(new HTMLFieldInfo(fi.fieldName, parameter.nf.format(fi.value)))
                else
                    blockLog.topElement.addChildren(new HTMLFieldInfo(fi.fieldName, fi.value.toString()))
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
        blockLog.enterBlock('visitModal ' + modalId)
        isModal = true
        HTMLAjaxModal modal = new HTMLAjaxModal(isModalRefresh)
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
    void visitMenuStart(MenuSpec.MenuMode menuMode) {
        blockLog.enterBlock('visitMenuStart')
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
        String i18n = parameter.trField(controller, action)
        visitLabeledSubMenu(i18n, controller, action, params)
    }


    @Override
    void visitSubMenu(String controller, String action, Map<String, ?> params) {
        blockLog.stayBlock('visitSubMenu')
        String i18n = parameter.trField(controller, action)
        visitLabeledSubMenu(i18n, controller, action, params)
    }

    private void visitLabeledSubMenu(String i18n, String controller, String action, Map<String, ?> params) {
        blockLog.stayBlock('visitLabeledSubMenu ' + i18n)
        blockLog.topElement = menu.menu(blockLog.topElement, i18n, parameter.isAjaxRendering, parameter.urlMapped(controller, action, params))
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
        i18n ?= parameter.trField(controller, action)
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
        String img = currentOption ? parameter.applicationTagLib.img(file: currentOption.asset, width: 20, style: "padding: .5em 0em;") : ''

        blockLog.topElement = menu.menuOptions(blockLog.topElement, img, selectedOptionValue)
        blockLog.topElement.setTaackTag(TaackTag.MENU_OPTION)

        String controller = parameter.params['controller'] as String
        String action = parameter.params['action'] as String

        final IEnumOption[] options = enumOptions.options
        final int im = options.size()

        int i = 0
        for (i; i < im;) {
            IEnumOption option = options[i++]
            parameter.params.put(enumOptions.paramKey, option.key)
            img = parameter.applicationTagLib.img(file: option.asset, width: 20, style: "padding: .5em 0em;")
            if (option.section) {
                menu.menuOptionSection(blockLog.topElement, img, option.value)
            } else {
                String url = parameter.urlMapped(controller, action, parameter.params as Map, false)
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
