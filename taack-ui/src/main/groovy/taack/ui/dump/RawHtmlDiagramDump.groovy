package taack.ui.dump

import groovy.transform.CompileStatic
import taack.ui.base.diagram.DiagramTypeSpec
import taack.ui.base.diagram.IUiDiagramVisitor

@CompileStatic
class RawHtmlDiagramDump implements IUiDiagramVisitor {
    final private ByteArrayOutputStream out
    final private String diagramId

    RawHtmlDiagramDump(final ByteArrayOutputStream out, final String diagramId) {
        this.out = out
        this.diagramId = diagramId
    }

    enum DiagramBase {
        SVG,
        PNG
    }
    DiagramBase diagramBase = DiagramBase.SVG

    private BigDecimal diagramWidth
    private BigDecimal diagramHeight
    private List<String> xLabels
    private Map<String, List<BigDecimal>> yDataPerKey

    @Override
    void visitDiagram() {

    }

    @Override
    void visitDiagramEnd() {
        if (diagramBase == DiagramBase.SVG) {

        } else if (diagramBase == DiagramBase.PNG) {

        }
    }

    @Override
    void visitBarDiagram(List<String> xLabels, DiagramTypeSpec.HeightWidthRadio radio) {
        this.xLabels = xLabels
        this.diagramWidth = 480.0 / radio.radio
        this.diagramHeight = 480.0
        this.yDataPerKey = [:]
    }

    @Override
    void visitBarDiagramEnd(String xTitle, String yTitle, boolean isStacked) {
        yDataPerKey.each { Map.Entry<String, List<BigDecimal>> entry ->
            List<BigDecimal> yDataList = entry.value
            xLabels.eachWithIndex { String xLabel, int i ->
//                i < yDataList.size() ? yDataList[i] : 0, entry.key, xLabel
            }
        }

    }

    @Override
    void visitLineDiagram(List<String> xLabels, DiagramTypeSpec.HeightWidthRadio radio) {
        this.xLabels = xLabels
        this.diagramWidth = 480.0 / radio.radio
        this.diagramHeight = 480.0
        this.yDataPerKey = [:]
    }

    @Override
    void visitLineDiagramEnd(String xTitle, String yTitle) {
        yDataPerKey.each { Map.Entry<String, List<BigDecimal>> entry ->
            List<BigDecimal> yDataList = entry.value
            xLabels.eachWithIndex { String xLabel, int i ->
//                i < yDataList.size() ? yDataList[i] : 0, entry.key, xLabel
            }
        }

    }

    @Override
    void visitPieDiagram(DiagramTypeSpec.HeightWidthRadio radio) {
        this.xLabels = []
        this.diagramWidth = 480.0 / radio.radio
        this.diagramHeight = 480.0
        this.yDataPerKey = [:]
    }

    @Override
    void visitPieDiagramEnd() {
        BigDecimal total = yDataPerKey.values().collect { it.size() ? it.first() : 0 }.sum() as BigDecimal
        yDataPerKey.each {
            BigDecimal value = it.value.size() ? it.value.first() : 0
//            "${it.key}: ${value} (${(value / total * 100).round(2)}%)", value
        }

    }

    @Override
    void dataset(String key, List<BigDecimal> data) {
        yDataPerKey.put(key, data)
    }
}
