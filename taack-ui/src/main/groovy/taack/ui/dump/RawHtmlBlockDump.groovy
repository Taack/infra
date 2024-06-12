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
import taack.ui.dump.html.block.BootstrapBlock
import taack.ui.dump.html.block.HTMLAjaxCloseLastModal
import taack.ui.dump.html.block.HTMLAjaxCloseModal
import taack.ui.dump.html.block.HTMLFieldInfo
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

    private IHTMLElement _topElement

    IHTMLElement getTopElement() {
//        _topElement ?= new HTMLEmpty()
        _topElement
    }

    IHTMLElement setTopElement(IHTMLElement topElement) {
        _topElement = topElement
    }

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
    }

    private IHTMLElement closeTags(TaackTag tag) {
        IHTMLElement top = topElement
        while (top && top.taackTag != tag && top.parent) {
            top = top.parent
        }
        top.parent ?: top
//        (top?.taackTag == tag ? top?.parent : top) ?: block
    }

    @Override
    void visitBlock() {
        if (!parameter.isAjaxRendering || isModal) {
            topElement = block.block("${parameter.applicationTagLib.controllerName}-${parameter.applicationTagLib.actionName}")
        }
    }

    @Override
    void visitBlockEnd() {
        if (!parameter.isAjaxRendering || isModal) {
            topElement = closeTags(TaackTag.BLOCK)
        }
    }

    @Override
    void visitBlockHeader() {
        topElement = block.blockHeader(topElement)
    }

    @Override
    void visitBlockHeaderEnd() {
        topElement = closeTags(TaackTag.MENU_BLOCK)
    }

    @Override
    void visitCol(final BlockSpec.Width width) {
        topElement = block.col(topElement, width)
    }

    @Override
    void visitColEnd() {
        topElement = closeTags(TaackTag.COL)
    }

    @Override
    void visitCloseTitle() {
    }

    @Override
    void visitAjaxBlock(final String id) {
        if (!parameter.isAjaxRendering || isModal) {
            ajaxBlockId = id
        }
//        if (isModalRefresh) out << "__ajaxBlockStart__$id:"
        if (isModalRefresh) topElement = block.blockAjax(topElement, id)
    }

    @Override
    void visitAjaxBlockEnd() {
        if (!parameter.isAjaxRendering || isModal) ajaxBlockId = null
        if (isModalRefresh) topElement = closeTags(TaackTag.AJAX_BLOCK)
    }

    @Override
    void visitForm(final BlockSpec.Width width) {
        visitCol(width)
    }

    @Override
    void visitFormEnd(UiFormSpecifier formSpecifier) {
        visitCloseTitle()
        formSpecifier.visitForm(new RawHtmlFormDump(topElement, parameter))
        visitColEnd()
    }

    @Override
    void visitShow(final BlockSpec.Width width) {
        visitCol(width)
    }

    @Override
    void visitShowEnd(final UiShowSpecifier uiShowSpecifier) {
        visitCloseTitle()
        ByteArrayOutputStream out = new ByteArrayOutputStream(4096)
        if (uiShowSpecifier) uiShowSpecifier.visitShow(new RawHtmlShowDump(id, out, parameter))
        visitColEnd()
    }

    @Override
    void visitCloseModalAndUpdateBlock() {
        topElement = new HTMLAjaxCloseModal()
    }

    @Override
    void visitCloseModalAndUpdateBlockEnd() {

    }

    @Override
    void visitHtmlBlock(String html, Style style) {
        topElement.addChildren(
                new HTMLDiv().builder.addClasses(style?.cssClassesString).addChildren(
                        new HTMLTxtContent(html)
                ).build()
        )
//        out << """
//            <div class="${style?.cssClassesString ?: ''}">${html}</div>
//        """
    }

    @Override
    void visitTable(final String id, final BlockSpec.Width width) {
        this.id = id
        visitCol(width)
    }

    @Override
    void visitTableEnd(UiTableSpecifier tableSpecifier) {
        tableSpecifier.visitTableWithNoFilter(new RawHtmlTableDump(topElement, id, parameter))
        visitColEnd()
    }

    @Override
    void visitTableFilter(final String id,
                          final UiFilterSpecifier filterSpecifier,
                          final BlockSpec.Width width) {

        visitRow()
        visitCol(BlockSpec.Width.QUARTER)
        filterSpecifier.visitFilter(new RawHtmlFilterDump(topElement, parameter))
        visitColEnd()
        visitCol(BlockSpec.Width.THREE_QUARTER)
    }

    @Override
    void visitTableFilterEnd(final UiTableSpecifier tableSpecifier) {
        tableSpecifier.visitTable(new RawHtmlTableDump(topElement, id, parameter))
        visitColEnd()
        visitRowEnd()
    }

    @Override
    void visitChart(final BlockSpec.Width width) {
        visitCol(width)
    }

    @Override
    void visitChartEnd(final UiChartSpecifier chartSpecifier) {
        visitCloseTitle()
        ByteArrayOutputStream out = new ByteArrayOutputStream(4096)
        chartSpecifier.visitChart(new RawHtmlChartDump(out, ajaxBlockId))
        visitColEnd()
        visitColEnd()
    }

    @Override
    void visitDiagram(final BlockSpec.Width width) {
        visitCol(width)
    }

    @Override
    void visitDiagramFilter(final UiFilterSpecifier filterSpecifier, final BlockSpec.Width width) {
        visitCol(width)
        visitCloseTitle()
        filterSpecifier.visitFilter(new RawHtmlFilterDump(topElement, parameter))
        visitColEnd()
    }

    @Override
    void visitDiagramEnd(final UiDiagramSpecifier diagramSpecifier, final BlockSpec.Width width = BlockSpec.Width.MAX) {
        visitCloseTitle()
        ByteArrayOutputStream out = new ByteArrayOutputStream(4096)
        diagramSpecifier.visitDiagram(new RawHtmlDiagramDump(out, ajaxBlockId, width), UiDiagramSpecifier.DiagramBase.SVG)
        visitColEnd()
        visitColEnd()
    }

    @Override
    void visitCloseModal(final String id, final String value, FieldInfo[] fields = null) {

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
        currentTabNames << i18n
        topElement = block.tab(topElement, ++tabOccurrence)
    }

    @Override
    void visitBlockTabEnd() {
        topElement = closeTags(TaackTag.TAB)
    }

    private List<String> currentTabNames = []
    private IHTMLElement oldParent = null
    private BlockSpec.Width blockTabWidth

    @Override
    void visitBlockTabs(final BlockSpec.Width width) {
        oldParent = topElement
        topElement = new HTMLEmpty()
    }

    @Override
    void visitBlockTabsEnd() {
        IHTMLElement tabsContent = topElement
        topElement = block.tabs(oldParent, random.nextInt(), currentTabNames, blockTabWidth)
        topElement.addChildren(tabsContent)
        topElement = closeTags(TaackTag.TABS)
    }

    @Override
    void visitCustom(final String html, Style style, final BlockSpec.Width width) {
        visitCol(width)
        visitCloseTitle()
        visitHtmlBlock(html, style)
        visitColEnd()
        visitColEnd()
    }

    @Override
    void visitModal() {
        isModal = true
    }

    @Override
    void visitModalEnd() {
        isModal = false
    }

    @Override
    Map getParameterMap() {
        parameter.applicationTagLib.params
    }

    @Override
    void visitRow() {
        topElement = block.row(topElement)
    }

    @Override
    void visitRowEnd() {
        topElement = closeTags(TaackTag.ROW)
    }

    @Override
    void visitMenuLabel(String i18n, boolean hasClosure) {
        topElement = menu.label(topElement, i18n, hasClosure)
    }

    @Override
    void visitMenuLabelEnd() {
        topElement = closeTags(TaackTag.LABEL)
    }

    @Override
    void visitMenuStart(MenuSpec.MenuMode menuMode) {
        topElement = menu.menuStart(topElement)

    }

    @Override
    void visitMenuStartEnd() {
        topElement = closeTags(TaackTag.MENU)
    }

    private void splitMenuStart() {
        topElement = menu
        topElement = menu.splitMenuStart(topElement)
    }

    @Override
    void visitMenu(String controller, String action, Map<String, ?> params) {
        String i18n = parameter.trField(controller, action)
        visitLabeledSubMenu(i18n, controller, action, params)
    }


    @Override
    void visitSubMenu(String controller, String action, Map<String, ?> params) {
        String i18n = parameter.trField(controller, action)
        visitLabeledSubMenu(i18n, controller, action, params)
    }

    private void visitLabeledSubMenu(String i18n, String controller, String action, Map<String, ?> params) {
        topElement = menu.menu(topElement, i18n, parameter.isAjaxRendering, parameter.urlMapped(controller, action, params))
    }

    @Override
    void visitMenuSection(String i18n, MenuSpec.MenuPosition position) {
        menu.section(topElement, i18n)
    }

    @Override
    void visitMenuSectionEnd() {

    }

    @Override
    void visitSubMenuIcon(String i18n, ActionIcon actionIcon, String controller, String action, Map<String, ?> params, boolean isModal = false) {
        i18n ?= parameter.trField(controller, action)
        splitMenuStart()
        topElement = menu.menuIcon(topElement, actionIcon.getHtml(i18n, 24), parameter.urlMapped(controller, action, params, isModal), isModal)
    }

    @Override
    void visitMenuSelect(String paramName, IEnumOptions enumOptions, Map<String, ?> params) {
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
        splitMenuStart()
        topElement = menu.menuSearch(menu, q?.replace('"', "&quot;"), parameter.urlMapped(Utils.getControllerName(action), action.method))
    }

    @Override
    void visitMenuOptions(IEnumOptions enumOptions) {
        splitMenuStart()
        String selectedOptionKey = parameter.params[enumOptions.paramKey]

        IEnumOption currentOption = selectedOptionKey ? (enumOptions.options.find { it.key == selectedOptionKey }) : enumOptions.currents?.first() as IEnumOption
        String selectedOptionValue = currentOption ? currentOption.value : selectedOptionKey
        String img = currentOption ? parameter.applicationTagLib.img(file: currentOption.asset, width: 20, style: "padding: .5em 0em;") : ''

        topElement = menu.menuOptions(topElement, img, selectedOptionValue)

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
        topElement = closeTags(TaackTag.MENU_OPTION)
    }

}
