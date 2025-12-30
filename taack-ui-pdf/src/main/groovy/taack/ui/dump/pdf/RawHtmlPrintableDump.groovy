package taack.ui.dump.pdf

import groovy.transform.CompileStatic
import taack.ui.dsl.UiDiagramSpecifier
import taack.ui.dsl.UiShowSpecifier
import taack.ui.dsl.UiTableSpecifier
import taack.ui.dsl.block.BlockSpec
import taack.ui.dsl.printable.IUiPrintableVisitor
import taack.ui.dump.Parameter
import taack.ui.dump.RawHtmlDiagramDump
import taack.ui.dump.RawHtmlTableDump
import taack.ui.dump.common.BlockLog
import taack.ui.dump.diagram.scene.PieDiagramScene
import taack.ui.dump.html.layout.HTMLEmpty
import taack.ui.dump.html.theme.ThemeMode
import taack.ui.dump.html.theme.ThemeSelector
import taack.ui.dump.html.theme.ThemeSize

@CompileStatic
class RawHtmlPrintableDump implements IUiPrintableVisitor {
    final private ByteArrayOutputStream out
    final Parameter parameter

    private String id

    private boolean hasTitle = false
    private int hasPureG = 0
    private String ajaxBlockId = null
    String headerHeight = null

    private int tabOccurrence = 0
    final private BlockLog blockLog

    RawHtmlPrintableDump(ByteArrayOutputStream out, Parameter parameter) {
        this.blockLog = new BlockLog(new ThemeSelector(ThemeMode.LIGHT, ThemeMode.LIGHT, ThemeSize.NORMAL))
        this.blockLog.topElement = new HTMLEmpty()
        this.out = out
        this.parameter = parameter
    }

    @Override
    void visitShow(final UiShowSpecifier showSpecifier, final BlockSpec.Width width) {
        visitInnerBlock(width)
        showSpecifier.visitShow(new RawHtmlShowDump(out, parameter))
        visitInnerBlockEnd()
    }

    @Override
    void visitTable(UiTableSpecifier uiTableSpecifier, BlockSpec.Width width) {
        visitInnerBlock(width)
        this.blockLog.topElement = new HTMLEmpty() // TODO: Remove when no out
        uiTableSpecifier.visitTable(new RawHtmlTableDump(blockLog, 'pdf', parameter))
        blockLog.topElement.getOutput(out)
        visitInnerBlockEnd()

    }

    @Override
    void visitDiagram(UiDiagramSpecifier uiDiagramSpecifier, BlockSpec.Width width) {
        visitInnerBlock(width)
        ByteArrayOutputStream outb = new ByteArrayOutputStream(4096)
        RawHtmlDiagramDump d = new RawHtmlDiagramDump(outb)
        uiDiagramSpecifier.visitDiagram(d, UiDiagramSpecifier.DiagramBase.PNG)
        this.out << """<img width="680" height="${d.scene instanceof PieDiagramScene ? 680 : 340}" src='data:image/png;base64,${Base64.getEncoder().encodeToString(outb.toByteArray())}'/>"""
//        uiDiagramSpecifier.visitDiagram(new RawHtmlDiagramDump(outb), UiDiagramSpecifier.DiagramBase.SVG_PDF)
//        this.out << """<img src="data:image/svg+xml;base64,${Base64.getEncoder().encodeToString(outb.toByteArray())}"/>"""
        visitInnerBlockEnd()
    }

    @Override
    void visitCustom(final String html, final BlockSpec.Width width) {
        visitInnerBlock(width)
        out << html
        visitInnerBlockEnd()
    }

    @Override
    void visitPrintableHeader(final String height) {
        this.headerHeight = height
        out << '<div id="header" class="pure-g">'
    }

    @Override
    void visitPrintableHeaderLeft(String height) {
        this.headerHeight = height
        out << '<div id="header-left" class="pure-g">'
    }

    @Override
    void visitPrintableHeaderLeftEnd() {
        out << '</div>'
    }

    @Override
    void visitPrintableHeaderRight(String right) {
        out << '<div id="header-right" class="pure-g">'
    }

    @Override
    void visitPrintableHeaderRightEnd() {
        out << '</div>'
    }

    @Override
    void visitPrintableHeaderEnd() {
        out << '</div>'
    }

    @Override
    void visitPrintableFooter() {
        out << '<div id="footer">'
    }

    @Override
    void visitPrintableFooterEnd() {
        out << '</div>'
    }

    @Override
    void visitBlock() {

    }

    @Override
    void visitBlockEnd() {

    }

    @Override
    void visitAnonymousBlock(BlockSpec.Width width) {
        out << "<div class=\"${width.pdfCss}\">"
    }

    @Override
    void visitAnonymousBlockEnd() {
        out << '</div>'
    }

    @Override
    void visitInnerBlockEnd() {
        hasPureG--
        out << '</div>'
    }

    @Override
    void visitInnerBlock(BlockSpec.Width width) {
        out << "<div class=\"${width.pdfCss}\">"
    }

    @Override
    void visitPrintableBody() {
        out << '<div id=\'body\' class=\'pure-g\'>'
    }

    @Override
    void visitPrintableBodyEnd() {
        out << '</div>'
    }
}
