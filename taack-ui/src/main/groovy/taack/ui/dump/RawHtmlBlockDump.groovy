package taack.ui.dump

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
import taack.ui.dump.html.block.BootstrapBlock
import taack.ui.dump.html.block.HTMLAjaxCloseModal
import taack.ui.dump.html.element.IHTMLElement
import taack.ui.dump.html.element.TaackTag
import taack.ui.dump.html.menu.BootstrapMenu
import taack.ui.dump.html.theme.ThemeSelector

@CompileStatic
final class RawHtmlBlockDump implements IUiBlockVisitor {
    private String id
    final ByteArrayOutputStream out
    final String modalId


    private String ajaxBlockId = null
    final private Long blockId = System.currentTimeMillis()
    boolean isModal = false
    boolean isModalRefresh = false

    private int tabOccurrence = 0
    private int tabOccurrencePrevious = 0
    private int tabIds = 0

    final BootstrapBlock bootstrapBlock
    final BootstrapMenu menu
    final Parameter parameter

    IHTMLElement topElement

    RawHtmlBlockDump(final ByteArrayOutputStream out, final Parameter parameter, final String modalId = null) {
        if (modalId) isModal = true
        this.parameter = parameter
        this.modalId = modalId
        this.out = out
        if (parameter.params.boolean('refresh'))
            isModalRefresh = true
        ThemeSelector ts = parameter.uiThemeService.themeSelector
        bootstrapBlock = new BootstrapBlock(ts.themeMode, ts.themeSize)
    }

    private IHTMLElement closeTags(TaackTag tag) {
        IHTMLElement top = topElement
        while (top && top.taackTag != tag) {
            top = top.parent
        }
        (top?.taackTag == tag ? top?.parent : top) ?: bootstrapBlock
    }

    @Override
    void visitBlock() {
        if (!parameter.isAjaxRendering || isModal) {
            topElement = bootstrapBlock.block("${parameter.applicationTagLib.controllerName}-${parameter.applicationTagLib.actionName}")
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
        topElement = bootstrapBlock.blockHeader(topElement)
//        out << """
//            <nav class="navbar navbar-expand-md" >
//                <div id="dropdownNav" class="container-fluid">
//                    <button id="dLabel" class="navbar-toggler navbar-dark" type="button" data-bs-toggle="collapse"
//                            data-bs-target="#navbarSupportedContent"
//                            aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation"
//                            data-bs-toggle="dropdownNav">
//                        <span class="navbar-toggler-icon"></span>
//                    </button>
//                    <div class="collapse navbar-collapse" id="navbarSupportedContent">
//        """
    }

    @Override
    void visitBlockHeaderEnd() {
//        out << """
//                    </div>
//                </div>
//            </nav>
//        """
        topElement = closeTags(TaackTag.MENU_BLOCK)
    }

    @Override
    void visitCol(final BlockSpec.Width width) {
//        out << """<div class="${width.bootstrapCss} align-items-start">"""
        topElement = bootstrapBlock.col(topElement)
    }

    @Override
    void visitInnerColBlockEnd() {
//        out << "</div>"
        topElement = closeTags(TaackTag.MENU_COL)

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
        if (isModalRefresh) topElement = bootstrapBlock.blockAjax(id)
    }

    @Override
    void visitAjaxBlockEnd() {
        if (!parameter.isAjaxRendering || isModal) ajaxBlockId = null
        if (isModalRefresh) out << topElement.output
        //else out << "</div>"
    }

    @Override
    void visitForm(final BlockSpec.Width width) {
        visitCol(width)
    }

    @Override
    void visitFormEnd(UiFormSpecifier formSpecifier) {
        visitCloseTitle()
        formSpecifier.visitForm(new RawHtmlFormDump(out, parameter))
        visitInnerColBlockEnd()
    }

    @Override
    void visitShow(final BlockSpec.Width width) {
        visitCol(width)
    }

    @Override
    void visitShowEnd(final UiShowSpecifier uiShowSpecifier) {
        visitCloseTitle()
        if (uiShowSpecifier) uiShowSpecifier.visitShow(new RawHtmlShowDump(id, out, parameter))
        visitInnerColBlockEnd()
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
        out << """
            <div class="${style?.cssClassesString ?: ''}">${html}</div>
        """
    }

    @Override
    void visitTable(final String id, final BlockSpec.Width width) {
        this.id = id
        visitCol(width)
    }

    @Override
    void visitTableEnd(UiTableSpecifier tableSpecifier) {
        tableSpecifier.visitTableWithNoFilter(new RawHtmlTableDump(id, out, parameter))
        visitInnerColBlockEnd()
    }

    @Override
    void visitTableFilter(final String id,
                          final UiFilterSpecifier filterSpecifier,
                          final BlockSpec.Width width) {

        visitRow()
        visitCol(BlockSpec.Width.QUARTER)
        filterSpecifier.visitFilter(new RawHtmlFilterDump(out, parameter))
        visitInnerColBlockEnd()
        visitCol(BlockSpec.Width.THREE_QUARTER)
    }

    @Override
    void visitTableFilterEnd(final UiTableSpecifier tableSpecifier) {
        tableSpecifier.visitTable(new RawHtmlTableDump(id, out, parameter))
        visitInnerColBlockEnd()
        visitRowEnd()
    }

    @Override
    void visitChart(final BlockSpec.Width width) {
        visitCol(width)
    }

    @Override
    void visitChartEnd(final UiChartSpecifier chartSpecifier) {
        visitCloseTitle()
        chartSpecifier.visitChart(new RawHtmlChartDump(out, ajaxBlockId))
        visitInnerColBlockEnd()
        visitInnerColBlockEnd()
    }

    @Override
    void visitDiagram(final BlockSpec.Width width) {
        visitCol(width)
    }

    @Override
    void visitDiagramFilter(final UiFilterSpecifier filterSpecifier, final BlockSpec.Width width) {
        visitCol(width)
        visitCloseTitle()
        filterSpecifier.visitFilter(new RawHtmlFilterDump(out, parameter))
        visitInnerColBlockEnd()
    }

    @Override
    void visitDiagramEnd(final UiDiagramSpecifier diagramSpecifier, final BlockSpec.Width width = BlockSpec.Width.MAX) {
        visitCloseTitle()
        diagramSpecifier.visitDiagram(new RawHtmlDiagramDump(out, ajaxBlockId, width), UiDiagramSpecifier.DiagramBase.SVG)
        visitInnerColBlockEnd()
        visitInnerColBlockEnd()
    }

    @Override
    void visitCloseModal(final String id, final String value, FieldInfo[] fields = null) {
        out << "__closeLastModal__:${id ?: ""}:${value ?: ""}"
        for (FieldInfo fi : fields) {
            if (fi.value) {
                if (parameter.nf && fi.value instanceof Number) out << ":__FieldInfo__:${fi.fieldName}:${parameter.nf.format(fi.value)}:__FieldInfoEnd__"
                else out << ":__FieldInfo__:${fi.fieldName}:${fi.value.toString()}:__FieldInfoEnd__"
            }
        }
    }

    @Override
    void visitBlockTab(final String i18n) {
        currentTabNames << i18n
        out << """<div class="tab${++tabOccurrence}${tabOccurrencePrevious != 0 ? "Inner" : ""}">"""
    }

    @Override
    void visitBlockTabEnd() {
        out << '</div>'
    }

    private List<String> currentTabNames
    private ByteArrayOutputStream outBkup
    private BlockSpec.Width blockTabWidth

    @Override
    void visitBlockTabs(final BlockSpec.Width width) {
        outBkup = out
//        out = new ByteArrayOutputStream()
        blockTabWidth = width
        currentTabNames = []
    }

    @Override
    void visitBlockTabsEnd() {
        outBkup << """<div class="pc-tab ${blockTabWidth.bootstrapCss} taackContainer">"""
        currentTabNames.eachWithIndex { it, occ ->
            outBkup << """<input ${occ == 0 ? 'checked="checked"' : ''} id="tab${occ + 1}-${tabIds}" type="radio" class="taackBlockInputTab inputTab${occ + 1}${false ? "Inner" : ""}" name="pct-${tabIds}" />"""
        }
        outBkup << "<nav><ul>"
        currentTabNames.eachWithIndex { it, occ ->
            outBkup << """
                <li class="tab${occ + 1}">
                    <label for="tab${occ + 1}-${tabIds}">${it}</label>
                </li>
            """
        }
        outBkup << "</ul></nav>"
        outBkup << "<section>"
        tabIds++
        if (tabOccurrence != 0) tabOccurrencePrevious = tabOccurrence
        tabOccurrence = 0
        out.writeTo(outBkup)
//        out = outBkup

        tabOccurrence = tabOccurrencePrevious
        tabOccurrencePrevious = 0
        out << "</section></div>"
    }

    @Override
    void visitCustom(final String html, Style style, final BlockSpec.Width width) {
        visitCol(width)
        visitCloseTitle()
        visitHtmlBlock(html, style)
        visitInnerColBlockEnd()
        visitInnerColBlockEnd()
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
        out << """<div class="row align-items-start">"""
    }

    @Override
    void visitRowEnd() {
        out << "</div>"
    }

    @Override
    void visitMenuLabel(String i18n, boolean hasClosure) {
        topElement = menu.label(this, i18n, hasClosure)
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
        topElement = closeTags(TaackTag.MENU)
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
        menu.menu(topElement, i18n, parameter.urlMapped(controller, action, params))
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
        menu.menuIcon(topElement, actionIcon.getHtml(i18n, 24), parameter.urlMapped(controller, action, params, isModal), isModal)
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
        menu.menuSearch(topElement, q?.replace('"', "&quot;"), parameter.urlMapped(Utils.getControllerName(action), action.method))
    }

    @Override
    void visitMenuOptions(IEnumOptions enumOptions) {
        splitMenuStart()
        String selectedOptionKey = parameter.params[enumOptions.paramKey]

        IEnumOption currentOption = selectedOptionKey ? (enumOptions.options.find { it.key == selectedOptionKey }) : enumOptions.currents?.first() as IEnumOption
        String selectedOptionValue = currentOption ? currentOption.value : selectedOptionKey
//        String current = """\
//            <a class="nav-link dropdown-toggle" id="navbar${enumOptions.paramKey.capitalize()}" role="button" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
//                ${currentOption ? parameter.applicationTagLib.img(file: currentOption.asset, width: 20, style: "padding: .5em 0em;") : ''}
//                ${selectedOptionValue}
//            </a>
//        """.stripIndent()

//        out << """\
//        <li class="nav-item dropdown">
//            $current
//            <ul class="dropdown-menu" aria-labelledby="navbar${enumOptions.paramKey.capitalize()}">
//        """.stripIndent()

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
            if (option.section) {
                img = parameter.applicationTagLib.img(file: option.asset, width: 20, style: "padding: .5em 0em;")
//                out << """\
//                    <li>
//                        <a class="dropdown-item" style="color: #887700">
//                            ${parameter.applicationTagLib.img(file: option.asset, width: 20, style: "padding: .5em 0em;")}
//                            <b>${option.value}</b>
//                        </a>
//                    </li>
//                """.stripIndent()
                menu.menuOptionSection(topElement, img, option.value)
            } else {
//                out << """\
//                    <li>
//                        <a class='dropdown-item' href='${parameter.urlMapped(controller, action, parameter.params as Map, false)}'>
//                            ${parameter.applicationTagLib.img(file: option.asset, width: 20, style: "padding: .5em 0em;")}
//                            ${option.value}
//                        </a>
//                    </li>
//                """.stripIndent()
                String url = parameter.urlMapped(controller, action, parameter.params as Map, false)
                menu.menuOption(topElement, img, option.value, url)
            }
        }
//        out << """\
//            </ul>
//        </li>
//        """.stripIndent()
        closeTags(TaackTag.MENU_OPTION)

    }

}
