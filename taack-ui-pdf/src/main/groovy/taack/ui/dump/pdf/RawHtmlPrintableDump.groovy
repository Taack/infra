package taack.ui.dump.pdf

import groovy.transform.CompileStatic
import taack.ui.dsl.UiDiagramSpecifier
import taack.ui.dsl.UiPrintableSpecifier
import taack.ui.dsl.UiShowSpecifier
import taack.ui.dsl.UiTableSpecifier
import taack.ui.dsl.block.BlockSpec
import taack.ui.dsl.printable.IUiPrintableVisitor
import taack.ui.dump.Parameter
import taack.ui.dump.RawHtmlDiagramDump
import taack.ui.dump.RawHtmlTableDump
import taack.ui.dump.common.BlockLog
import taack.ui.dump.html.layout.HTMLEmpty
import taack.ui.dump.html.theme.ThemeMode
import taack.ui.dump.html.theme.ThemeSelector
import taack.ui.dump.html.theme.ThemeSize

import javax.imageio.ImageIO
import java.util.regex.Matcher
import java.awt.image.BufferedImage

@CompileStatic
class RawHtmlPrintableDump implements IUiPrintableVisitor {
    // PNG diagrams are rendered at least twice their display size; a small diagram is never enlarged beyond it.
    private static final BigDecimal DIAGRAM_MAX_RATIO = 0.5
    private static final BigDecimal DEFAULT_HEADER_HEIGHT_CM = 5.0
    private static final BigDecimal CSS_PX_PER_CM = 96.0 / 2.54

    final private ByteArrayOutputStream out
    final Parameter parameter

    private String id

    private boolean hasTitle = false
    private int hasPureG = 0
    private String ajaxBlockId = null
    String headerHeight = null

    private int tabOccurrence = 0
    final private BlockLog blockLog
    final private UiPrintableSpecifier.Orientation orientation

    RawHtmlPrintableDump(ByteArrayOutputStream out, Parameter parameter, UiPrintableSpecifier.Orientation orientation = UiPrintableSpecifier.Orientation.PORTRAIT) {
        this.orientation = orientation
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
        byte[] png = outb.toByteArray()
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(png))
        BigDecimal ratio = [orientation.diagramMaxWidth / image.width, diagramMaxHeight() / image.height, DIAGRAM_MAX_RATIO].min()
        this.out << """<img style="display: block; margin: 0 auto;" width="${(image.width * ratio).toInteger()}" height="${(image.height * ratio).toInteger()}" src='data:image/png;base64,${Base64.getEncoder().encodeToString(png)}'/>"""
//        uiDiagramSpecifier.visitDiagram(new RawHtmlDiagramDump(outb), UiDiagramSpecifier.DiagramBase.SVG_PDF)
//        this.out << """<img src="data:image/svg+xml;base64,${Base64.getEncoder().encodeToString(outb.toByteArray())}"/>"""
        visitInnerBlockEnd()
    }

    /**
     * Orientation.diagramMaxHeight fits below the default header of the template; a header height given in cm or mm moves that limit.
     */
    private BigDecimal diagramMaxHeight() {
        BigDecimal headerHeightCm = cssLengthInCm(headerHeight)
        headerHeightCm == null ? orientation.diagramMaxHeight : orientation.diagramMaxHeight + (DEFAULT_HEADER_HEIGHT_CM - headerHeightCm) * CSS_PX_PER_CM
    }

    private static BigDecimal cssLengthInCm(String length) {
        Matcher m = (length ?: '') =~ /^\s*([0-9.]+)\s*(cm|mm)\s*$/
        if (!m.matches()) return null
        BigDecimal value = new BigDecimal(m.group(1))
        m.group(2) == 'mm' ? value / 10 : value
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
