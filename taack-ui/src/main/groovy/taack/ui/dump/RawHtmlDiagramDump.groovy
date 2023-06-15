package taack.ui.dump

import groovy.transform.CompileStatic
import taack.ui.base.UiDiagramSpecifier
import taack.ui.base.diagram.DiagramTypeSpec
import taack.ui.base.diagram.IUiDiagramVisitor
import taack.ui.diagram.render.IDiagramRender
import taack.ui.diagram.render.PngDiagramRender
import taack.ui.diagram.render.SvgDiagramRender
import taack.ui.diagram.scene.BarDiagramScene

@CompileStatic
class RawHtmlDiagramDump implements IUiDiagramVisitor {
    final private ByteArrayOutputStream out
    final private String diagramId

    RawHtmlDiagramDump(final ByteArrayOutputStream out, final String diagramId) {
        this.out = out
        this.diagramId = diagramId
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
        } else if (diagramBase == UiDiagramSpecifier.DiagramBase.PNG) {
            (this.render as PngDiagramRender).writeImage(out)
        }
    }

    @Override
    void visitDiagramPreparation(List<String> xLabels, DiagramTypeSpec.HeightWidthRadio radio) {
        this.yDataPerKey = [:]
        this.xLabels = xLabels
        if (diagramBase == UiDiagramSpecifier.DiagramBase.SVG) {
            this.render = new SvgDiagramRender(1800.0, 1800.0 * radio.radio, true)
        } else if (diagramBase == UiDiagramSpecifier.DiagramBase.PNG) {
            this.render = new PngDiagramRender(1800.0, 1800.0 * radio.radio) // todo: adjust size for Pie (radio == 1)
        }
    }

    @Override
    void visitBarDiagram(boolean isStacked) {
        BarDiagramScene scene = new BarDiagramScene(render, xLabels, yDataPerKey, isStacked)
        scene.draw()
    }

    @Override
    void visitLineDiagram() {
//        yDataPerKey.each { Map.Entry<String, List<BigDecimal>> entry ->
//            List<BigDecimal> yDataList = entry.value
//            xLabels.eachWithIndex { String xLabel, int i ->
////                i < yDataList.size() ? yDataList[i] : 0, entry.key, xLabel
//            }
//        }

    }

    @Override
    void visitPieDiagram() {
//        BigDecimal total = yDataPerKey.values().collect { it.size() ? it.first() : 0 }.sum() as BigDecimal
//        yDataPerKey.each {
//            BigDecimal value = it.value.size() ? it.value.first() : 0
////            "${it.key}: ${value} (${(value / total * 100).round(2)}%)", value
//        }

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
