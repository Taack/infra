package taack.ui.mail.dump

import groovy.transform.CompileStatic
import taack.ui.base.UiChartSpecifier
import taack.ui.base.UiShowSpecifier
import taack.ui.base.UiTableSpecifier
import taack.ui.base.block.BlockSpec
import taack.ui.base.block.UiBlockVisitor
import taack.ui.base.common.ActionIcon
import taack.ui.base.common.Style
import taack.ui.base.helper.Utils
import taack.ui.dump.Parameter
import taack.ui.dump.RawHtmlChartDump

@CompileStatic
class RawHtmlMailDump extends UiBlockVisitor {
    private ByteArrayOutputStream out
    final Parameter parameter

    private String id

    private boolean hasTitle = false
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
    void visitInnerBlock(final String i18n, final BlockSpec.Width width) {
        if (!(!i18n || i18n.isEmpty())) {
            hasTitle = true
            out << """
                <div style="background-color: #50768d !important;width=100%">
                <div style="font-weight: bolder; font-size: larger;">${i18n}</div>
            """
        }
    }

    @Override
    void visitCloseTitle() {
        out << "</div>"
    }

    @Override
    void visitShow(final String i18n, final BlockSpec.Width width) {
        visitInnerBlock(null, width)
        visitInnerBlock(i18n, BlockSpec.Width.MAX)
    }

    @Override
    void visitShowEnd(final UiShowSpecifier uiShowSpecifier) {
        visitCloseTitle()
        if (uiShowSpecifier) uiShowSpecifier.visitShow(new RawHtmlShowDump(out, parameter))
        visitInnerBlockEnd()
        visitInnerBlockEnd()
    }

    @Override
    void visitHtmlBlock(String html, Style style) {
        out << """
            <div style="${style.labelCssStyleString}">${html}</div>
        """
    }

    @Override
    void visitTable(final String id, final String i18n, final BlockSpec.Width width) {
        this.id = id
        visitInnerBlock(null, width)
        visitInnerBlock(i18n, BlockSpec.Width.MAX)
    }

    @Override
    void visitTableEnd(UiTableSpecifier tableSpecifier) {
        visitCloseTitle()
        tableSpecifier.visitTable(new taack.ui.dump.RawHtmlTableDump(id, out, parameter))
        visitInnerBlockEnd()
        visitInnerBlockEnd()
    }

    @Override
    void visitChart(final String i18n, final BlockSpec.Width width) {
        visitInnerBlock(null, width)
        visitInnerBlock(i18n, BlockSpec.Width.MAX)
    }

    @Override
    void visitChartEnd(final UiChartSpecifier chartSpecifier) {
        visitCloseTitle()
        chartSpecifier.visitChart(new RawHtmlChartDump(out, ajaxBlockId))
        visitInnerBlockEnd()
        visitInnerBlockEnd()
    }

    @Override
    void visitCustom(final String i18n, final String html, Style style, final BlockSpec.Width width) {
        visitInnerBlock(null, width)
        visitInnerBlock(i18n, BlockSpec.Width.MAX)
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
    void visitInnerBlockEnd() {
        out << "</div></div>"
    }

    @Override
    void visitActionStart() {
        out << "<div class='icon right'>"
    }

    @Override
    void visitActionEnd() {
        out << "</div>"
    }

    @Override
    void visitAction(final String i18n, final ActionIcon actionIcon, final String controller, final String action, final Long id, Map<String, ? extends Object> params, boolean isAjaxRendering) {
        out << "<a href='https://$serverUrl/${controller}/${action}${id ? "/${id}" : ''}${params ? "?${Utils.paramsString params}'" : ''}'>${i18n}</a> "
    }

    @Override
    Map getParameterMap() {
        parameter.map
    }
}
