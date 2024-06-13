package taack.ui.dump.mail

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import org.grails.datastore.gorm.GormEntity
import taack.ast.type.FieldInfo
import taack.ui.IEnumOption
import taack.ui.IEnumOptions
import taack.ui.dsl.UiChartSpecifier
import taack.ui.dsl.UiDiagramSpecifier
import taack.ui.dsl.UiFilterSpecifier
import taack.ui.dsl.UiFormSpecifier
import taack.ui.dsl.UiShowSpecifier
import taack.ui.dsl.UiTableSpecifier
import taack.ui.dsl.block.BlockSpec
import taack.ui.dsl.block.IUiBlockVisitor
import taack.ui.dsl.common.ActionIcon
import taack.ui.dsl.common.Style
import taack.ui.dsl.helper.Utils
import taack.ui.dsl.menu.MenuSpec
import taack.ui.dump.Parameter
import taack.ui.dump.RawHtmlChartDump
import taack.ui.dump.RawHtmlDiagramDump
import taack.ui.dump.RawHtmlFilterDump
import taack.ui.dump.RawHtmlFormDump
import taack.ui.dump.html.block.BootstrapBlock
import taack.ui.dump.html.block.HTMLAjaxCloseLastModal
import taack.ui.dump.html.block.HTMLAjaxCloseModal
import taack.ui.dump.html.block.HTMLFieldInfo
import taack.ui.dump.html.element.HTMLDiv
import taack.ui.dump.html.element.HTMLTxtContent
import taack.ui.dump.html.element.IHTMLElement
import taack.ui.dump.html.element.TaackTag
import taack.ui.dump.html.menu.BootstrapMenu
import taack.ui.dump.html.theme.ThemeSelector

@CompileStatic
final class RawHtmlBlockDump implements IUiBlockVisitor {
    private String id
    final String modalId


    private String ajaxBlockId = null
    final private Long blockId = System.currentTimeMillis()
    boolean isModal = false
    boolean isModalRefresh = false

    private int tabOccurrence = 0
    private int tabOccurrencePrevious = 0
    private int tabIds = 0

    final BootstrapBlock block
    final BootstrapMenu menu
    final Parameter parameter

    IHTMLElement topElement

    RawHtmlBlockDump(final ByteArrayOutputStream out, final Parameter parameter, final String modalId = null) {
        if (modalId) isModal = true
        this.parameter = parameter
        this.modalId = modalId
        if (parameter.params.boolean('refresh'))
            isModalRefresh = true
        ThemeSelector ts = parameter.uiThemeService.themeSelector
        block = new BootstrapBlock(ts.themeMode, ts.themeSize)
        menu = new BootstrapMenu(ts.themeMode, ts.themeSize)
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
//        out << """<div class="${width.bootstrapCss} align-items-start">"""
        topElement = block.col(topElement)
    }

    @Override
    void visitColEnd() {
//        out << "</div>"
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
    }

    @Override
    void visitForm(UiFormSpecifier formSpecifier) {

    }

    @Override
    void visitFormEnd(UiFormSpecifier formSpecifier) {
        visitCloseTitle()
        formSpecifier.visitForm(new RawHtmlFormDump(topElement, parameter))
        visitColEnd()
    }

    @Override
    void visitShow() {

    }

    @Override
    void visitTable(String id, UiTableSpecifier tableSpecifier) {

    }

    @Override
    void visitShowEnd(final UiShowSpecifier uiShowSpecifier) {
        visitCloseTitle()
        ByteArrayOutputStream out = new ByteArrayOutputStream(4096)
        if (uiShowSpecifier) uiShowSpecifier.visitShow(new RawHtmlShowDump(out, parameter))
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

//    @Override
//    void visitTable(final String id, final BlockSpec.Width width) {
//        this.id = id
//        visitCol(width)
//    }

    @Override
    void visitTableEnd(UiTableSpecifier tableSpecifier) {
        ByteArrayOutputStream out = new ByteArrayOutputStream(2048)
        tableSpecifier.visitTableWithNoFilter(new RawHtmlTableDump(out, parameter))
        visitColEnd()
    }

    @Override
    void visitTableFilter(String id, UiFilterSpecifier filterSpecifier, UiTableSpecifier tableSpecifier) {
        visitRow()
        visitCol(BlockSpec.Width.QUARTER)
        filterSpecifier.visitFilter(new RawHtmlFilterDump(topElement, parameter))
        visitColEnd()
        visitCol(BlockSpec.Width.THREE_QUARTER)
        ByteArrayOutputStream out = new ByteArrayOutputStream(4096)
        tableSpecifier.visitTable(new RawHtmlTableDump(out, parameter))
        visitColEnd()
        visitRowEnd()

    }

    @Override
    void visitChart() {

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
    void visitDiagram() {

    }

    @Override
    void visitDiagramFilter(UiFilterSpecifier filterSpecifier) {

    }

    @Override
    void visitDiagramEnd(UiDiagramSpecifier diagramSpecifier) {

    }

    @Override
    void visitCloseModal(String id, String value, FieldInfo[] fields) {

    }

    @Override
    void visitBlockTab(final String i18n) {
        currentTabNames << i18n
//        out << """<div class="tab${++tabOccurrence}${tabOccurrencePrevious != 0 ? "Inner" : ""}">"""
        topElement = block.tab(topElement, ++tabOccurrence)
    }

    @Override
    void visitBlockTabEnd() {
//        out << '</div>'
        topElement = closeTags(TaackTag.TAB)
    }

    @Override
    void visitBlockTabs() {

    }
    private List<String> currentTabNames
    private BlockSpec.Width blockTabWidth

    @Override
    void visitBlockTabsEnd() {
        topElement = block.tabs(topElement, tabIds, currentTabNames, blockTabWidth)
    }

    @Override
    void visitCustom(String html, Style style) {

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
//        out << """<div class="row align-items-start">"""
        topElement = block.row(topElement)
    }

    @Override
    void visitRowEnd() {
//        out << "</div>"
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
    void visitSubMenuIcon(String i18n, ActionIcon actionIcon, String controller, String action, Map<String, ?> params, boolean isModal) {

    }

    @Override
    void visitMenuSelect(String paramName, IEnumOptions enumOptions, Map<String, ?> params) {

    }

    @Override
    void visitMenuSearch(MethodClosure action, String q, Class<? extends GormEntity>[] aClasses) {
        splitMenuStart()
        topElement = menu.menuSearch(menu, q?.replace('"', "&quot;"), parameter.urlMapped(Utils.getControllerName(action), action.method))
    }

    @Override
    void visitMenuOptions(IEnumOptions enumOptions) {

    }
}
