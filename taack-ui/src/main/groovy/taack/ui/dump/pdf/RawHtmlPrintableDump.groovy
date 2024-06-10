package taack.ui.dump.pdf

import groovy.transform.CompileStatic
import taack.ui.dsl.UiShowSpecifier
import taack.ui.dsl.UiTableSpecifier
import taack.ui.dsl.block.BlockSpec
import taack.ui.dsl.printable.IUiPrintableVisitor
import taack.ui.dump.Parameter

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

    RawHtmlPrintableDump(final ByteArrayOutputStream out, Parameter parameter) {
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
    void visitTable(UiTableSpecifier tableSpecifier, final BlockSpec.Width width) {
        visitInnerBlock(width)
        tableSpecifier.visitTable(new RawHtmlTableDump(out, parameter))
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
        out << """<div id="header" class="pure-g">"""
    }

    @Override
    void visitPrintableHeaderLeft(String height) {
        this.headerHeight = height
        out << """<div id="header-left" class="pure-g">"""
    }

    @Override
    void visitPrintableHeaderLeftEnd() {
        out << "</div>"
    }

    @Override
    void visitPrintableHeaderRight(String right) {
        out << """<div id="header-right" class="pure-g">"""
    }

    @Override
    void visitPrintableHeaderRightEnd() {
        out << "</div>"
    }

    @Override
    void visitPrintableHeaderEnd() {
        out << "</div>"
    }

    @Override
    void visitPrintableFooter() {
        out << """<div id="footer">"""
    }

    @Override
    void visitPrintableFooterEnd() {
        out << "</div>"
    }

    @Override
    void visitBlock() {

    }

    @Override
    void visitBlockEnd() {

    }

    @Override
    void visitAnonymousBlock(BlockSpec.Width width) {
        out << """<div class="${width.pdfCss}">"""
    }

    @Override
    void visitAnonymousBlockEnd() {
        out << "</div>"
    }

    @Override
    void visitInnerBlockEnd() {
        hasPureG--
        out << "</div>"
    }

    @Override
    void visitInnerBlock(BlockSpec.Width width) {
        out << """<div class="${width.pdfCss}">"""
    }

    @Override
    void visitPrintableBody() {
        out << "<div id=\"body\" class=\"pure-g\">"
    }

    @Override
    void visitPrintableBodyEnd() {
        out << "</div>"
    }
}
