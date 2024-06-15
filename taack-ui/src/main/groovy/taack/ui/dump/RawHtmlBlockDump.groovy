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

    final private Random random = new Random(System.currentTimeMillis())
    private int tabOccurrence = 0

    final BootstrapBlock block
    final BootstrapMenu menu
    final Parameter parameter

    private IHTMLElement topElement

    RawHtmlBlockDump(final Parameter parameter, final String modalId = null) {
        if (modalId) isModal = true
        this.parameter = parameter
        this.modalId = modalId
        if (parameter.params.boolean('refresh'))
            isModalRefresh = true
        ThemeSelector ts = parameter.uiThemeService.themeSelector
        block = new BootstrapBlock(ts.themeMode, ts.themeSize)
        menu = new BootstrapMenu(ts.themeMode, ts.themeSize)
        topElement = new HTMLEmpty()
        topElement.setTaackTag(TaackTag.BLOCK)
    }

    String indent = '   '
    int occ = 0
    boolean debug = true
    void enterBlock(String method) {
        if (debug) println(indent*occ++ + method + ' +++ ' + topElement)
    }

    void stayBlock(String method) {
        if (debug) {
            if (occ <= 0) println "OCC <= 0 !!! occ == $occ"
            println(indent*occ + method + ' === ' + topElement)
        }
    }

    void exitBlock(String method) {
        if (debug) {
            if (occ <= 0) println "OCC <= 0 !!! occ == $occ"
            println(indent*--occ + method + ' --- ' + topElement)
        }
    }

    @Override
    void visitBlock() {
        enterBlock('visitBlock')
        if (!parameter.isAjaxRendering || isModal) {
            topElement = block.block(topElement, "${parameter.applicationTagLib.controllerName}-${parameter.applicationTagLib.actionName}")
        }
    }

    @Override
    void visitBlockEnd() {
        exitBlock('visitBlockEnd')
        if (!parameter.isAjaxRendering || isModal) {
            topElement = topElement.toParentTaackTag(TaackTag.BLOCK)
        }
    }

    @Override
    void visitBlockHeader() {
        enterBlock('visitBlockHeader')
        topElement.setTaackTag(TaackTag.MENU_BLOCK)
        topElement = block.blockHeader(topElement)
    }

    @Override
    void visitBlockHeaderEnd() {
        exitBlock('visitBlockHeaderEnd')
        topElement = topElement.toParentTaackTag(TaackTag.MENU_BLOCK)
    }

    @Override
    void visitCol(final BlockSpec.Width width) {
        enterBlock('visitCol')
        topElement.setTaackTag(TaackTag.COL)
        topElement = block.col(topElement, width)
    }

    @Override
    void visitColEnd() {
        exitBlock('visitColEnd')
        topElement = topElement.toParentTaackTag(TaackTag.COL)
    }

    @Override
    void visitAjaxBlock(final String id) {
        enterBlock('visitAjaxBlock ' + id)
        if (!parameter.isAjaxRendering || isModal) {
            ajaxBlockId = id
        }
        if (isModalRefresh) {
            topElement.setTaackTag(TaackTag.AJAX_BLOCK)
            topElement = block.blockAjax(topElement, id)
        }
    }

    @Override
    void visitAjaxBlockEnd() {
        exitBlock('visitAjaxBlockEnd ' + ajaxBlockId)
        if (!parameter.isAjaxRendering || isModal) ajaxBlockId = null
        if (isModalRefresh) topElement = topElement.toParentTaackTag(TaackTag.AJAX_BLOCK)
    }

    @Override
    void visitForm(UiFormSpecifier formSpecifier) {
        stayBlock('visitForm')
        formSpecifier.visitForm(new RawHtmlFormDump(topElement, parameter))
    }


    @Override
    void visitShow(final UiShowSpecifier uiShowSpecifier) {
        stayBlock('visitShow')
        ByteArrayOutputStream out = new ByteArrayOutputStream(4096)
        if (uiShowSpecifier) uiShowSpecifier.visitShow(new RawHtmlShowDump(id, out, parameter))
    }

    @Override
    void visitCloseModalAndUpdateBlock() {
        enterBlock('visitCloseModalAndUpdateBlock')
        topElement = new HTMLAjaxCloseModal()
    }

    @Override
    void visitCloseModalAndUpdateBlockEnd() {
        exitBlock('visitCloseModalAndUpdateBlockEnd')
    }

    @Override
    void visitHtmlBlock(String html, Style style) {
        stayBlock('visitHtmlBlock')
        topElement.addChildren(
                new HTMLDiv().builder.addClasses(style?.cssClassesString).addChildren(
                        new HTMLTxtContent(html)
                ).build()
        )
    }

    @Override
    void visitTable(String id, final UiTableSpecifier tableSpecifier) {
        stayBlock('visitTable ' + id)
        tableSpecifier.visitTableWithNoFilter(new RawHtmlTableDump(topElement, id, parameter))
    }

    @Override
    void visitTableFilter(final String id,
                          final UiFilterSpecifier filterSpecifier,
                          final UiTableSpecifier tableSpecifier) {
        stayBlock('visitTableFilter ' + id)
        visitCol(BlockSpec.Width.QUARTER)
        filterSpecifier.visitFilter(new RawHtmlFilterDump(topElement, id, parameter))
        visitColEnd()
        visitCol(BlockSpec.Width.THREE_QUARTER)
        tableSpecifier.visitTable(new RawHtmlTableDump(topElement, id, parameter))
        visitColEnd()
    }

    @Override
    void visitChart(final UiChartSpecifier chartSpecifier) {
        stayBlock('visitChart')
        ByteArrayOutputStream out = new ByteArrayOutputStream(4096)
        chartSpecifier.visitChart(new RawHtmlChartDump(out, ajaxBlockId))
    }

    @Override
    void visitDiagramFilter(final UiDiagramSpecifier diagramSpecifier, final UiFilterSpecifier filterSpecifier) {
        stayBlock('visitDiagramFilter')
        filterSpecifier.visitFilter(new RawHtmlFilterDump(topElement, id, parameter))
    }

    @Override
    void visitDiagram(final UiDiagramSpecifier diagramSpecifier) {
        stayBlock('visitDiagram')
        ByteArrayOutputStream out = new ByteArrayOutputStream(4096)
        diagramSpecifier.visitDiagram(new RawHtmlDiagramDump(out, ajaxBlockId, BlockSpec.Width.MAX), UiDiagramSpecifier.DiagramBase.SVG)
    }

    @Override
    void visitCloseModal(final String id, final String value, FieldInfo[] fields = null) {
        stayBlock('visitCloseModal')
        topElement.addChildren(new HTMLAjaxCloseLastModal(id, value))
        for (FieldInfo fi : fields) {
            if (fi.value) {
                if (parameter.nf && fi.value instanceof Number)
                    topElement.addChildren(new HTMLFieldInfo(fi.fieldName, parameter.nf.format(fi.value)))
                else
                    topElement.addChildren(new HTMLFieldInfo(fi.fieldName, fi.value.toString()))
            }
        }
    }

    @Override
    void visitBlockTab(final String i18n) {
        enterBlock('visitBlockTab')
        currentTabNames << i18n
        topElement.setTaackTag(TaackTag.TAB)
        topElement = block.tab(topElement, ++tabOccurrence)
    }

    @Override
    void visitBlockTabEnd() {
        exitBlock('visitBlockTabEnd')
        topElement = topElement.toParentTaackTag(TaackTag.TAB)
    }

    private List<String> currentTabNames = []
    private IHTMLElement oldParent = null
    private BlockSpec.Width blockTabWidth

    @Override
    void visitBlockTabs() {
        enterBlock('visitBlockTabs')
        oldParent = topElement
        topElement = new HTMLEmpty()
    }

    @Override
    void visitBlockTabsEnd() {
        exitBlock('visitBlockTabsEnd')
        IHTMLElement tabsContent = topElement
        topElement = block.tabs(oldParent, random.nextInt(), currentTabNames, blockTabWidth)
        topElement.addChildren(tabsContent)
        topElement = topElement.toParentTaackTag(TaackTag.TABS)
    }

    @Override
    void visitCustom(final String html, Style style) {
        stayBlock('visitCustom')
        visitHtmlBlock(html, style)
    }

    @Override
    void visitModal() {
        enterBlock('visitModal ' + modalId)
        isModal = true
        topElement.setTaackTag(TaackTag.MODAL)
        topElement.addChildren(new HTMLAjaxModal(isModalRefresh))
        topElement = topElement.children.last()
    }

    @Override
    void visitModalEnd() {
        exitBlock('visitModalEnd')
        isModal = false
        topElement = topElement.toParentTaackTag(TaackTag.MODAL)
    }

    @Override
    Map getParameterMap() {
        parameter.applicationTagLib.params
    }

    @Override
    void visitRow() {
        enterBlock('visitRow')
        topElement.setTaackTag(TaackTag.ROW)
        topElement = block.row(topElement)
    }

    @Override
    void visitRowEnd() {
        exitBlock('visitRowEnd')
        topElement = topElement.toParentTaackTag(TaackTag.ROW)
    }

    @Override
    void visitMenuLabel(String i18n, boolean hasClosure) {
        if (hasClosure) enterBlock('visitMenuLabel ' + i18n)
        else stayBlock('visitMenuLabel ' + i18n)
        topElement.setTaackTag(TaackTag.LABEL)
        topElement = menu.label(topElement, i18n, hasClosure)
    }

    @Override
    void visitMenuLabelEnd() {
        exitBlock('visitMenuLabelEnd')
        topElement = topElement.toParentTaackTag(TaackTag.LABEL)
    }

    @Override
    void visitMenuStart(MenuSpec.MenuMode menuMode) {
        enterBlock('visitMenuStart')
        topElement.setTaackTag(TaackTag.MENU)
        topElement = menu.menuStart(topElement)
    }

    @Override
    void visitMenuStartEnd() {
        exitBlock('visitMenuStartEnd')
        topElement = topElement.toParentTaackTag(TaackTag.MENU)
    }

    private void splitMenu() {
        if (!topElement.testParentTaackTag(TaackTag.MENU_SPLIT)) {
            stayBlock('splitMenu +++')
            topElement = menu.splitMenuStart(topElement.parent)
            topElement.setTaackTag(TaackTag.MENU_SPLIT)
            stayBlock('splitMenu ---')
        } else {
            stayBlock('splitMenu -+-+-+-')
        }

    }

    private void splitMenuEnd() {
        if (topElement.testParentTaackTag(TaackTag.MENU_SPLIT)) {
            stayBlock('splitMenuEnd TRUE')
            topElement = topElement.toParentTaackTag(TaackTag.MENU_SPLIT)
        } else {
            stayBlock('splitMenuEnd FALSE')
        }
    }

    @Override
    void visitMenu(String controller, String action, Map<String, ?> params) {
        stayBlock('visitMenu')
        String i18n = parameter.trField(controller, action)
        visitLabeledSubMenu(i18n, controller, action, params)
    }


    @Override
    void visitSubMenu(String controller, String action, Map<String, ?> params) {
        stayBlock('visitSubMenu')
        String i18n = parameter.trField(controller, action)
        visitLabeledSubMenu(i18n, controller, action, params)
    }

    private void visitLabeledSubMenu(String i18n, String controller, String action, Map<String, ?> params) {
        stayBlock('visitLabeledSubMenu ' + i18n)
        topElement = menu.menu(topElement, i18n, parameter.isAjaxRendering, parameter.urlMapped(controller, action, params))
    }

    @Override
    void visitMenuSection(String i18n, MenuSpec.MenuPosition position) {
        enterBlock('visitMenuSection ' + i18n)
        menu.section(topElement, i18n)
    }

    @Override
    void visitMenuSectionEnd() {
        exitBlock('visitMenuSectionEnd')

    }

    @Override
    void visitSubMenuIcon(String i18n, ActionIcon actionIcon, String controller, String action, Map<String, ?> params, boolean isModal = false) {
        i18n ?= parameter.trField(controller, action)
        stayBlock('visitSubMenuIcon ' + i18n)
        if (!topElement.testParentTaackTag(TaackTag.MENU_SPLIT)) {
            splitMenu()
        }
        menu.menuIcon(topElement, actionIcon.getHtml(i18n, 24), parameter.urlMapped(controller, action, params, isModal), isModal)
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
        enterBlock('visitMenuSearch')
        splitMenu()
        menu.menuSearch(topElement, q?.replace('"', "&quot;"), parameter.urlMapped(Utils.getControllerName(action), action.method))
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
        String img = currentOption ? parameter.applicationTagLib.img(file: currentOption.asset, width: 20, style: "padding: .5em 0em;") : ''

        topElement = menu.menuOptions(topElement, img, selectedOptionValue)
        topElement.setTaackTag(TaackTag.MENU_OPTION)

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
                menu.menuOptionSection(topElement, img, option.value)
            } else {
                String url = parameter.urlMapped(controller, action, parameter.params as Map, false)
                menu.menuOption(topElement, img, option.value, url)
            }
        }
        topElement = topElement.toParentTaackTag(TaackTag.MENU_OPTION)
        splitMenuEnd()
        exitBlock('visitMenuOptions')
    }

}
