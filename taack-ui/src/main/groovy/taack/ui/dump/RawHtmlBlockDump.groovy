package taack.ui.dump

import groovy.transform.CompileStatic
import taack.ast.type.FieldInfo
import taack.ui.base.UiChartSpecifier
import taack.ui.base.UiDiagramSpecifier
import taack.ui.base.UiFilterSpecifier
import taack.ui.base.UiFormSpecifier
import taack.ui.base.UiShowSpecifier
import taack.ui.base.UiTableSpecifier
import taack.ui.base.block.BlockSpec
import taack.ui.base.block.IUiBlockVisitor
import taack.ui.base.common.Style

@CompileStatic
class RawHtmlBlockDump extends RawHtmlMenuDump implements IUiBlockVisitor {
    private String id

    private String ajaxBlockId = null
    final private Long blockId = System.currentTimeMillis()
    boolean isModal = false
    boolean isModalRefresh = false

    private int tabOccurrence = 0
    private int tabOccurrencePrevious = 0
    private int tabIds = 0

    RawHtmlBlockDump(final ByteArrayOutputStream out, final Parameter parameter, final String modalId = null) {
        super(out, modalId, parameter)
        if (modalId) isModal = true
        if (parameter.params.boolean('refresh'))
            isModalRefresh = true
    }

    @Override
    void visitBlock() {
        if (!parameter.isAjaxRendering || isModal) {
            out << "<div id='blockId${blockId}' class='container-fluid' blockId='${parameter.applicationTagLib.controllerName}-${parameter.applicationTagLib.actionName}'>"
        }
    }

    @Override
    void visitBlockEnd() {
        if (!parameter.isAjaxRendering || isModal) {
            out << "</div>"
        }
    }

    @Override
    void visitBlockHeader() {
        out << """
            <nav class="navbar navbar-expand-md" >
                <div id="dropdownNav" class="container-fluid">
                    <button id="dLabel" class="navbar-toggler navbar-dark" type="button" data-bs-toggle="collapse"
                            data-bs-target="#navbarSupportedContent"
                            aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation"
                            data-bs-toggle="dropdownNav">
                        <span class="navbar-toggler-icon"></span>
                    </button>
                    <div class="collapse navbar-collapse" id="navbarSupportedContent">
        """
    }

    @Override
    void visitBlockHeaderEnd() {
        out << """
                    </div>
                </div>
            </nav>
        """
    }

    @Override
    void visitInnerColBlock(final BlockSpec.Width width) {
        out << """<div class="${width.bootstrapCss} align-items-start">"""
    }

    @Override
    void visitInnerColBlockEnd() {
        out << "</div>"
    }

    @Override
    void visitCloseTitle() {
    }

    @Override
    void visitAjaxBlock(final String id) {
        if (!parameter.isAjaxRendering || isModal) {
            ajaxBlockId = id
        }
        if (isModalRefresh) out << "__ajaxBlockStart__$id:"
    }

    @Override
    void visitAjaxBlockEnd() {
        if (!parameter.isAjaxRendering || isModal) ajaxBlockId = null
        if (isModalRefresh) out << "__ajaxBlockEnd__"
    }

    @Override
    void visitForm(final BlockSpec.Width width) {
        visitInnerColBlock(width)
    }

    @Override
    void visitFormEnd(UiFormSpecifier formSpecifier) {
        visitCloseTitle()
        formSpecifier.visitForm(new RawHtmlFormDump(out, parameter))
        visitInnerColBlockEnd()
    }

    @Override
    void visitShow(final BlockSpec.Width width) {
        visitInnerColBlock(width)
    }

    @Override
    void visitShowEnd(final UiShowSpecifier uiShowSpecifier) {
        visitCloseTitle()
        if (uiShowSpecifier) uiShowSpecifier.visitShow(new RawHtmlShowDump(id, out, parameter))
        visitInnerColBlockEnd()
    }

    @Override
    void visitCloseModalAndUpdateBlock() {
        out << "__closeLastModalAndUpdateBlock__:"
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
        def recordStateForId = parameter.applicationTagLib.params['recordStateDecoded']?[id] as Map
        if (recordStateForId) {
            parameter.applicationTagLib.params.putAll(recordStateForId)
        }
        visitInnerColBlock(width)
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

        def recordStateForId = parameter.applicationTagLib.params['recordStateDecoded']?[id] as Map
        if (recordStateForId) {
            parameter.applicationTagLib.params.putAll(recordStateForId)
        }
        visitInnerRowBlock()
        visitInnerColBlock(BlockSpec.Width.QUARTER)
        filterSpecifier.visitFilter(new RawHtmlFilterDump(out, parameter))
        visitInnerColBlockEnd()
        visitInnerColBlock(BlockSpec.Width.THREE_QUARTER)
    }

    @Override
    void visitTableFilterEnd(final UiTableSpecifier tableSpecifier) {
        tableSpecifier.visitTable(new RawHtmlTableDump(id, out, parameter))
        visitInnerColBlockEnd()
        visitInnerRowBlockEnd()
    }

    @Override
    void visitChart(final BlockSpec.Width width) {
        visitInnerColBlock(width)
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
        visitInnerColBlock(width)
    }

    @Override
    void visitDiagramFilter(final UiFilterSpecifier filterSpecifier, final BlockSpec.Width width) {
        visitInnerColBlock(width)
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
        visitInnerColBlock(width)
        visitCloseTitle()
        visitHtmlBlock(html, style)
        visitInnerColBlockEnd()
        visitInnerColBlockEnd()
    }

    @Override
    void visitModal() {
        isModal = true
        if (!isModalRefresh)
            out << "<div ajaxBlockId='modal${blockId}'>"

    }

    @Override
    void visitModalEnd() {
        isModal = false
        if (!isModalRefresh)
            out << "</div>"
    }

    @Override
    Map getParameterMap() {
        parameter.applicationTagLib.params
    }

    @Override
    void visitInnerRowBlock() {
        out << """<div class="row align-items-start">"""
    }

    @Override
    void visitInnerRowBlockEnd() {
        out << "</div>"
    }
}
