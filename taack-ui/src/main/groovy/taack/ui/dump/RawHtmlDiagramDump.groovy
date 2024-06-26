package taack.ui.dump

import groovy.transform.CompileStatic
import taack.ui.dsl.UiDiagramSpecifier
import taack.ui.dsl.block.BlockSpec
import taack.ui.dsl.diagram.DiagramTypeSpec
import taack.ui.dsl.diagram.IUiDiagramVisitor
import taack.ui.dump.diagram.IDiagramRender
import taack.ui.dump.diagram.PngDiagramRender
import taack.ui.dump.diagram.SvgDiagramRender
import taack.ui.dump.diagram.scene.BarDiagramScene
import taack.ui.dump.diagram.scene.LineDiagramScene
import taack.ui.dump.diagram.scene.PieDiagramScene
import taack.ui.dump.pdf.SvgDiagramRenderPdf

@CompileStatic
class RawHtmlDiagramDump implements IUiDiagramVisitor {
    final private ByteArrayOutputStream out
    final private String diagramId
    final private BlockSpec.Width blockSpecWidth

    RawHtmlDiagramDump(final ByteArrayOutputStream out, final String diagramId, final BlockSpec.Width blockSpecWidth) {
        this.out = out
        this.diagramId = diagramId
        this.blockSpecWidth = blockSpecWidth
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
    void visitDiagramEnd() {
        if (diagramBase == UiDiagramSpecifier.DiagramBase.SVG) {
            out << (this.render as SvgDiagramRender).getRendered()
        } else if (diagramBase == UiDiagramSpecifier.DiagramBase.SVG_CSS21) {
            out << (this.render as SvgDiagramRenderPdf).getRendered()
        } else if (diagramBase == UiDiagramSpecifier.DiagramBase.PNG) {
            (this.render as PngDiagramRender).writeImage(out)
        }
    }

    @Override
    void visitDiagramPreparation(List<String> xLabels, DiagramTypeSpec.HeightWidthRadio radio) {
        this.yDataPerKey = [:]
        this.xLabels = xLabels
        BigDecimal width
        switch (blockSpecWidth) {
            case BlockSpec.Width.THREE_QUARTER: width = 1800.0 * 3 / 4; break
            case BlockSpec.Width.TWO_THIRD: width = 1800.0 * 2 / 3; break
            case BlockSpec.Width.HALF: width = 1800.0 / 2; break
            case BlockSpec.Width.THIRD: width = 1800.0 / 3; break
            case BlockSpec.Width.QUARTER: width = 1800.0 / 4; break
            default: width = 1800.0; break
        }
        if (diagramBase == UiDiagramSpecifier.DiagramBase.SVG) {
            this.render = new SvgDiagramRender(width, width * radio.radio, true)
        } else if (diagramBase == UiDiagramSpecifier.DiagramBase.SVG_CSS21) {
            this.render = new SvgDiagramRenderPdf(width, width * radio.radio)
        } else if (diagramBase == UiDiagramSpecifier.DiagramBase.PNG) {
            this.render = new PngDiagramRender(width, width * radio.radio)
        }
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
    void visitPieDiagram() {
        PieDiagramScene scene = new PieDiagramScene(render, yDataPerKey)
        scene.draw()
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
}
