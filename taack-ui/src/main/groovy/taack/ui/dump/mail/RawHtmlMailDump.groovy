package taack.ui.dump.mail

import groovy.transform.CompileStatic
import taack.ui.base.UiChartSpecifier
import taack.ui.base.UiShowSpecifier
import taack.ui.base.UiTableSpecifier
import taack.ui.base.block.BlockSpec
import taack.ui.base.block.UiBlockVisitor
import taack.ui.base.common.Style
import taack.ui.dump.Parameter
import taack.ui.dump.RawHtmlChartDump

@CompileStatic
class RawHtmlMailDump extends UiBlockVisitor {
    private ByteArrayOutputStream out
    final Parameter parameter

    private String id

    private String ajaxBlockId = null
    final private Long blockId = System.currentTimeMillis()
    boolean isModal = false
    final String serverUrl

    RawHtmlMailDump(final ByteArrayOutputStream out, final Parameter parameter, final String serverUrl) {
        this.out = out
        this.parameter = parameter
        this.serverUrl = serverUrl
    }

    @Override
    void visitBlock() {
        if (!parameter.isAjaxRendering || isModal) out << "<div id='blockId${blockId}'>"
    }

    @Override
    void visitBlockEnd() {
        if (!parameter.isAjaxRendering || isModal) {
            out << "</div>"
        }
    }

    @Override
    void visitCloseTitle() {
        out << "</div>"
    }

    @Override
    void visitShow(final BlockSpec.Width width) {
    }

    @Override
    void visitShowEnd(final UiShowSpecifier uiShowSpecifier) {
        visitCloseTitle()
        if (uiShowSpecifier) uiShowSpecifier.visitShow(new RawHtmlShowDump(out, parameter))
        visitInnerColBlockEnd()
        visitInnerColBlockEnd()
    }

    @Override
    void visitHtmlBlock(String html, Style style) {
        out << """
            <div style="${style.labelCssStyleString}">${html}</div>
        """
    }

    @Override
    void visitTable(final String id, final BlockSpec.Width width) {
        this.id = id
        visitInnerColBlock(width)
    }

    @Override
    void visitTableEnd(UiTableSpecifier tableSpecifier) {
        visitCloseTitle()
        tableSpecifier.visitTable(new taack.ui.dump.RawHtmlTableDump(id, out, parameter))
        visitInnerColBlockEnd()
        visitInnerColBlockEnd()
    }

    @Override
    void visitChart(final BlockSpec.Width width) {
    }

    @Override
    void visitChartEnd(final UiChartSpecifier chartSpecifier) {
        visitCloseTitle()
        chartSpecifier.visitChart(new RawHtmlChartDump(out, ajaxBlockId))
        visitInnerColBlockEnd()
        visitInnerColBlockEnd()
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
    void visitInnerColBlockEnd() {
        out << "</div></div>"
    }

    @Override
    Map getParameterMap() {
        parameter.applicationTagLib.params
    }
}
