package taack.ui.dump

import groovy.transform.CompileStatic
import taack.ui.dsl.UiDiagramSpecifier
import taack.ui.dsl.diagram.DiagramTypeSpec
import taack.ui.dsl.diagram.IUiDiagramVisitor
import taack.ui.dump.diagram.IDiagramRender
import taack.ui.dump.diagram.PngDiagramRender
import taack.ui.dump.diagram.SvgDiagramRender
import taack.ui.dump.diagram.scene.AreaDiagramScene
import taack.ui.dump.diagram.scene.BarDiagramScene
import taack.ui.dump.diagram.scene.LineDiagramScene
import taack.ui.dump.diagram.scene.PieDiagramScene
import taack.ui.dump.pdf.SvgDiagramRenderPdf

@CompileStatic
class RawHtmlDiagramDump implements IUiDiagramVisitor {
    final private ByteArrayOutputStream out

    RawHtmlDiagramDump(final ByteArrayOutputStream out) {
        this.out = out
    }

    UiDiagramSpecifier.DiagramBase diagramBase
    private IDiagramRender render
    private List<String> xLabels
    private Map<String, List<BigDecimal>> yDataPerKey

    @Override
    void visitDiagram(UiDiagramSpecifier.DiagramBase diagramBase) {
        this.diagramBase = diagramBase
    }

    @Override
    void visitDiagramPreparation(List<String> xLabels, BigDecimal widthInPx, DiagramTypeSpec.HeightWidthRadio radio) {
        this.yDataPerKey = [:]
        this.xLabels = xLabels
        if (diagramBase == UiDiagramSpecifier.DiagramBase.SVG) {
            BigDecimal width = widthInPx ?: 960.0
            this.render = new SvgDiagramRender(width, width * radio.radio, widthInPx == null)
        } else if (diagramBase == UiDiagramSpecifier.DiagramBase.SVG_CSS21) {
            BigDecimal width = widthInPx ?: 720.0
            this.render = new SvgDiagramRenderPdf(width, width * radio.radio)
        } else if (diagramBase == UiDiagramSpecifier.DiagramBase.PNG) {
            BigDecimal width = widthInPx ?: 720.0
            this.render = new PngDiagramRender(width, width * radio.radio)
        }
    }

    @Override
    void dataset(String key, List<BigDecimal> data) {
        if (!xLabels.isEmpty()) {
            int labelSize = xLabels.size()
            int dataSize = data.size()
            if (dataSize < labelSize) {
                for (int i = 0; i < labelSize - dataSize; i++) {
                    data.add(0.0)
                }
            }
        }
        yDataPerKey.put(key, data)
    }

    @Override
    void visitBarDiagram(boolean isStacked) {
        BarDiagramScene scene = new BarDiagramScene(render, xLabels, yDataPerKey, isStacked)
        scene.draw()
    }

    @Override
    void visitLineDiagram() {
        LineDiagramScene scene = new LineDiagramScene(render, xLabels, yDataPerKey)
        scene.draw()
    }

    @Override
    void visitAreaDiagram(boolean isStacked) {
        AreaDiagramScene scene = new AreaDiagramScene(render, xLabels, yDataPerKey, isStacked)
        scene.draw()
    }

    @Override
    void visitPieDiagram() {
        PieDiagramScene scene = new PieDiagramScene(render, yDataPerKey)
        scene.draw()
    }

    @Override
    void visitDiagramEnd() {
        if (diagramBase == UiDiagramSpecifier.DiagramBase.SVG) {
            out << (this.render as SvgDiagramRender).getRendered()
        } else if (diagramBase == UiDiagramSpecifier.DiagramBase.SVG_CSS21) {
            out << (this.render as SvgDiagramRenderPdf).getRendered()
        } else if (diagramBase == UiDiagramSpecifier.DiagramBase.PNG) {
            (this.render as PngDiagramRender).writeImage(out)
        }
    }
}
