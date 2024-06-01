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
import taack.ui.base.common.ActionIcon
import taack.ui.base.common.Style
import taack.ui.base.helper.Utils
import taack.ui.dump.html.theme.ThemeSelector

@CompileStatic
class RawHtmlBlockDump extends RawHtmlMenuDump implements IUiBlockVisitor {
    private String id

    private boolean hasTitle = false
    private int hasPureG = 0
    private String ajaxBlockId = null
    final private Long blockId = System.currentTimeMillis()
    boolean isModal = false

    private int tabOccurrence = 0
    private int tabOccurrencePrevious = 0
    private int tabIds = 0

    RawHtmlBlockDump(final ByteArrayOutputStream out, final Parameter parameter, final String modalId = null) {
        super(out, modalId, parameter)
    }

    @Override
    void visitBlock() {
        if (!parameter.isAjaxRendering || isModal) {
            out << "<div id='blockId${blockId}' class='taackBlock' 'blockId'='${parameter.applicationTagLib.controllerName}-${parameter.applicationTagLib.actionName}'>"
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
    void visitInnerBlock(final BlockSpec.Width width) {
//            if (!parameter.isAjaxRendering || isModal)
//                out << "<div class='${width.css} ${!isModal ? 'taackContainer' : ''} ${ajaxBlockId ? "ajaxBlock ${hasPureG == 0?'taackAjaxBlock': ''}" : ""}' ${ajaxBlockId ? "ajaxBlockId=${ajaxBlockId}" : ""}>"
//            else
//                out << "<div>"
        out << "<div class='${width.css} ${!isModal ? 'taackContainer' : ''}' >"

    }

    @Override
    void visitInnerBlockEnd() {
        out << "</div>"
        out << "</div>"
    }

    @Override
    void visitCloseTitle() {
    }

    @Override
    void visitAjaxBlock(final String id) {
        if (!parameter.isAjaxRendering || isModal) ajaxBlockId = id
        else out << "__ajaxBlockStart__$id:"
    }

    @Override
    void visitAjaxBlockEnd() {
        if (!parameter.isAjaxRendering || isModal) ajaxBlockId = null
        else out << "__ajaxBlockEnd__"
    }

    @Override
    void visitForm(final BlockSpec.Width width) {
        visitInnerBlock(width)
    }

    @Override
    void visitFormEnd(UiFormSpecifier formSpecifier) {
        visitCloseTitle()
        formSpecifier.visitForm(new RawHtmlFormDump(out, parameter))
        visitInnerBlockEnd()
        visitInnerBlockEnd()
    }

    @Override
    void visitShow(final BlockSpec.Width width) {
        visitInnerBlock(width)
    }

    @Override
    void visitShowEnd(final UiShowSpecifier uiShowSpecifier) {
        visitCloseTitle()
        if (uiShowSpecifier) uiShowSpecifier.visitShow(new RawHtmlShowDump(id, out, parameter))
        visitInnerBlockEnd()
        visitInnerBlockEnd()
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
        visitInnerBlock(width)
    }

    @Override
    void visitTableEnd(UiTableSpecifier tableSpecifier) {
        visitCloseTitle()
        tableSpecifier.visitTableWithNoFilter(new RawHtmlTableDump(id, out, parameter))
        visitInnerBlockEnd()
        visitInnerBlockEnd()
    }

    @Override
    void visitTableFilter(final String id,
                          final UiFilterSpecifier filterSpecifier,
                          final BlockSpec.Width width) {

        def recordStateForId = parameter.applicationTagLib.params['recordStateDecoded']?[id] as Map
        if (recordStateForId) {
            parameter.applicationTagLib.params.putAll(recordStateForId)
        }
        visitInnerBlock(width)
        visitInnerBlock(BlockSpec.Width.QUARTER)
        filterSpecifier.visitFilter(new RawHtmlFilterDump(out, parameter))
        visitInnerBlockEnd()
        visitInnerBlock(BlockSpec.Width.THREE_QUARTER)
    }

    @Override
    void visitTableFilterEnd(final UiTableSpecifier tableSpecifier) {
        tableSpecifier.visitTable(new RawHtmlTableDump(id, out, parameter))
        visitInnerBlockEnd()
        visitInnerBlockEnd()
    }

    @Override
    void visitChart(final BlockSpec.Width width) {
        visitInnerBlock(width)
    }

    @Override
    void visitChartEnd(final UiChartSpecifier chartSpecifier) {
        visitCloseTitle()
        chartSpecifier.visitChart(new RawHtmlChartDump(out, ajaxBlockId))
        visitInnerBlockEnd()
        visitInnerBlockEnd()
    }

    @Override
    void visitDiagram(final BlockSpec.Width width) {
        visitInnerBlock(width)
    }

    @Override
    void visitDiagramFilter(final UiFilterSpecifier filterSpecifier, final BlockSpec.Width width) {
        visitInnerBlock(width)
        visitCloseTitle()
        filterSpecifier.visitFilter(new RawHtmlFilterDump(out, parameter))
        visitInnerBlockEnd()
    }

    @Override
    void visitDiagramEnd(final UiDiagramSpecifier diagramSpecifier, final BlockSpec.Width width = BlockSpec.Width.MAX) {
        visitCloseTitle()
        diagramSpecifier.visitDiagram(new RawHtmlDiagramDump(out, ajaxBlockId, width), UiDiagramSpecifier.DiagramBase.SVG)
        visitInnerBlockEnd()
        visitInnerBlockEnd()
    }

    @Override
    void visitCloseModal(final String id, final String value, FieldInfo[] fields = null) {
        out << "__closeLastModal__:${id?:""}:${value?:""}"
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
        out << """<div class="tab${++tabOccurrence}${tabOccurrencePrevious != 0?"Inner":""}">"""
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
        outBkup << """<div class="pc-tab ${blockTabWidth.css} taackContainer">"""
        currentTabNames.eachWithIndex { it, occ ->
            outBkup << """<input ${occ == 0 ? 'checked="checked"' : ''} id="tab${occ + 1}-${tabIds}" type="radio" class="taackBlockInputTab inputTab${occ + 1}${/*tabOccurrence != 0*/ false ?"Inner":""}" name="pct-${tabIds}" />"""
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
        visitInnerBlock(width)
        visitCloseTitle()
        visitHtmlBlock(html, style)
        visitInnerBlockEnd()
        visitInnerBlockEnd()
    }

    @Override
    void anonymousBlock(BlockSpec.Width width) {
        out << """<div class="${width.css}">"""
    }

    @Override
    void anonymousBlockEnd() {
        out << "</div>"
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

}
